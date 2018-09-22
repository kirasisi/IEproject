package ff.ecochallenges;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ExploreFragment extends Fragment {
    PieChart piechart;
    DatabaseReference db;
    CheckBox cb;
    Spinner yearSelect;
    String year=null;
    String ctg="hard"; //default
    private RadioButton general;
    private RadioButton water;
    private RadioButton energy;
    private RadioButton co2;
    TextView title;
    private ArrayAdapter<CharSequence> adapter = null;

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
        general = vExplore.findViewById(R.id.button1);
        water = vExplore.findViewById(R.id.button2);
        energy = vExplore.findViewById(R.id.button3);
        co2 = vExplore.findViewById(R.id.button4);

        if (general.isChecked())
            adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartGeneral,android.R.layout.simple_spinner_dropdown_item);
        else if (water.isChecked())
            adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartWater,android.R.layout.simple_spinner_dropdown_item);
        else if (energy.isChecked())
            adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartEnergy,android.R.layout.simple_spinner_dropdown_item);
        else if (co2.isChecked())
            adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartCO2,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSelect.setAdapter(adapter);
        cb = vExplore.findViewById(R.id.checkBox);
        title = vExplore.findViewById(R.id.titleForTotalChart);
        year = yearSelect.getSelectedItem().toString();
        getData(ctg, year);
        yearSelect.setSelection(3);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    year = yearSelect.getSelectedItem().toString();
                    title.setText("Annually Generated Waste Per Capita (KG), Victoria");
                    getPerCap(year);
                }
                else{
                    year = yearSelect.getSelectedItem().toString();
                    title.setText("Annually Generated Total Waste (Tonnes), Victoria");
                    getData(ctg, year);
                }
            }
        });

        water.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(water.isChecked()){
                    cb.setVisibility(View.GONE);
                    ctg = "water";
                    adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartWater,android.R.layout.simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    yearSelect.setAdapter(adapter);
                    yearSelect.setSelection(5);
                    year = yearSelect.getSelectedItem().toString();
                    title.setText("Annual water consumption (Megaliters), Victoria");
                    getData("water", year);
                }
            }
        });

        energy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(energy.isChecked()){
                    cb.setVisibility(View.GONE);
                    ctg = "energy";
                    adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartEnergy,android.R.layout.simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    yearSelect.setAdapter(adapter);
                    yearSelect.setSelection(5);
                    year = yearSelect.getSelectedItem().toString();
                    title.setText("Annual energy consumption (Petajoules), Victoria");
                    getData("energy", year);
                }
            }
        });

        co2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(co2.isChecked()){
                    cb.setVisibility(View.GONE);
                    ctg = "co2";
                    adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartCO2,android.R.layout.simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    yearSelect.setAdapter(adapter);
                    yearSelect.setSelection(7);
                    year = yearSelect.getSelectedItem().toString();
                    title.setText("Annual CO2 emission (Megatonnes), Victoria");
                    getData("co2", year);
                }
            }
        });

        general.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(general.isChecked()){
                    cb.setVisibility(View.VISIBLE);
                    ctg = "hard";
                    adapter = ArrayAdapter.createFromResource(getActivity(),R.array.yearOfChartGeneral,android.R.layout.simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    yearSelect.setAdapter(adapter);
                    yearSelect.setSelection(3);
                    year = yearSelect.getSelectedItem().toString();
                    cb.setChecked(false);
                    title.setText("Annually Generated Total Waste (Tonnes), Victoria");
                    getData("hard", year);
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
                intent.putExtra("ctg",ctg);
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


    public void getData(final String ctg, String year){
           setData(ctg, year);

            yearSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String year = yearSelect.getSelectedItem().toString();
                    setData(ctg, year);
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

                Map pairVals = new HashMap();
                pairVals.put("Glass",glass);
                pairVals.put("Metal", metal);
                pairVals.put("Organic", organic);
                pairVals.put("Paper", paper);
                pairVals.put("Plastic", plastic);
                pairVals.put("Rubber", rubber);

                setPie(pairVals);
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

    public void setData(String ctg, String year){
        if (ctg.equals("hard"))
        {
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

                    Map pairVals = new HashMap();
                    pairVals.put("Glass",glass);
                    pairVals.put("Metal", metal);
                    pairVals.put("Organic", organic);
                    pairVals.put("Paper", paper);
                    pairVals.put("Plastic", plastic);
                    pairVals.put("Rubber", rubber);

                    setPie(pairVals);
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
            db.orderByKey().equalTo(year).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Double agriculture = dataSnapshot.child("Agriculture").child("total").getValue(Double.class);
                    Double mining = dataSnapshot.child("Mining").child("total").getValue(Double.class);
                    Double manufacturing = dataSnapshot.child("Manufacturing").child("total").getValue(Double.class);
                    Double utility = dataSnapshot.child("Utility Services").child("total").getValue(Double.class);
                    Double households = dataSnapshot.child("Households").child("total").getValue(Double.class);
                    Double other = dataSnapshot.child("Other").child("total").getValue(Double.class);
                    Map pairVals = new HashMap();
                    //pairVals.put("Agriculture",agriculture);
                    pairVals.put("Mining", mining);
                    pairVals.put("Manufacturing", manufacturing);
                    pairVals.put("Utility Services", utility);
                    pairVals.put("Households", households);
                    pairVals.put("Other", other);

                    setPie(pairVals);
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
        else if (ctg.equals("energy"))
        {
            db = FirebaseDatabase.getInstance().getReference().child("ODEnergyConsumption");
            db.orderByKey().equalTo(year).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Double commercial = dataSnapshot.child("Commercial").child("total").getValue(Double.class);
                    Double electricity = dataSnapshot.child("Electricity").child("total").getValue(Double.class);
                    Double manufacturing = dataSnapshot.child("Manufacturing").child("total").getValue(Double.class);
                    Double mining = dataSnapshot.child("Mining").child("total").getValue(Double.class);
                    Double residential = dataSnapshot.child("Residential").child("total").getValue(Double.class);
                    Double other = dataSnapshot.child("Other").child("total").getValue(Double.class);
                    Double transport = dataSnapshot.child("Transport").child("total").getValue(Double.class);
                    Map pairVals = new HashMap();
                    pairVals.put("Mining", mining);
                    pairVals.put("Commercial", commercial);
                    pairVals.put("Electricity", electricity);
                    pairVals.put("Manufacturing", manufacturing);
                    pairVals.put("Other", other);
                    pairVals.put("Residential", residential);
                    pairVals.put("Transport", transport);

                    setPie(pairVals);
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
        else if (ctg.equals("co2"))
        {
            db = FirebaseDatabase.getInstance().getReference().child("ODCO2Emission");
            db.orderByKey().equalTo(year).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Double agriculture = dataSnapshot.child("Agriculture").child("total").getValue(Double.class);
                    Double electricity = dataSnapshot.child("Electricity").child("total").getValue(Double.class);
                    Double fugitive = dataSnapshot.child("Fugitive").child("total").getValue(Double.class);
                    Double industrial = dataSnapshot.child("Industrial").child("total").getValue(Double.class);
                    Double stationary = dataSnapshot.child("Stationary Energy").child("total").getValue(Double.class);
                    Double waste = dataSnapshot.child("Waste").child("total").getValue(Double.class);
                    Double transport = dataSnapshot.child("Transport").child("total").getValue(Double.class);
                    Map pairVals = new HashMap();
                    pairVals.put("Agriculture", agriculture);
                    pairVals.put("Fugitive", fugitive);
                    pairVals.put("Electricity", electricity);
                    pairVals.put("Industrial", industrial);
                    pairVals.put("Stationary Energy", stationary);
                    pairVals.put("Waste", waste);
                    pairVals.put("Transport", transport);

                    setPie(pairVals);
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


    public void setPie(Map pairVals) {
        piechart.setHoleColor(Color.WHITE);
        ArrayList<PieEntry> entries = new ArrayList<>();
        Iterator it = pairVals.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            double val = (double) pair.getValue();
            entries.add(new PieEntry((float) val, pair.getKey().toString()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(20);
        dataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(dataSet);
        if (ctg.equals("hard"))
            piechart.getDescription().setText("Source: Sustainability Victoria");
        else if (ctg.equals("water"))
            piechart.getDescription().setText("Source: Australian Bureau of Statistics");
        else if (ctg.equals("energy"))
            piechart.getDescription().setText("Source: Department of the Environment and Energy");
        else if (ctg.equals("co2"))
            piechart.getDescription().setText("Source: Department of the Environment and Energy");
        piechart.setData(pieData);
        piechart.invalidate();
        dataSet.setColors(new int[]{Color.parseColor("#b79a96"),
                Color.parseColor("#73d0f4"),
                Color.parseColor("#81db6d"),
                Color.parseColor("#ed8a12"),
                Color.parseColor("#ffc1c3"),
                Color.parseColor("#d4a2f2"),
                Color.parseColor("#ff5050"),
        });
    }
}
