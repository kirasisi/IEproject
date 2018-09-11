package ff.ecochallenges;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChallengeFragment extends Fragment {

    private SharedPreferences myPreferences;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private TextView cName;
    private ImageView cPic;
    private String urlImage;
    private ImageView completeSign;
    private TextView point;
    private Button completeBtn;
    private TextView completeText;
    private TextView instructContent;
    private DatabaseReference todaysChallenge;
    private DatabaseReference nextDayChallenge;
    private TextView nextChallenge;
    private RelativeLayout worthNuts;
    private int t;
    private int date = 0;
    private boolean check = false;
    boolean completed = false;
    private TextView tip;
    private String userUID = null;
    private ImageView openPop;
    private String tp;
    private RelativeLayout cf;


    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userUID = myPreferences.getString("uid", null);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vChallenge = inflater.inflate(R.layout.fragment_challenge, container, false);
        //create view
        cName = vChallenge.findViewById(R.id.cName);
        cPic = vChallenge.findViewById(R.id.challengePic);
        completeSign = vChallenge.findViewById(R.id.completeIcon);
        completeSign.setVisibility(getView().GONE);
        completeText = vChallenge.findViewById(R.id.completeText);
        point = vChallenge.findViewById(R.id.points);
        completeBtn = vChallenge.findViewById(R.id.c_complete);
        instructContent = vChallenge.findViewById(R.id.insContent);
        nextChallenge = vChallenge.findViewById(R.id.nextChallenge);
        nextChallenge.setVisibility(View.GONE);
        tip = vChallenge.findViewById(R.id.tips);
        tip.setVisibility(View.GONE);
        openPop = (ImageView) vChallenge.findViewById(R.id.toOpenPopUp);
        worthNuts = vChallenge.findViewById(R.id.worthNuts);
        cf = vChallenge.findViewById(R.id.challengefragment);






        getSignIn();
        checkCompletion();

        openPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),popChart.class);
                intent.putExtra("type",tp);
                startActivity(intent);
                //cf.setAlpha(0.3f);

            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChallengeResult();
                completeBtn.setVisibility(getView().GONE);
                worthNuts.setVisibility(View.GONE);
                completeText.setText("Well Done!");
                updateCompeletion();
                displayNextChallenge();

                check = true;
                tip.setVisibility(View.VISIBLE);
                instructContent.setVisibility(View.GONE);
                updatePoint();


            }

        });
        return vChallenge;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("date", date);
        outState.putBoolean("check", check);

        Log.e("TAG", "onSaveInstanceState OK");
        Log.e("TAG", "onSaveInstanceState" + outState.getInt("date"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void updatePoint() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.orderByKey().equalTo(userUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Object points = null;
                Object challengeNum = null;
                points = dataSnapshot.child("points").getValue();
                challengeNum = dataSnapshot.child("challengeFinished").getValue();

                try {
                    mDatabase.child(userUID).child("points")
                            .setValue(Integer.parseInt(points.toString()) + Integer.parseInt(point.getText().toString()));
                    mDatabase.child(userUID).child("challengeFinished")
                            .setValue(Integer.parseInt(challengeNum.toString()) + 1);

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

    public void updateCompeletion() {
        completeSign.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase.child(userUID).child("isCompleted").setValue("true");
                completed = true;
                checkCompletion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void saveChallengeResult() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChallengeHistory");
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase2.equalTo(userUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Calendar sCalendar = Calendar.getInstance();
                            String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                            date = sCalendar.get(Calendar.DATE);
                            if (day.equals("Saturday") || day.equals("Sunday")) {
                                mDatabase2.child("currentWeekendChallenge").setValue(getStamp());
                            } else {
                                mDatabase2.child("currentWeekdayChallenge").setValue(getStamp());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public void checkCompletion() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.orderByKey().equalTo(userUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Object isCompleted = null;
                        Calendar sCalendar = Calendar.getInstance();
                        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                        date = sCalendar.get(Calendar.DATE);
                        Object stamp = null;

                        isCompleted = dataSnapshot.child("isCompleted").getValue();
                        if (day.equals("Saturday") || day.equals("Sunday")) {
                            stamp = dataSnapshot.child("currentWeekendChallenge").getValue();
                        } else {
                            stamp = dataSnapshot.child("currentWeekdayChallenge").getValue();
                        }

                        Log.i("COMPLETE", isCompleted.toString());
                        if (isCompleted.equals("true")) {
                            completeBtn.setVisibility(View.GONE);
                            worthNuts.setVisibility(View.GONE);
                            completeText.setVisibility(View.VISIBLE);
                            completeText.setText("Well Done!");
                            completeSign.setVisibility(View.VISIBLE);
                            tip.setVisibility(View.VISIBLE);
                            instructContent.setVisibility(View.GONE);
                            setStamp(Integer.parseInt(stamp.toString()) + 1);
                            Log.i("STAMP", String.valueOf(getStamp()));
                            displayNextChallenge();
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

    public void displayNextChallenge() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("WeekdayChallenges");
        if (getStamp() == 0) {

            nextChallenge.setText("Enjoy your Weekend with the big challenge!");

        } else {
            nextDayChallenge = mDatabase.child(String.valueOf(getStamp()));
            nextDayChallenge.addValueEventListener(new ValueEventListener() {
                public String TAG;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("cName").getValue(String.class);
                    nextChallenge.setText("Tomorrow's challenge: " + name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });
        }
        nextChallenge.setVisibility(View.VISIBLE);
    }


    public void changeChallenge() {
        Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        date = sCalendar.get(Calendar.DATE);

        //check week day or weekend
        if (day.equals("Saturday") || day.equals("Sunday")) {
            mDatabase2 = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase2.orderByKey().equalTo(userUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String last = dataSnapshot.child(userUID).child("currentWeekendChallenge").getValue(String.class);
                        int current = Integer.parseInt(last);
                        if (current < 4) {
                            todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf(current));

                            updateChallengeUI();
                            setStamp(current + 1);
                            completeBtn.setVisibility(View.VISIBLE);
                            worthNuts.setVisibility(View.VISIBLE);
                            completeText.setVisibility(View.GONE);
                            nextChallenge.setVisibility(View.GONE);
                            tip.setVisibility(View.GONE);
                            instructContent.setVisibility(View.VISIBLE);
                            cName.setVisibility(View.VISIBLE);
                            completeSign.setVisibility(View.GONE);

                        } else {
                            todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child("1");

                            updateChallengeUI();
                            setStamp(1);
                            completeBtn.setVisibility(View.VISIBLE);
                            worthNuts.setVisibility(View.VISIBLE);
                            completeText.setVisibility(View.GONE);
                            nextChallenge.setVisibility(View.GONE);
                            tip.setVisibility(View.GONE);
                            instructContent.setVisibility(View.VISIBLE);
                            cName.setVisibility(View.VISIBLE);
                            completeSign.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            //connect to firebase
            mDatabase2 = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase2.orderByKey().equalTo(userUID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String last = dataSnapshot.child(userUID).child("currentWeekdayChallenge").getValue(String.class);
                        int current = Integer.parseInt(last);
                        if (current <= 26) {
                            todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(String.valueOf(current));

                            updateChallengeUI();
                            setStamp(current + 1);
                            completeBtn.setVisibility(View.VISIBLE);
                            completeText.setVisibility(View.GONE);
                            nextChallenge.setVisibility(View.GONE);
                            tip.setVisibility(View.GONE);
                            instructContent.setVisibility(View.VISIBLE);
                            cName.setVisibility(View.VISIBLE);
                            completeSign.setVisibility(View.GONE);
                        } else {
                            todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child("1");
                            updateChallengeUI();
                            setStamp(1);
                            completeBtn.setVisibility(View.VISIBLE);
                            completeText.setVisibility(View.GONE);
                            nextChallenge.setVisibility(View.GONE);
                            tip.setVisibility(View.GONE);
                            instructContent.setVisibility(View.VISIBLE);
                            cName.setVisibility(View.VISIBLE);
                            completeSign.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    public void keepChallenge() {
        Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        date = sCalendar.get(Calendar.DATE);
        mDatabase2 = FirebaseDatabase.getInstance().getReference("Users");
        //check week day or weekend
        if (day.equals("Saturday") || day.equals("Sunday")) {
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(userUID).child("currentWeekendChallenge").getValue(String.class);
                    int current = Integer.parseInt(last);
                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf(current));
                    updateChallengeUI();
                    setStamp(current + 1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            //connect to firebase
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(userUID).child("currentWeekdayChallenge").getValue(String.class);
                    int current = Integer.parseInt(last);
                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(String.valueOf(current));
                    updateChallengeUI();
                    setStamp(current);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    public void setType(String t){
        tp = t;
    }

    public void updateChallengeUI() {
        todaysChallenge.addValueEventListener(new ValueEventListener() {
            public String TAG;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                urlImage = dataSnapshot.child("cPic").getValue(String.class);
                cPic.setAlpha(0.5f);
                Glide.with(getActivity().getApplicationContext())
                        .load(urlImage)
                        .into(cPic);
                String name = dataSnapshot.child("cName").getValue(String.class);
                cName.setText(name);
                String description = dataSnapshot.child("description").getValue(String.class);
                instructContent.setText(description);
                Long points = dataSnapshot.child("points").getValue(Long.class);
                point.setText(String.valueOf(points));
                String type1 = dataSnapshot.child("type").getValue(String.class);
                updateTips(type1);
                setType(type1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });


    }

    public void setStamp(int today) {
        t = today;
    }

    public int getStamp() {
        return t;
    }

    public void updateTips(String type) {
        mDatabase3 = FirebaseDatabase.getInstance().getReference("ODWasteAvgs");
        mDatabase3.orderByChild("type").equalTo(type)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            Object avg = dataSnapshot.child("avgDailyPerCapita").getValue();
                            Object type = dataSnapshot.child("type").getValue();
                            Object unit = dataSnapshot.child("unit").getValue();
                            tip.setText("The daily average generation per capita of " + type + " waste in Australia is " + String.valueOf(avg) + " " + unit);
                        } else {
                            tip.setText("tip");
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


    public void getSignIn() {

        final DatabaseReference fdb = FirebaseDatabase.getInstance().getReference().child("Users");
        fdb.orderByKey().equalTo(userUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        //{
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date DATE = new Date();
                            Object last = dataSnapshot.child("lastLoginDate").getValue();

                            if (last.toString().equals(formatter.format(DATE))) {
                                fdb.child(userUID).child("lastLoginDate").setValue(formatter.format(DATE));
                                keepChallenge();
                            } else {

                                fdb.child(userUID).child("isCompleted").setValue("false");
                                completed = false; //Tarek
                                fdb.child(userUID).child("lastLoginDate").setValue(formatter.format(DATE));
                                Calendar sCalendar = Calendar.getInstance();
                                String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                                date = sCalendar.get(Calendar.DATE);

                                //check week day or weekend
                                if (day.equals("Saturday") || day.equals("Sunday")) {
                                    Object currentChallenge = dataSnapshot.child("currentWeekendChallenge").getValue();
                                    int current = Integer.parseInt(currentChallenge.toString());
                                    if (current < 4) {
                                        fdb.child(userUID).child("currentWeekendChallenge").setValue(String.valueOf(current + 1));
                                    } else {
                                        fdb.child(userUID).child("currentWeekendChallenge").setValue("1");
                                    }

                                } else {
                                    Object currentChallenge = dataSnapshot.child("currentWeekdayChallenge").getValue();
                                    int current = Integer.parseInt(currentChallenge.toString());
                                    if (current < 26) {
                                        fdb.child(userUID).child("currentWeekdayChallenge").setValue(String.valueOf(current + 1));
                                    } else {
                                        fdb.child(userUID).child("currentWeekdayChallenge").setValue("1");
                                    }
                                }

                                changeChallenge();
                            }

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
