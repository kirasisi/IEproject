package ff.ecochallenges;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ff.ecochallenges.game.Global;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "mainnotif";
    private FragmentManager fragmentManager;
    private Fragment challengeFragment;
    private DatabaseReference db;
    private SharedPreferences myPreferences;
    private BottomNavigationView navigation;


    Fragment newFragment = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homef();


                    return true;
                case R.id.navigation_challenge:
                    challengef();

                    return true;
                case R.id.navigation_explore:
                    exploref();
                    return true;
                case R.id.navigation_garden:
                    Intent intent = new Intent(getApplicationContext(), garden.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    public void homef() {//swith to fragment
        newFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
    }

    public void challengef() {
        try {
            getSignIn();
        } catch (Exception e) {
        }
    }

    public void exploref() {
        newFragment = new ExploreFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Check if uid is set
        if (myPreferences.getString("uid", null) == null) {
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("uid", UUID.randomUUID().toString());
            myEditor.commit();
        }
        setContentView(R.layout.activity_main);

        newFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
        fragmentManager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_home);

        //Check if app is launched through notification
        String action = getIntent().getAction();
        if (action != null && action.equals("challenge"))
        {
            navigation.setSelectedItemId(R.id.navigation_challenge);
        }

    }


    public void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onStart() {
        super.onStart();
        createNotificationChannel();

    }

    @Override
    public void onResume() {

        super.onResume();
        //Check if garden mini-game is exited to select home screen from navigation menu
        if (navigation.getSelectedItemId() == R.id.navigation_garden)
        navigation.setSelectedItemId(R.id.navigation_home);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifs";
            String description = "Main notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void onStop() {
        super.onStop();
        //Set the timer for the notification
        setReminder(this, AlarmReceiver.class, 8, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getSignIn() {
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.orderByKey().equalTo(myPreferences.getString("uid", null))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            boolean same = checkSignIn(dataSnapshot.child("lastLoginDate").getValue(String.class));
                            challengeFragment = new ChallengeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("signInDateCheck", same);
                            challengeFragment.setArguments(bundle);
                            loadFragment(challengeFragment);
                        } else {
                            challengeFragment = new ChallengeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("signInDateCheck", false);
                            challengeFragment.setArguments(bundle);
                            loadFragment(challengeFragment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public boolean checkSignIn(String siginDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date DATE = new Date();
        String nowDate = formatter.format(DATE);
        if (siginDate == nowDate) {
            return true;
        } else {
            return false;
        }
    }

    public static void setReminder(Context context, Class<?> cls, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);
        // cancel already scheduled reminders
        //cancelReminder(context, cls);

        if (setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE, 1);

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                54321, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}


