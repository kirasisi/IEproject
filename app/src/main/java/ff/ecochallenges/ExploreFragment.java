package ff.ecochallenges;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;



public class ExploreFragment extends Fragment {
    private ImageView closeBtn;
    PieChart piechart;
    DatabaseReference db;
    CheckBox cb;
    Spinner yearSelect;
    String year=null;
    TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vExplore = inflater.inflate(R.layout.fragment_explore, container, false);
        yearSelect = (Spinner)vExplore.findViewById(R.id.selectYear);
        piechart = vExplore.findViewById(R.id.pieC);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChart,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSelect.setAdapter(adapter);
        cb = vExplore.findViewById(R.id.checkBox);
        title = vExplore.findViewById(R.id.titleForTotalChart);
        year = yearSelect.getSelectedItem().toString();
        getData(year);






        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    //yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                             //@Override
                                                             //public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                 year = yearSelect.getSelectedItem().toString();

                                                             //}

                                                            // @Override
                                                             //public void onNothingSelected(AdapterView<?> parent) {

                                                            // }
                                                        // });
                    title.setText("Annually Generated Waste Per Capita (KG), Victoria");

                    getPerCap(year);


                }
                else{
                    //yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            year = yearSelect.getSelectedItem().toString();

//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
                    title.setText("Annually Generated Total Waste (Tonnes), Victoria");
                    getData(year);


                }
            }
        });


        piechart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                String type = pe.getLabel();
                Intent intent = new Intent(getActivity(),popChart.class);
                boolean isChecked = false;
                if(cb.isChecked()){
                    isChecked = true;
                }
                intent.putExtra("isChecked",isChecked);
                intent.putExtra("type",type);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });


        return vExplore;
    }

    public void getPerCap(String year){

            setPerCap(year);

        yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String year = yearSelect.getSelectedItem().toString();
                setPerCap(year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });}


    public void getData(String year){
           setData(year);

            yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String year = yearSelect.getSelectedItem().toString();
                    setData(year);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


    public void setPerCap(String year){
        db = FirebaseDatabase.getInstance().getReference().child("ODTotalAnnualWaste");
        db.orderByKey().equalTo(year).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Double glass = dataSnapshot.child("Glass").child("generatedPerCapitaKg").getValue(Double.class);
                Double metal = dataSnapshot.child("Metal").child("generatedPerCapitaKg").getValue(Double.class);
                Double organic = dataSnapshot.child("Organic").child("generatedPerCapitaKg").getValue(Double.class);
                Double paper = dataSnapshot.child("Paper").child("generatedPerCapitaKg").getValue(Double.class);
                Double plastic = dataSnapshot.child("Plastic").child("generatedPerCapitaKg").getValue(Double.class);
                Double rubber = dataSnapshot.child("Rubber").child("generatedPerCapitaKg").getValue(Double.class);



                setPie(glass,metal,organic,paper,plastic,rubber);
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

    public void setData(String year){
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



                setPie(glass,metal,organic,paper,plastic,rubber);
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


    public void setPie(double glassTotal,double metalTotal,double organicToal,double paperTotal,double plasticTotal, double rubberTotal) {
        piechart.setHoleColor(Color.WHITE);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) metalTotal, "Metal"));
        entries.add(new PieEntry((float) glassTotal, "Glass"));
        entries.add(new PieEntry((float) organicToal, "Organic"));
        entries.add(new PieEntry((float) rubberTotal, "Rubber"));
        entries.add(new PieEntry((float) paperTotal, "Paper"));
        entries.add(new PieEntry((float) plasticTotal, "Plastic"));
        int index = 0;


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(20);
        dataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(dataSet);
        piechart.getDescription().setText("Source: Sustainability Victoria");
        piechart.setData(pieData);
        piechart.invalidate();
        //piechart.highlightValue(type, 0, false);
        dataSet.setColors(new int[]{Color.parseColor("#b79a96"),
                Color.parseColor("#73d0f4"),
                Color.parseColor("#81db6d"),
                Color.parseColor("#ed8a12"),
                Color.parseColor("#ffc1c3"),
                Color.parseColor("#d4a2f2"),
        });
    }





}
