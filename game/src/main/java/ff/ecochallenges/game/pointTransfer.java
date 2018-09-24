package ff.ecochallenges.game;

import android.content.SharedPreferences;
import android.util.Log;

public class pointTransfer {

    private SharedPreferences myPreferences;
    private int point=0;

    public pointTransfer(){


    }
    public int pointPass(){
        Global g = Global.getInstance();
        int data=g.getData();
        Log.d("Unity",String.valueOf(data));
        return data;

    }

}
