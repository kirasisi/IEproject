package ff.ecochallenges;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class popChart extends Activity {
    private ImageView closeBtn;
    PieChart piechart;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_chart);
        closeBtn = (ImageView)findViewById(R.id.closeIcon);
        final Spinner yearSelect = (Spinner)findViewById(R.id.selectYear);
        piechart = findViewById(R.id.pieC);
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
        yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  String year = yearSelect.getSelectedItem().toString();
                  db = FirebaseDatabase.getInstance().getReference().child("ODTotalAnnualWaste");
                db.orderByKey().equalTo(year).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                          Double glass = dataSnapshot.child("Glass").child("totalGenerated").getValue(Double.class);
                          Double metal = dataSnapshot.child("Metal").child("totalGenerated").getValue(Double.class);
                          Double organic = dataSnapshot.child("Organic").child("totalGenerated").getValue(Double.class);
                          Double paper = dataSnapshot.child("Paper").child("totalGenerated").getValue(Double.class);
                          Double plastic = dataSnapshot.child("Plastic").child("totalGenerated").getValue(Double.class);
                          Double rubber = dataSnapshot.child("Rubber").child("totalGenerated").getValue(Double.class);
                          Double textile = dataSnapshot.child("Textile").child("totalGenerated").getValue(Double.class);
                          String type = getIntent().getStringExtra("type");
                          int index = 0;
                        switch (type) {
                            case "Metal" : index =0;
                                break;
                            case  "Glass" : index = 1;
                                break;
                            case  "Organic": index = 2;
                                break;
                            case "Rubber": index = 3;
                                break;
                            case "Paper": index =4;
                                break;
                            case "Plastic":index=5;
                                break;
                        }
                          setPie(glass,metal,organic,paper,plastic,rubber,textile,index);
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    public void setPie(double glassTotal,double metalTotal,double organicToal,double paperTotal,double plasticTotal, double rubberTotal, double textTotal, int type){
        piechart.setHoleColor(Color.WHITE);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)metalTotal,"Metal"));
        entries.add(new PieEntry((float)glassTotal,"Glass"));
        entries.add(new PieEntry((float)organicToal,"Organic"));
        entries.add(new PieEntry((float)rubberTotal,"Rubber"));
        entries.add(new PieEntry((float)paperTotal,"Paper"));
        entries.add(new PieEntry((float)plasticTotal,"Plastic"));
        int index = 0;



        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(20);
        dataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(dataSet);
        piechart.getDescription().setText("Annually Generated Waste in tonnes, Victoria");
        piechart.setData(pieData);
        piechart.invalidate();
        piechart.highlightValue(type, 0, false);
        dataSet.setColors(new int[]{Color.parseColor("#c61939"),
                Color.parseColor("#af4623"),
                Color.parseColor("#6a753b"),
                Color.parseColor("#FFF2ED53"),
                Color.parseColor("#3c6bb7"),
                Color.parseColor("#a83ca2"),
        });
//        Legend l = piechart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setEnabled(false);

    }
}
