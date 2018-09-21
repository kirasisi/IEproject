package ff.ecochallenges;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.protobuf.StringValue;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class popChart extends Activity {
    private ImageView closeBtn;
    LineChart lineChart;
    DatabaseReference db;
    ArrayList<String> yearList;
    ArrayList<Entry> totalList;
    ArrayList<Entry> perCapList;
    CheckBox cb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_chart);
        closeBtn = (ImageView)findViewById(R.id.closeIcon);
        lineChart = findViewById(R.id.lineC);
        yearList = new ArrayList<>();
        totalList = new ArrayList<>();
        perCapList = new ArrayList<>();
        cb = findViewById(R.id.checkBox);
        DisplayMetrics dsm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dsm);
        int width = dsm.widthPixels;
        int height = dsm.heightPixels;
        getWindow().setLayout((int) (width*0.9),(int)(height*0.7));
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
        final String type = getIntent().getStringExtra("type");
        final String ctg = getIntent().getStringExtra("ctg");
        boolean isChecked = getIntent().getBooleanExtra("isChecked",true);
        if(isChecked==true && !ctg.equals("maintrend")){
           cb.setVisibility(View.GONE);
            perCapList.clear();
            getPerCap(ctg, type);
        }else {
           cb.setVisibility(View.GONE);
            getData(ctg, type);
        }

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    perCapList.clear();
                    getPerCap(ctg, type);
                }
                else{
                    totalList.clear();
                    getData(ctg, type);
                }
            }

        });
    }


    public void getPerCap(final String ctg, final String type){
        db = FirebaseDatabase.getInstance().getReference().child("ODTotalAnnualWaste");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                yearList.add(dataSnapshot.getKey().toString());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(snapshot.getKey().toString().equals(type)){
                        Float total = snapshot.child("generatedPerCapitaKg").getValue(Float.class);
                        perCapList.add(new Entry(Float.parseFloat(dataSnapshot.getKey().toString()), total));

                        Log.i("data1",total.toString());
                    }
                }
                Log.i("data2",perCapList.toString());
                setLine2(ctg, type);
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


    public void getData(final String ctg, final String type){

        if (ctg.equals("hard"))
        {
            db = FirebaseDatabase.getInstance().getReference().child("ODTotalAnnualWaste");

            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    yearList.add(dataSnapshot.getKey().toString());
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if(snapshot.getKey().toString().equals(type)){
                            Float total = snapshot.child("totalGenerated").getValue(Float.class);
                            totalList.add(new Entry(Float.parseFloat(dataSnapshot.getKey().toString()), total));

                            Log.i("data1",total.toString());

                        }

                    }
                    Log.i("data2",totalList.toString());
                    setLine(ctg, type);

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
        else if (ctg.equals("water"))
        {
            db = FirebaseDatabase.getInstance().getReference().child("ODWaterConsumption");

            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    yearList.add(dataSnapshot.getKey().toString());
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if(snapshot.getKey().toString().equals(type)){
                            Float total = snapshot.child("total").getValue(Float.class);
                            totalList.add(new Entry(Float.parseFloat(dataSnapshot.getKey().toString()), total));

                            Log.i("data1",total.toString());

                        }

                    }
                    Log.i("data2",totalList.toString());
                    setLine(ctg, type);

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
        else if(ctg.equals("maintrend"))
        {
            db = FirebaseDatabase.getInstance().getReference().child("ODTotalAnnualTrend");

            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    yearList.add(dataSnapshot.getKey().toString());
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        //if(snapshot.getKey().toString().equals(type)){
                            Float total = snapshot.getValue(Float.class);
                            totalList.add(new Entry(Float.parseFloat(dataSnapshot.getKey().toString()), total));

                            Log.i("data1",total.toString());

                        //}

                    }
                    Log.i("data2",totalList.toString());
                    setLine(ctg, type);

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

    public void setLine(String ctg, String type){
        LineDataSet set1 = null;
        if (ctg.equals("hard"))
        {
            set1 = new LineDataSet(totalList, "Total "+type+" waste per year in VIC");
            set1.setValueTextSize(9f);
        }

        else if (ctg.equals("maintrend"))
        {
            set1 = new LineDataSet(totalList, "Waste generated yearly trend in VIC");
            set1.setValueTextSize(0f);
        }

        else if (ctg.equals("water"))
        {
            set1 = new LineDataSet(totalList, "Total "+type+" water consumption in VIC");
            set1.setValueTextSize(9f);
        }

        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        XAxis xAxisFromChart = lineChart.getXAxis();
        xAxisFromChart.setDrawAxisLine(true);
        xAxisFromChart.setGranularity(1f);
        xAxisFromChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.0f",value);
            }

            public int getDecimalDigits() {  return 0; }
        };

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        xAxisFromChart.setValueFormatter(formatter);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setText("Unit: Tonnes");

        lineChart.invalidate();

    }

    public void setLine2(String ctg, String type){
        LineDataSet set2;
        set2 = new LineDataSet(perCapList, "Total "+type+" waste per year per capita in VIC");
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<ILineDataSet>();
        dataSets2.add(set2);
        LineData data = new LineData(dataSets2);
        lineChart.setData(data);
        final XAxis xAxisFromChart = lineChart.getXAxis();
        xAxisFromChart.setDrawAxisLine(true);
        xAxisFromChart.setGranularity(1f);
        xAxisFromChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.0f",value);
            }


            public int getDecimalDigits() {  return 0; }
        };

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        xAxisFromChart.setValueFormatter(formatter);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setText("Unit: Kilograms");
        lineChart.invalidate();

    }
}
