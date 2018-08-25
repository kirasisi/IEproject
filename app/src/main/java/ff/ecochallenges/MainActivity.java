package ff.ecochallenges;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private String ChallengeName = "";
    private String URI = "";
    private Bundle chBundle;


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
//                    Intent chActivity = new Intent(MainActivity.this,Challenges.class);
//                    startActivity(chActivity);
                    challengef();

                    return true;
                case R.id.navigation_garden:

                    return true;
            }


            return false;
        }

    };

    public void homef(){//swith to fragment
        newFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();
    }

    public void challengef(){
        FragmentManager manager = getSupportFragmentManager();
        ChallengeFragment cf = new ChallengeFragment();
        manager.beginTransaction().replace(R.id.frame_layout, cf).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        //newFragment = new HomeFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);


//        mDatabase = FirebaseDatabase.getInstance().getReference("Challenges");
//        Calendar sCalendar = Calendar.getInstance();
//        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
//        int date = sCalendar.get(Calendar.DATE);
//        DatabaseReference todaysChallenge = mDatabase.child("c" + String.valueOf(date));
//
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        //urlImage = "https://firebasestorage.googleapis.com/v0/b/ecochallenge-214211.appspot.com/o/challenge%2Fbottle-drink-glass-113734.jpg?alt=media&token=a50b1eeb-cd08-467a-869f-50acc234b8ee";
//
//
//        todaysChallenge.addValueEventListener(new ValueEventListener() {
//            public String TAG;
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ChallengeName = dataSnapshot.child("cName").getValue(String.class);
//
//                URI = dataSnapshot.child("cPic").getValue(String.class);
//                chBundle = new Bundle();
//                chBundle.putString("cName",ChallengeName);
//                chBundle.putString("cPic",URI);
//
//                Log.i("image",URI);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w(TAG, "onCancelled", databaseError.toException());
//            }
//        });
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        newFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, newFragment).commit();



    }

}
