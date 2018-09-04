package ff.ecochallenges;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class HomeFragment extends Fragment {
    private TextView uid;
    private DatabaseReference mDatabase;
    private DatabaseReference counterData;
    private int point = 0;
    private TextView challengeCount;
    private TextView nutsPoint;
    private TextView currentMonthTotal;
    private SharedPreferences myPreferences;
    private String userUID = null;
    private Handler repeatUpdateHandler = new Handler();
    private double currentWasteTotal;
    private double currentWasteIncrement;
    private int counterSearchKey;
    Calendar currentCal = Calendar.getInstance();
    Calendar yearStart = Calendar.getInstance();


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userUID = myPreferences.getString("uid", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vHome = inflater.inflate(R.layout.fragment_home, container, false);
        uid = (TextView) vHome.findViewById(R.id.uID);
        challengeCount = (TextView) vHome.findViewById(R.id.challengeCount);
        nutsPoint = (TextView) vHome.findViewById(R.id.pointYouHave);
        currentMonthTotal = vHome.findViewById(R.id.currentMonthTotal);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.orderByKey().equalTo(userUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date DATE = new Date();
                        if (dataSnapshot.exists()) {

                            Log.i("TAG", "already exist");
                        } else {
                            mDatabase.child(userUID);
                            mDatabase.child(userUID).child("points").setValue(point);
                            Calendar sCalendar = Calendar.getInstance();
                            String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                            if (day.equals("Saturday") || day.equals("Sunday")) {
                                mDatabase.child(userUID).child("currentWeekendChallenge").setValue("1");
                                mDatabase.child(userUID).child("currentWeekdayChallenge").setValue("0");
                            } else {
                                mDatabase.child(userUID).child("currentWeekdayChallenge").setValue("1");
                                mDatabase.child(userUID).child("currentWeekendChallenge").setValue("0");
                            }
                            mDatabase.child(userUID).child("isCompleted").setValue("false");
                            mDatabase.child(userUID).child("lastLoginDate").setValue(formatter.format(DATE));
                            mDatabase.child(userUID).child("challengeFinished").setValue("0");
                            challengeCount.setText("You have done 0 challenges so far!");
                            nutsPoint.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        try {
            mDatabase.orderByKey().equalTo(userUID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Object challenge = dataSnapshot.child("challengeFinished").getValue();
                    Object nuts = dataSnapshot.child("points").getValue();
                    try {
                        challengeCount.setText("You have done " + challenge.toString() + " challenge(s) so far!");
                        nutsPoint.setText(nuts.toString());
                    } catch (Exception e) {
                        Log.i("Exception", "Null");
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            Log.i("Notice", "New user");
        }

        //Begin setting up counter

        counterData = FirebaseDatabase.getInstance().getReference().child("ODWasteCounterTotals");
        //Calendar currentCal = Calendar.getInstance();
        //Calendar yearStart = Calendar.getInstance();
        yearStart.set(currentCal.get(YEAR),0,1,0,0,0);
        counterSearchKey = currentCal.get(YEAR);
        //Check predicted total
        counterData.orderByChild("year").equalTo(counterSearchKey)
                .addChildEventListener(new ChildEventListener() {


                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        double predictedTotal = dataSnapshot.child("totalGenerated").getValue(Double.class);
                        currentWasteIncrement = (((predictedTotal/365)/86400000)*50);
                        currentWasteTotal = ((currentCal.getTimeInMillis() - yearStart.getTimeInMillis())/50*currentWasteIncrement);
                        DecimalFormat df = new DecimalFormat("#.##");
                        currentMonthTotal.setText(df.format(currentWasteTotal)+" tonnes");
                        repeatUpdateHandler.post( new RptUpdater() );
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //long test = (currentCal.getTimeInMillis() - yearStart.getTimeInMillis());


        return vHome;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    class RptUpdater implements Runnable {
        public void run() {
            currentWasteTotal += currentWasteIncrement;
            DecimalFormat df = new DecimalFormat("#.##");
            currentMonthTotal.setText(df.format(currentWasteTotal)+" tonnes");
            repeatUpdateHandler.postDelayed( new RptUpdater(), 50 );
        }
    }
}