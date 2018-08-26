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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account!=null){
            uName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            uid.setText(uName);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(point);
                        } else {
                            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("points").setValue(point);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                    });




        return vHome;
    }
    public void onAcitivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }
}