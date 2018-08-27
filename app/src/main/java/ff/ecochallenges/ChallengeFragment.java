package ff.ecochallenges;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class ChallengeFragment extends Fragment {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;
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
    private int t;
    private int date=0;
    private boolean check=false;
    private Bundle bundle;
    private String challengeID;
    boolean completed = false;
    private TextView nextCtitle;
    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View vChallenge = inflater.inflate(R.layout.fragment_challenge, container, false);



            return vChallenge;


        }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("date", date);
        outState.putBoolean("check",check);

        Log.e("TAG", "onSaveInstanceState OK");
        Log.e("TAG", "onSaveInstanceState"+ outState.getInt("date"));

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = new Bundle();
        boolean lastSignIn = bundle.getBoolean("signInDateCheck");

        if(lastSignIn==true){
            changeChallenge();
        }
        else {
            keepChallenge();

        }

        //crete view
        //View vChallenge = inflater.inflate(R.layout.fragment_challenge, container, false);
        cName = getActivity().findViewById(R.id.cName);
        cPic = getActivity().findViewById(R.id.challengePic);
        completeSign = getActivity().findViewById(R.id.completeIcon);
        completeSign.setVisibility(getView().GONE);
        completeText = getActivity().findViewById(R.id.completeText);
        point = getActivity().findViewById(R.id.points);
        completeBtn = getActivity().findViewById(R.id.c_complete);
        instructContent = getActivity().findViewById(R.id.insContent);
        nextChallenge = getActivity().findViewById(R.id.nextChallenge);
        nextChallenge.setVisibility(View.GONE);




//        if (!checkCompletion()) {
//
//            completeBtn.setVisibility(View.GONE);
//            completeSign.setVisibility(View.VISIBLE);
//            completeText.setVisibility(View.VISIBLE);
//            completeText.setText("Well Done");
//            nextChallenge.setVisibility(View.VISIBLE);
//            displayNextChallenge();
//
//        }

        //get from firebase






        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChallengeResult();
                completeBtn.setVisibility(getView().GONE);
                completeSign.setVisibility(getView().VISIBLE);
                completeText.setText("Well Done");

                displayNextChallenge();

                check = true;
                Bundle b = new Bundle();
                b.putInt("date",date);
                b.putBoolean("date",check);
                onSaveInstanceState(b);

            }

        });




    }

    public void saveChallengeResult(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChallengeHistory");
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users");

//        mDatabase.equalTo(challengeID)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                            Date date = new Date();
//                            mDatabase.child(challengeID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("completionDate")
//                                                                                                                          .setValue(formatter.format(date));
//
//
//                        } else {
//                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                            Date date = new Date();
//                            String historyId = mDatabase.push().child(challengeID).getKey();
//                            mDatabase.child(historyId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                                                                    .child("completionDate").setValue(formatter.format(date));
//
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });



        mDatabase2.equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Calendar sCalendar = Calendar.getInstance();
                            String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
                            date = sCalendar.get(Calendar.DATE);
                            if (day.equals("Saturday")||day.equals("Sunday")){
                                mDatabase2.child("currentWeekendChallenge").setValue(getStamp()+1);
                            }
                            else{
                                mDatabase2.child("currentWeekdayChallenge").setValue(getStamp()+1);
                            }




                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public boolean checkCompletion(){


        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChallengeHistory");

        mDatabase.equalTo(getStamp())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        if(dataSnapshot.exists()){
                            if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                                    && dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("completionDate")
                                                                            .equals(formatter.format(date))){


                                    completed =true;


                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });





        return completed;
    }

    public void displayNextChallenge(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("WeekdayChallenges");
        if(getStamp()==0){

            nextChallenge.setVisibility(View.VISIBLE);
            nextChallenge.setText("Enjoy your Weekend with the big challenge!");

        }
        else{
            Calendar cd = Calendar.getInstance();

            nextDayChallenge = mDatabase.child(String.valueOf(getStamp()+1));




            nextDayChallenge.addValueEventListener(new ValueEventListener() {
                public String TAG;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("cName").getValue(String.class);

                    nextChallenge.setText(name);
                    nextChallenge.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });


        }
    }


    public void changeChallenge(){
        Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        date = sCalendar.get(Calendar.DATE);
        //int wk = sCalendar.get(Calendar.WEEK_OF_MONTH);

        DatabaseReference userBase = FirebaseDatabase.getInstance().getReference("Users");
        //check week day or weekend
        if (day.equals("Saturday")||day.equals("Sunday")){
            mDatabase = FirebaseDatabase.getInstance().getReference("WeekendChallenges");
            userBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekendChallenge").getValue(String.class);
                    int current = Integer.parseInt(last);
                    if(current<=4){
                        todaysChallenge = mDatabase.child(String.valueOf(current+1));
                        updateChallengeUI();
                        setStamp(current);
                    }
                    else{
                        todaysChallenge = mDatabase.child("1");
                        updateChallengeUI();
                        setStamp(1);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //challengeID = "c0" + String.valueOf(wk-1);
        }
        else{
            //connect to firebase
            mDatabase = FirebaseDatabase.getInstance().getReference("WeekdayChallenges");

            userBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekdayChallenge").getValue(String.class);
                    int current = Integer.parseInt(last);
                    if(current<=26){
                        todaysChallenge = mDatabase.child(String.valueOf(current+1));
                        updateChallengeUI();
                        setStamp(current);
                    }
                    else{
                        todaysChallenge = mDatabase.child("1");
                        updateChallengeUI();
                        setStamp(1);
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
        //int wk = sCalendar.get(Calendar.WEEK_OF_MONTH);

        mDatabase2 = FirebaseDatabase.getInstance().getReference("Users");
        //check week day or weekend
        if (day.equals("Saturday") || day.equals("Sunday")) {
            //mDatabase = FirebaseDatabase.getInstance().getReference("WeekendChallenges");
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekendChallenge").getValue(String.class);
                    int current = Integer.parseInt(last);

                        todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf(current));
                        updateChallengeUI();
                        setStamp(current);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //challengeID = "c0" + String.valueOf(wk-1);
        } else {
            //connect to firebase
           // mDatabase = FirebaseDatabase.getInstance().getReference("WeekdayChallenges");
            mDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentWeekdayChallenge").getValue(String.class);

                    updateToday(last);
                    updateChallengeUI();



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void updateToday(String current){
        int t = Integer.parseInt(current);
        todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(current);
        setStamp(t);


    }


    public  void updateChallengeUI(){
        todaysChallenge.addValueEventListener(new ValueEventListener() {
            public String TAG;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                urlImage = dataSnapshot.child("cPic").getValue(String.class);
                Glide.with(getActivity().getApplicationContext())
                        .load(urlImage)
                        .into(cPic);

                String name = dataSnapshot.child("cName").getValue(String.class);
                cName.setText(name);
                String description = dataSnapshot.child("description").getValue(String.class);
                instructContent.setText(description);
                Long points = dataSnapshot.child("points").getValue(Long.class);
                point.setText(String.valueOf(points)+"points");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public void setStamp(int today){
        t = today;
    }

    public int getStamp(){
         return t;
    }

    public void updatepoint(int point,String user){



    }










}
