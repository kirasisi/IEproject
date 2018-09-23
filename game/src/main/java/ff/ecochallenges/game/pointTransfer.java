package ff.ecochallenges.game;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

public class pointTransfer {
    DatabaseReference db;

    private SharedPreferences myPreferences;
    private int point=0;
    public void setPoint(int p){
        point = p;
        Log.d("Unity",String.valueOf(point));
    }
    public pointTransfer(){


    }
    public void pointPass(){

         Global g = Global.getInstance();
        int data=g.getData();
        Log.d("Unity",String.valueOf(data));

    }
}
