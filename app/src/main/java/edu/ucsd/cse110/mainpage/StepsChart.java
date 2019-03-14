package edu.ucsd.cse110.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepsChart extends AppCompatActivity {

    private LineChart lineChart;
    private LineData lineData;
    private int StepsForToday = 0;
    private int walkedStepsForToday = 0;
    private long goalSteps = 0;
    ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    FirebaseFirestore db;
    SharedPreferences pref;
    String userDocString;
    private static final String TAG = "StepsChart";
    List<String> regSteps;
    List<String> walkedSteps;
    ArrayList<Entry> entryArrayList;
    ArrayList<Entry> entryWalkedStepsList;
    String[] xAxis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_chart);

        pref = getSharedPreferences("userdata", MODE_PRIVATE);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            StepsForToday = (int)bundle.getLong("mySteps");
            walkedStepsForToday = (int)bundle.getLong("walkedSteps");
            goalSteps = bundle.getLong("goal");
            //regSteps = bundle.getStringArrayList("regStepsArray");
           // System.out.println("reg steps is.............." + regSteps);
           // walkedSteps = bundle.getStringArrayList("walkedStepsArray");


        }

        lineChart = (LineChart)findViewById(R.id.linechart);
        //lineData = new LineData(getXvalues(), getLineDataValues());
        //lineChart.setData(lineData);
        ArrayList<String> xAxes = getXvalues();
        xAxis = new String[xAxes.size()];
        for(int i = 0; i < xAxes.size(); i++){
            xAxis[i] = xAxes.get(i).toString();
        }

        entryArrayList = new ArrayList<>();
        entryWalkedStepsList = new ArrayList<>();

        //hardcoded step values for Monday to Saturday since I didn't have access to an android phone then.
        db = FirebaseFirestore.getInstance();
        userDocString = pref.getString("userIDinDB", "");

        if(!userDocString.equals("")) {
            DocumentReference user = db.collection("users").document(userDocString);

            user.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                regSteps = (List<String>) task.getResult().get("regularStepsData");
                                walkedSteps = (List<String>) task.getResult().get("walkedStepsData");
                                System.out.println("walkedSteps.................." + walkedSteps);

                                //flip the arrays so it's easier to select 7 elements to display in
                                //the graph
                                for (int b = 0; b < regSteps.size() / 2; b++) {
                                    final String steps = regSteps.get(b);
                                    regSteps.set(b, regSteps.get(regSteps.size() - b- 1));
                                    regSteps.set(regSteps.size() - b - 1, steps);

                                    final String steps2 = walkedSteps.get(b);
                                    walkedSteps.set(b, walkedSteps.get(walkedSteps.size() - b- 1));
                                    walkedSteps.set(walkedSteps.size() - b - 1, steps2);
                                }


                                if(regSteps.size() >= 7){
                                    regSteps = regSteps.subList(0,8);
                                }
                                if(walkedSteps.size() >= 7) {
                                    walkedSteps = walkedSteps.subList(0, 8);
                                }

                                for (int b = 0; b < regSteps.size() / 2; b++) {
                                    final String steps = regSteps.get(b);
                                    regSteps.set(b, regSteps.get(regSteps.size() - b- 1));
                                    regSteps.set(regSteps.size() - b - 1, steps);

                                    final String steps2 = walkedSteps.get(b);
                                    walkedSteps.set(b, walkedSteps.get(walkedSteps.size() - b- 1));
                                    walkedSteps.set(walkedSteps.size() - b - 1, steps2);
                                }

                                for(int i=0; i<regSteps.size()-1; i++){
                                    Entry e = new Entry(Integer.parseInt(regSteps.get(i)), i);

                                    entryArrayList.add(e);


                                }

                                for(int k=0; k<walkedSteps.size()-1; k++){

                                    Entry j = new Entry(Integer.parseInt(walkedSteps.get(k)), k);

                                    entryWalkedStepsList.add(j);

                                }
                                setRegStepLineValues(entryArrayList);
                                setWalkStepLineValues(entryWalkedStepsList);



                                System.out.println("regSteps.................." + regSteps);
                                System.out.println("walkedSteps.................." + walkedSteps);
                                //fetchLineDataValues();
                                LineDataSet lineDataSet = new LineDataSet(entryArrayList, "Regular Steps Taken");
                                lineDataSet.setDrawCircles(true);
                                lineDataSet.setCircleColor(Color.BLUE);
                                lineDataSet.setCircleColorHole(Color.BLUE);
                                lineDataSet.setColor(Color.BLUE);

                                LineDataSet walkDataSet = new LineDataSet(entryWalkedStepsList, "Walk Steps Taken");
                                walkDataSet.setDrawCircles(true);
                                walkDataSet.setCircleColor(Color.GREEN);
                                lineDataSet.setCircleColorHole(Color.GREEN);
                                walkDataSet.setColor(Color.GREEN);

                                LineDataSet goalLine = new LineDataSet(getGoalDataValues(), "goal");
                                goalLine.setDrawCircles(false);
                                goalLine.setDrawValues(false);
                                goalLine.setColor(Color.RED);

                                lineDataSets.add(lineDataSet);
                                lineDataSets.add(goalLine);
                                lineDataSets.add(walkDataSet);

                                YAxis yAxisRight = lineChart.getAxisRight();
                                yAxisRight.setEnabled(false);

                                lineChart.setData(new LineData(xAxis, lineDataSets));

                                lineChart.setVisibleXRangeMaximum(1000f);
                                lineChart.setTouchEnabled(true);
                                lineChart.setDragEnabled(true);


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }


    }



    private void fetchLineDataValues() {
        entryArrayList = new ArrayList<>();
        entryWalkedStepsList = new ArrayList<>();

        //hardcoded step values for Monday to Saturday since I didn't have access to an android phone then.
        db = FirebaseFirestore.getInstance();
        userDocString = pref.getString("userIDinDB", "");

        if(!userDocString.equals("")) {
            DocumentReference user = db.collection("users").document(userDocString);

            user.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                regSteps = (List<String>) task.getResult().get("regularStepsData");
                                walkedSteps = (List<String>) task.getResult().get("walkedStepsData");
                                System.out.println("walkedSteps.................." + walkedSteps);

                                if(regSteps.size() >= 8){
                                    regSteps = regSteps.subList((regSteps.size()-8),((regSteps.size()-8)+7));
                                }
                                if(walkedSteps.size() >= 8){
                                    walkedSteps =  walkedSteps.subList((walkedSteps.size()-8),((walkedSteps.size()-8)+7));
                                }

                                for(int i=0; i<regSteps.size()-1; i++){
                                    Entry e = new Entry(Integer.parseInt(regSteps.get(i)), i);

                                    entryArrayList.add(e);


                                }

                                for(int k=0; k<walkedSteps.size()-1; k++){

                                    Entry j = new Entry(Integer.parseInt(walkedSteps.get(k)), k);

                                    entryWalkedStepsList.add(j);

                                }
                                setRegStepLineValues(entryArrayList);
                                setWalkStepLineValues(entryWalkedStepsList);



                                System.out.println("regSteps.................." + regSteps);
                                System.out.println("walkedSteps.................." + walkedSteps);


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }


    }

    private void setRegStepLineValues(ArrayList<Entry> regStepsLine) {
        entryArrayList = regStepsLine;
        System.out.println("entryArraylist is.............. " + entryArrayList);
    }

    private void setWalkStepLineValues(ArrayList<Entry> walkStepLineValues) {
        entryWalkedStepsList = walkStepLineValues;
    }

    private ArrayList<Entry> getGoalDataValues() {
        ArrayList<Entry> goalArrayList = new ArrayList<>();

        Entry goal1 = new Entry(goalSteps, 0);
        Entry goal2 = new Entry(goalSteps, 1);
        Entry goal3 = new Entry(goalSteps, 2);
        Entry goal4 = new Entry(goalSteps, 3);
        Entry goal5 = new Entry(goalSteps, 4);
        Entry goal6 = new Entry(goalSteps, 5);
        Entry goal7 = new Entry(goalSteps, 6);





        goalArrayList.add(goal1);
        goalArrayList.add(goal2);
        goalArrayList.add(goal3);
        goalArrayList.add(goal4);
        goalArrayList.add(goal5);
        goalArrayList.add(goal6);
        goalArrayList.add(goal7);

        return goalArrayList;
    }

    private ArrayList<String> getXvalues() {
        ArrayList<String> xvalues = new ArrayList<>();
        xvalues.add("Monday");
        xvalues.add("Tuesday");
        xvalues.add("Wednesday");
        xvalues.add("Thursday");
        xvalues.add("Friday");
        xvalues.add("Saturday");
        xvalues.add("Sunday");
        return xvalues;
    }
}
