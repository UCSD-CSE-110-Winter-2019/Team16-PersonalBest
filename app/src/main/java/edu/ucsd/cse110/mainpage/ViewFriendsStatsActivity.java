package edu.ucsd.cse110.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewFriendsStatsActivity extends AppCompatActivity {

    private BarChart lineChart;
    private LineData lineData;
    private String userEmail;
    private long goalSteps = 0;
    ArrayList<IBarDataSet> lineDataSets = new ArrayList<>();
    ArrayList<ILineDataSet> goalLineDataSets = new ArrayList<>();
    FirebaseFirestore db;
    SharedPreferences pref;
    String userDocString;
    private static final String TAG = "StepsChart";
    List<String> regSteps;
    List<String> walkedSteps;
    ArrayList<BarEntry> entryArrayList;
    ArrayList<BarEntry> entryWalkedStepsList;
    String[] xAxis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends_stats);

        pref = getSharedPreferences("userdata", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            userEmail = (String)bundle.getString("userEmail");
        }

        Button messageBtn = findViewById(R.id.MessageBtn);

        messageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goMessage();
            }
        });

        lineChart = (BarChart)findViewById(R.id.friendsStatsLineChart);
        //lineData = new LineData(getXvalues(), getLineDataValues());
        //lineChart.setData(lineData);
        ArrayList<String> xAxes = getXvalues();
        xAxis = new String[xAxes.size()];
        for(int i = 0; i < xAxes.size(); i++){
            xAxis[i] = xAxes.get(i).toString();
        }

        entryArrayList = new ArrayList<>();
        entryWalkedStepsList = new ArrayList<>();


        if(!userEmail.equals("")) {
            System.out.println("user email is ------------------------" + userEmail);
            DocumentReference user = db.collection("users").document(userEmail);
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

                                for(int i=0; i<regSteps.size()-1; i++){
                                    BarEntry e = new BarEntry(Integer.parseInt(regSteps.get(i)), i);

                                    entryArrayList.add(e);


                                }

                                for(int k=0; k<walkedSteps.size()-1; k++){

                                    BarEntry j = new BarEntry(Integer.parseInt(walkedSteps.get(k)), k);

                                    entryWalkedStepsList.add(j);

                                }
                                setRegStepBarValues(entryArrayList);
                                setWalkStepBarValues(entryWalkedStepsList);

                                System.out.println("regSteps.................." + regSteps);
                                System.out.println("walkedSteps.................." + walkedSteps);
                                //fetchLineDataValues();
                                BarDataSet barRegDataSet = new BarDataSet(entryArrayList, "Regular Steps Taken");

                                barRegDataSet.setColor(Color.BLUE);

                                BarDataSet barWalkDataSet = new BarDataSet(entryWalkedStepsList, "Walk Steps Taken");

                                barWalkDataSet.setColor(Color.GREEN);

                                LineDataSet goalLine = new LineDataSet(getGoalDataValues(), "goal");
                                goalLine.setDrawCircles(false);
                                goalLine.setDrawValues(false);
                                goalLine.setColor(Color.RED);

                                lineDataSets.add(barRegDataSet);
                                goalLineDataSets.add(goalLine);
                                lineDataSets.add(barWalkDataSet);

                                YAxis yAxisRight = lineChart.getAxisRight();
                                yAxisRight.setEnabled(false);

                                lineChart.setData(new BarData(xAxis, lineDataSets));

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
                                    BarEntry e = new BarEntry(Integer.parseInt(regSteps.get(i)), i);

                                    entryArrayList.add(e);


                                }

                                for(int k=0; k<walkedSteps.size()-1; k++){

                                    BarEntry j = new BarEntry(Integer.parseInt(walkedSteps.get(k)), k);

                                    entryWalkedStepsList.add(j);

                                }
                                setRegStepBarValues(entryArrayList);
                                setWalkStepBarValues(entryWalkedStepsList);



                                System.out.println("regSteps.................." + regSteps);
                                System.out.println("walkedSteps.................." + walkedSteps);


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }


    }

    private void setRegStepBarValues(ArrayList<BarEntry> regStepsLine) {
        entryArrayList = regStepsLine;
        System.out.println("entryArraylist is.............. " + entryArrayList);
    }

    private void setWalkStepBarValues(ArrayList<BarEntry> walkStepLineValues) {
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
        Entry goal8 = new Entry(goalSteps, 6);
        Entry goal9 = new Entry(goalSteps, 6);
        Entry goal10 = new Entry(goalSteps, 6);






        goalArrayList.add(goal1);
        goalArrayList.add(goal2);
        goalArrayList.add(goal3);
        goalArrayList.add(goal4);
        goalArrayList.add(goal5);
        goalArrayList.add(goal6);
        goalArrayList.add(goal7);
        goalArrayList.add(goal8);
        goalArrayList.add(goal9);
        goalArrayList.add(goal10);


        return goalArrayList;
    }

    private ArrayList<String> getXvalues() {
        ArrayList<String> xvalues = new ArrayList<>();
        xvalues.add("1");
        xvalues.add("2");
        xvalues.add("3");
        xvalues.add("4");
        xvalues.add("5");
        xvalues.add("6");
        xvalues.add("7");
        xvalues.add("8");
        xvalues.add("9");
        xvalues.add("10");
        xvalues.add("11");
        xvalues.add("12");
        xvalues.add("13");
        xvalues.add("14");
        xvalues.add("15");
        xvalues.add("16");
        xvalues.add("17");
        xvalues.add("18");
        xvalues.add("19");
        xvalues.add("20");
        xvalues.add("21");
        xvalues.add("22");
        xvalues.add("23");
        xvalues.add("24");
        xvalues.add("25");
        xvalues.add("26");
        xvalues.add("27");
        xvalues.add("28");



        return xvalues;
    }

    private void goMessage() {
        Intent messageIntent = new Intent(this, MessagingActivity.class);
        messageIntent.putExtra("userEmail", userEmail);
        startActivity(messageIntent);
    }
}


