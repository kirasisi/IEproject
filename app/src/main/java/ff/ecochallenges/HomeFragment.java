package ff.ecochallenges;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import ff.ecochallenges.game.Global;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class HomeFragment extends Fragment {
    private TextView uid;
    private DatabaseReference mDatabase;
    private DatabaseReference counterData;
    private int point = 0;
    private TextView challengeCount;
    private TextView nutsPoint;
    private Button currentMonthTotal;
    private TextView counterLabel;
    private ProgressBar homeProgressBar;
    private SharedPreferences myPreferences;
    private String userUID = null;
    private Handler repeatUpdateHandler = new Handler();
    private double currentWasteTotal;
    private double currentWasteIncrement;
    private double predictedTotal;
    private String counterSearchKey;
    private WaveView wasteAnimation;
    private ImageView info;
    Calendar currentCal = Calendar.getInstance();
    Calendar monthStart = Calendar.getInstance();


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
        //previousMonthTotal = vHome.findViewById(R.id.previousMonthTotal);
        //nextMonthTotal = vHome.findViewById(R.id.nextMonthTotal);
        homeProgressBar = vHome.findViewById(R.id.homeProgressBar);
        wasteAnimation = vHome.findViewById(R.id.waveProgress);
        info = vHome.findViewById(R.id.info);
        counterLabel = vHome.findViewById(R.id.counterLabel);
        Global g = Global.getInstance();
        g.setId(userUID);

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
                            nutsPoint.setText("Your Points: 0 ");
                            Global g = Global.getInstance();
                            g.setData(0);

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
                        nutsPoint.setText("Your Points: "+nuts.toString()+" ");
                        Global g = Global.getInstance();
                        g.setData(Integer.parseInt(nuts.toString()));

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
        monthStart.set(currentCal.get(YEAR),currentCal.get(MONTH),1,0,0,0);
        counterSearchKey = currentCal.get(YEAR)+"-"+(currentCal.get(MONTH)+1);
        //Check predicted total
        counterData.orderByKey().equalTo(counterSearchKey)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        predictedTotal = dataSnapshot.child("Total").getValue(Double.class);
                        currentWasteIncrement = (((predictedTotal/30)/86400000)*250);
                        currentWasteTotal = (((currentCal.getTimeInMillis() - monthStart.getTimeInMillis())/250)*currentWasteIncrement);
                        DecimalFormat df = new DecimalFormat("#,###");
                        currentMonthTotal.setText(df.format(currentWasteTotal)+"");
                        wasteAnimation.setProgress((int) (Math.round((currentWasteTotal/predictedTotal)*100)));
                        //Finished loading page, stop load animation
                        homeProgressBar.setVisibility(View.GONE);
                        counterLabel.setVisibility(View.VISIBLE);
                        currentMonthTotal.setVisibility(View.VISIBLE);
                        wasteAnimation.setVisibility(View.VISIBLE);
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

        currentMonthTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),popChart.class);
                    intent.putExtra("ctg","maintrend");
                    startActivity(intent);
                }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),popChart.class);
                intent.putExtra("origin","home");
                startActivity(intent);
            }
        });
        return vHome;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    class RptUpdater implements Runnable {
        public void run() {
            currentWasteTotal += currentWasteIncrement;
            DecimalFormat df = new DecimalFormat("#,###");
            currentMonthTotal.setText(df.format(currentWasteTotal)+"");
            wasteAnimation.setProgress((int) (Math.round((currentWasteTotal/predictedTotal)*100)));
            repeatUpdateHandler.postDelayed( new RptUpdater(), 250 );
        }
    }

    public void uploadPoint(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.orderByKey().equalTo(userUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Global g = Global.getInstance();
                int point = g.getData();
                try {
                    mDatabase.child(userUID).child("points")
                            .setValue(point);

                } catch (Exception e) {

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
    }


}