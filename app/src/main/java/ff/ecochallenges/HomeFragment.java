package ff.ecochallenges;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private TextView uid;
    private DatabaseReference mDatabase;
    private int point=0;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String uName = "";
        View vHome = inflater.inflate(R.layout.fragment_home, container, false);
        uid = (TextView)vHome.findViewById(R.id.uID);
        TextView challengeCount = (TextView)vHome.findViewById(R.id.challengeCount);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account!=null){
            uName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            uid.setText(uName);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

            mDatabase.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date DATE = new Date();
                            if(dataSnapshot.exists()){
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points").setValue(point);
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastLoginDate").setValue(formatter.format(DATE));

                            } else {
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points").setValue(point);
                                Calendar sCalendar = Calendar.getInstance();
                                String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                                int date = sCalendar.get(Calendar.DATE);
                                if (day.equals("Saturday")||day.equals("Sunday")){
                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekendChallenge").setValue("1");
                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekdayChallenge").setValue("0");
                                }
                                else{
                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekdayChallenge").setValue("1");
                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekendChallenge").setValue("0");
                                }
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isCompleted").setValue("false");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastLoginDate").setValue(formatter.format(DATE));


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }







        return vHome;
    }
    public void onAcitivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }
}