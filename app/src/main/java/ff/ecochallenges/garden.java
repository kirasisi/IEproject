package ff.ecochallenges;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ff.garden.UnityPlayerActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ff.ecochallenges.game.Global;


public class garden extends AppCompatActivity {
    private SharedPreferences myPreferences;
    private String userUID = null;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userUID = myPreferences.getString("uid", null);
        Intent intent2 = new Intent(this,MainActivity.class);
        intent2.setAction("home");
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.putExtra("Unity","intent test");
        startActivity(intent);
    }




}
