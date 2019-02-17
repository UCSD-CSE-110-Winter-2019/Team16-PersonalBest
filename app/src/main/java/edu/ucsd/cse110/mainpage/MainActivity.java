package edu.ucsd.cse110.mainpage;

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

    private TextView mTextMessage;
    private TextView textSteps;
    private long stepCount;

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
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        //userSharedPref.edit().clear().commit();

        int height = userSharedPref.getInt("height", -1);
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        UpdateStepsAsyncTask task = new UpdateStepsAsyncTask(fitnessService);
        task.execute();

        stepsToDistance(stepCount);
        //values used for testing, replace with actual value
        walkingSpeed(stepsToDistance(stepCount),500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // close if the height is not set
        SharedPreferences userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);
        int height = userSharedPref.getInt("height", -1);
        if (height == -1) {
            finish();
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount));
        this.stepCount = stepCount;
    }


    public float stepsToDistance(long stepCount) {
        //find your average stride length
        SharedPreferences pref = getSharedPreferences("userdata", MODE_PRIVATE);
        int height = pref.getInt("height", -1);

        float strideLength = 0;
        if (height != -1) {
            strideLength = (float) (height * 0.413);
            System.out.println("\n\n\n\nstrideLength is........." + strideLength);
        }
        float feetPerStride = strideLength / 12;
        float stepsPerMile = 5280 / feetPerStride;
        float totalDistanceMiles = stepCount / stepsPerMile;
        System.out.println("\n\n\n\ntotalDistance is........." + totalDistanceMiles);
        return totalDistanceMiles;

    }

    public float walkingSpeed(float distance, float timeinSecs) {
        float hour = timeinSecs / (float)360;
        System.out.println("hour is "+hour);
        float speed = distance / hour;
        System.out.println("walking speed is "+speed +" mph");
        return speed;
    }


    public void updatePersonalBest(View view) {
        SharedPreferences perBest = getSharedPreferences("personalBest", MODE_PRIVATE);
        System.out.println("Personal Best is "+ perBest.getLong("personalBest",0));
        if(perBest.getLong("personalBest",0)<stepCount){
            SharedPreferences.Editor editor = perBest.edit();
            editor.putLong("personalBest", stepCount);
            editor.commit();
        }
        TextView personalBest = (TextView)findViewById(R.id.personalBest);
        long bestSteps= perBest.getLong("personalBest",0);
        personalBest.setText(String.valueOf(bestSteps));
    }
}