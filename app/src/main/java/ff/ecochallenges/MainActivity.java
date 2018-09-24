package ff.ecochallenges;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import com.ff.garden.UnityPlayerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "test123";
    private FragmentManager fragmentManager;
    private Fragment challengeFragment;
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private SharedPreferences myPreferences;



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

    public void exploref(){
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
        mAuth = FirebaseAuth.getInstance();
        newFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
        fragmentManager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        //sendNotification();

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void onStop()
    {
        super.onStop();
        startService(new Intent(this, NotificationService.class));
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
}


