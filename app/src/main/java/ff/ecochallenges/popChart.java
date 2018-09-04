package ff.ecochallenges;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class popChart extends Activity {
    private ImageView closeBtn;
    PieChart piechart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_chart);
        closeBtn = (ImageView)findViewById(R.id.closeIcon);
        Spinner yearSelect = (Spinner)findViewById(R.id.selectYear);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.yearOfChart,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSelect.setAdapter(adapter);

        DisplayMetrics dsm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dsm);
        int width = dsm.widthPixels;
        int height = dsm.heightPixels;

        //getWindow().setLayout((int) (width*0.9),(int)(height*0.9));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams parmas = getWindow().getAttributes();
        parmas.gravity = Gravity.CENTER;

        parmas.x = 0;
        parmas.y = 0;
        getWindow().setAttributes(parmas);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popChart.this.finish();
            }
        });



    }


}
