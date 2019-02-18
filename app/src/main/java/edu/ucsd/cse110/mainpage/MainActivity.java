package edu.ucsd.cse110.mainpage;

import android.graphics.Color;
import android.app.Activity;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.ucsd.cse110.mainpage.fitness.FitnessService;
import edu.ucsd.cse110.mainpage.fitness.FitnessServiceFactory;
import edu.ucsd.cse110.mainpage.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView homeMessage;
    private TextView textSteps;
    private long stepsCount;
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

        Button chartsPageBtn = (Button)findViewById(R.id.StepsChartBtn);
        chartsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChartsPage();
            }
        });

        Button personalBestBtn = (Button)findViewById(R.id.personalbestBtn);
        personalBestBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                updatePersonalBest();
            }
        });

        userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        userSharedPref.edit().clear().commit();

        int height = userSharedPref.getInt("height",-1);
        stepsCount = userSharedPref.getLong("steps", 0);

        if (height == -1) {
            Intent promptHeightIntent = new Intent(this, EnterHeightActivity.class);
            startActivityForResult(promptHeightIntent, 0);
        }

        textSteps = findViewById(R.id.stepsView);

        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity stepCountActivity) {
                return new GoogleFitAdapter(stepCountActivity);
            }
        });

        //fitnessService = FitnessServiceFactory.create(fitnessServiceKey,this);
        //fitnessService.setup(); ----> this is the code that keeps terminating the app upon page load
        //String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        homeMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final Button walk_button = (Button) findViewById(R.id.walk_button);
        walk_button.setTextColor(Color.WHITE);
        walk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(walk_button.getText()== getString(R.string.start_button))
                {
                    homeMessage.setBackgroundResource(R.drawable.accent_background);
                    textSteps.setBackgroundResource(R.drawable.accent_background);
                    view.setBackgroundResource(R.drawable.end_button_bg_round);
                    homeMessage.setText(getString(R.string.title_walk));
                    walk_button.setText(getString(R.string.end_button));
                }
                else
                {
                    homeMessage.setBackgroundResource(R.drawable.primary_background);
                    textSteps.setBackgroundResource(R.drawable.primary_background);
                    view.setBackgroundResource(R.drawable.start_button_bg_round);
                    homeMessage.setText(getString(R.string.title_home));
                    walk_button.setText(getString(R.string.start_button));
                }

            }
        });

        UpdateStepsAsyncTask task = new UpdateStepsAsyncTask(fitnessService);
        task.execute();
        fitnessService.updateStepCount();

        //values used for testing, replace with actual value
        walkingSpeed(stepsToDistance(stepsCount),500);
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
                stepsToDistance(stepsCount);
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount) + " Steps");
        this.stepsCount = stepCount;
        System.out.println("\n\n\n\n my steps1 taken are ........." + stepCount);
        System.out.println("\n\n\n\n my stepsCount taken are ........." + stepsCount);
        SharedPreferences.Editor editor = userSharedPref.edit();
        editor.putLong("steps", stepCount);
        editor.apply();
        //stepsToDistance(stepCount);
    }


    public float stepsToDistance(long steps){

        //find your average stride length
        int height = userSharedPref.getInt("height", -1);
        //stepsCount = userSharedPref.getLong("steps", 0);
        fitnessService.updateStepCount();
        float strideLength = 0;
        if (height != -1) {
            strideLength = (float) (height * 0.413);
            System.out.println("\n\n\n\nstrideLength is........." + strideLength);
        }

        float feetPerStride = strideLength/12;
        float stepsPerMile = 5280/feetPerStride;
        float totalDistanceMiles = steps/stepsPerMile;
        System.out.println("\n\n\n\n my steps taken are ........." + steps);
        System.out.println("\n\n\n\ntotalDistance is........." + totalDistanceMiles);
        setDistanceTextView(totalDistanceMiles);
        return totalDistanceMiles;

    }


    public void setDistanceTextView(float totalDistance){
        TextView distance = (TextView)findViewById(R.id.distanceTView);
        distance.setText("Dist: "+totalDistance+" miles");
    }

    public void goToChartsPage(){
        Intent intent = new Intent(this, StepsChart.class);
        intent.putExtra("mySteps", stepsCount);
        startActivity(intent);
    }


    public float walkingSpeed(float distance, float timeinSecs) {
        float hour = timeinSecs / (float)3600;
        System.out.println("hour is "+hour);
        float speed = distance / hour;
        System.out.println("walking speed is "+speed +" mph");
        TextView speedTView = (TextView)findViewById(R.id.walkingSpeed);
        speedTView.setText(speed + " mph");
        return speed;
    }

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

