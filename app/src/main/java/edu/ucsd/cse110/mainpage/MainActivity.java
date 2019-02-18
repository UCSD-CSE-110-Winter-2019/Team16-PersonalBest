package edu.ucsd.cse110.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.ucsd.cse110.mainpage.fitness.FitnessService;
import edu.ucsd.cse110.mainpage.fitness.FitnessServiceFactory;
import edu.ucsd.cse110.mainpage.fitness.GoogleFitAdapter;

import edu.ucsd.cse110.mainpage.classes.DistanceCalculator;
import edu.ucsd.cse110.mainpage.classes.SpeedCalculator;
import edu.ucsd.cse110.mainpage.classes.TimeCalculator;
import edu.ucsd.cse110.mainpage.classes.StepCounter;

public class MainActivity extends AppCompatActivity {
    private TimeCalculator timer;
    private StepCounter stepper;
    private TextView homeMessage;
    private TextView textSteps;
    private TextView speedTView;
    private TextView distance;
    private long stepsCount;
    private long walkStepsCount;
    private long walkTime;
    private float walkSpeed;
    private float walkDistance;
    private int height;
    SharedPreferences userSharedPref;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private String fitnessServiceKey = "GOOGLE_FIT";

    private static final String TAG = "MainActivity";

    private FitnessService fitnessService;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    homeMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    homeMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        userSharedPref.edit().clear().commit();
        // Keep track of user preferences
        height = userSharedPref.getInt("height",-1);
        stepsCount = userSharedPref.getLong("steps", 0);

        // Create a timer and a stepper to time and count steps for a walk
        timer = new TimeCalculator();
        stepper = new StepCounter();

        // Keep track of all of the Text Views to change
        homeMessage = (TextView) findViewById(R.id.message);
        speedTView = (TextView)findViewById(R.id.walkingSpeed);
        textSteps = findViewById(R.id.stepsView);
        distance = (TextView)findViewById(R.id.distanceTView);

        if (height == -1) {
            Intent promptHeightIntent = new Intent(this, EnterHeightActivity.class);
            startActivityForResult(promptHeightIntent, 0);
        }

        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity stepCountActivity) {
                return new GoogleFitAdapter(stepCountActivity);
            }
        });

        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Create a button to go to charts
        Button chartsPageBtn = (Button)findViewById(R.id.StepsChartBtn);
        chartsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChartsPage();
            }
        });

        // Create a button to update personal best
        Button personalBestBtn = (Button)findViewById(R.id.personalbestBtn);
        personalBestBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                updatePersonalBest();
            }
        });

        // Create a button to start and stop walks
        final Button walk_button = (Button) findViewById(R.id.walk_button);
        walk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Start the walk
                if(walk_button.getText()== getString(R.string.start_button))
                {
                    // Start keeping track of the walk
                    timer.startTimer();
                    stepper.startSteps(stepsCount);

                    // Update the view of the button
                    makeAccent(homeMessage);
                    makeAccent(textSteps);
                    view.setBackgroundResource(R.drawable.end_button_bg_round);
                    homeMessage.setText(getString(R.string.title_walk));
                    walk_button.setText(getString(R.string.end_button));
                }

                // Stop the walk
                else
                {
                    // Find steps distance, speed, and time for the walk
                    fitnessService.updateStepCount();
                    walkStepsCount = stepper.getSteps(stepsCount);
                    walkDistance = DistanceCalculator.stepsToDistance(walkStepsCount, height);
                    walkTime = timer.getWalkTime();
                    walkSpeed = SpeedCalculator.walkingSpeed(walkDistance, walkTime);

                    // Provide the user information about the walk
                    toaster("You walked for " + walkStepsCount + " steps over " + walkTime / 1000
                            + " seconds!");
                    setSpeedTextView(walkSpeed);
                    setDistanceTextView(walkDistance);

                    // Update the view of the button
                    makePrimary(homeMessage);
                    makePrimary(textSteps);
                    view.setBackgroundResource(R.drawable.start_button_bg_round);
                    homeMessage.setText(getString(R.string.title_home));
                    walk_button.setText(getString(R.string.start_button));
                }

            }
        });

        UpdateStepsAsyncTask task = new UpdateStepsAsyncTask(fitnessService);
        task.execute();
        fitnessService.updateStepCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // close if the height is not set
        int height = userSharedPref.getInt("height",-1);

        if (height == -1) {
            finish();
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
                DistanceCalculator.stepsToDistance(stepsCount, height);
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    /**
     * Set the number of steps
     */

    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount) + " Steps");
        this.stepsCount = stepCount;
        SharedPreferences.Editor editor = userSharedPref.edit();
        editor.putLong("steps", stepCount);
        editor.apply();
    }

    /**
     * Set the distance text
     */
    public void setDistanceTextView(float totalDistance){
        distance.setText("Dist: "+totalDistance+" miles");
    }

    /**
     * Set the speed text
     */
    public void setSpeedTextView(float walkSpeed) {
        speedTView.setText(walkSpeed + " mph");
    }

    /**
     * Makes a view a primary color
     */
    public void makePrimary(View v) {
        v.setBackgroundResource(R.drawable.primary_background);
    }

    /**
     * Makes a view an accent color
     */
    public void makeAccent(View v) {
        v.setBackgroundResource(R.drawable.accent_background);
    }

    /**
     * Makes a toast
     */
    public void toaster (String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s , Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Switches to the charts view
     */
    public void goToChartsPage(){
        Intent intent = new Intent(this, StepsChart.class);
        intent.putExtra("mySteps", stepsCount);
        startActivity(intent);
    }

    /**
     * Updates your personal best
     */
    public void updatePersonalBest(){
        System.out.println("Personal Best is " + userSharedPref.getLong("userdata", 0));
        if (userSharedPref.getLong("personalBest", 0) < stepsCount) {
            SharedPreferences.Editor editor = userSharedPref.edit();
            editor.putLong("personalBest", stepsCount);
            editor.apply();
        }
        TextView personalBest = (TextView)findViewById(R.id.personalBest);
        long bestSteps= userSharedPref.getLong("personalBest",0);
        personalBest.setText("Best: " + String.valueOf(bestSteps) + " Steps");
    }
}

