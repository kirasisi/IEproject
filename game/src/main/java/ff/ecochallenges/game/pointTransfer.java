package ff.ecochallenges.game;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

public class pointTransfer {

    private SharedPreferences myPreferences;
    private int point=0;


    public pointTransfer(){


    }
    public int pointPass(){
        Global g = Global.getInstance();
        int data=g.getData();
        return data;

    }
    public void updatePoint(){
        UnityPlayer.UnitySendMessage("ShopControl","updatePointAmount",null);
    }
    public void receivePoint(int point){
        Global g = Global.getInstance();
        g.setData(point);
        g.setpoint(point);
        int test = g.getData();
        Log.d("Unity",String.valueOf(test));

    }



}
