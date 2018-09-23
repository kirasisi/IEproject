package ff.ecochallenges;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ff.garden.UnityPlayerActivity;


public class garden extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.putExtra("Unity","intent test");
        startActivity(intent);
    }
}
