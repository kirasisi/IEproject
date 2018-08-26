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
import java.util.Locale;
import java.util.Random;


public class ChallengeFragment extends Fragment {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
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
    private int today;
    private int date=0;
    private boolean check=false;
    private Bundle bundle;
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

        if (savedInstanceState != null) {
            date = savedInstanceState.getInt("date");
            check = savedInstanceState.getBoolean("check");
            Calendar c = Calendar.getInstance();
            if(date==c.get(Calendar.DATE)&&check==true){
                completeBtn.setVisibility(View.GONE);
                completeSign.setVisibility(View.VISIBLE);
                completeText.setVisibility(View.VISIBLE);
                nextChallenge.setVisibility(View.VISIBLE);
            }
        }

        Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        date = sCalendar.get(Calendar.DATE);
        int wk = sCalendar.get(Calendar.WEEK_OF_MONTH);
        //check week day or weekend
        if (day.equals("Saturday")||day.equals("Sunday")){
            mDatabase = FirebaseDatabase.getInstance().getReference("WeekendChallenges");
            todaysChallenge = mDatabase.child("c0" + String.valueOf(wk-1));
            today = 0;
        }
        else{
            //connect to firebase
            mDatabase = FirebaseDatabase.getInstance().getReference("WeekdayChallenges");
            if(date<=5){
                todaysChallenge = mDatabase.child("c0" + String.valueOf(date));
            }
            if(5<date&&date<=9){
                todaysChallenge = mDatabase.child("c0" + String.valueOf(date-2));
            }
            if(10<=date&&date<=28){
                todaysChallenge = mDatabase.child("c" + String.valueOf(date-2));
            }
            if(29<=date&&date<=31){
                todaysChallenge = mDatabase.child("c" + String.valueOf(date-5));
            }
            today = date+1;
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

        //get from firebase
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
                point.setText(String.valueOf(points));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeBtn.setVisibility(getView().GONE);
                completeSign.setVisibility(getView().VISIBLE);
                completeText.setText("Well Done");
                if(today==0){
                    nextChallenge.setVisibility(View.VISIBLE);
                    nextChallenge.setText("Enjoy your Weekend with the big challenge!");

                }
                else{
                    Calendar cd = Calendar.getInstance();
                    int wk = cd.get(Calendar.WEEK_OF_MONTH);
                    if (wk-1<=2){
                        nextDayChallenge = mDatabase.child("c0" + String.valueOf(today));
                    }
                    if (wk-1>2){
                        nextDayChallenge = mDatabase.child("c" + String.valueOf(today));
                    }


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

                check = true;
                Bundle b = new Bundle();
                b.putInt("date",date);
                b.putBoolean("date",check);
                onSaveInstanceState(b);

            }

        });




    }








}
