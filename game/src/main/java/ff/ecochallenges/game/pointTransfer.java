package ff.ecochallenges.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unity3d.player.UnityPlayer;

public class pointTransfer {


    private int point=0;
    private SharedPreferences myPreferences;
    private String userUID = null;
    DatabaseReference mDatabase;


    public pointTransfer(){


    }
    public int pointPass(){
        Global g = Global.getInstance();
        int data=g.getData();
       // Log.i("unity",String.valueOf(data));
        return data;

    }
    public void updatePoint(){
        UnityPlayer.UnitySendMessage("ShopControl","updatePointAmount",null);
    }
    public void receivePoint(int point){
        Global g = Global.getInstance();
        g.setData(point);
       // g.setpoint(point);
        int test = g.getData();
        Log.d("Unity",String.valueOf(test));
        userUID = g.getId();
        uploadPoint();

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
