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

import edu.ucsd.cse110.mainpage.fitness.FitnessService;
import edu.ucsd.cse110.mainpage.fitness.FitnessServiceFactory;
import edu.ucsd.cse110.mainpage.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView homeMessage;
    private TextView textSteps;

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

        SharedPreferences userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        userSharedPref.edit().clear().commit();
        int height = userSharedPref.getInt("height",-1);
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
        UpdateStepsAsyncTask task = new UpdateStepsAsyncTask( fitnessService );
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // close if the height is not set
        SharedPreferences userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);
        int height = userSharedPref.getInt("height",-1);
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
    }
}
