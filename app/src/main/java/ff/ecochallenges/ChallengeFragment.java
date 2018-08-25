package ff.ecochallenges;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //connect to firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("WeekdayChallenges");
        Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        int date = sCalendar.get(Calendar.DATE);
        DatabaseReference todaysChallenge = mDatabase.child("c" + String.valueOf(date));
        //crete view
        View vChallenge = inflater.inflate(R.layout.fragment_challenge, container, false);
        cName = (TextView)vChallenge.findViewById(R.id.cName);
        cPic = (ImageView)vChallenge.findViewById(R.id.challengePic);
        completeSign = (ImageView)vChallenge.findViewById(R.id.completeIcon);
        completeSign.setVisibility(getView().GONE);
        completeText = (TextView)vChallenge.findViewById(R.id.completeText);
        point = (TextView)vChallenge.findViewById(R.id.points);
        completeBtn = (Button)vChallenge.findViewById(R.id.c_complete);
        instructContent = (TextView)vChallenge.findViewById(R.id.insContent);

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

            }
        });







        return vChallenge;
    }
}