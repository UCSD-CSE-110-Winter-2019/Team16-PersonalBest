package edu.ucsd.cse110.mainpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

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
    public long stepsCount;
    private long walkStepsCount;
    private long walkTime;
    private float walkSpeed;
    private float walkDistance;
    private int height;
    SharedPreferences userSharedPref;
    private long currGoalNum = 0;
    public EditText currentGoal;
    String userDocString;
    boolean userInDBBool = false;
    FirebaseFirestore db;
    ArrayList<String> regSteps;
    ArrayList<String> walkedSteps;
    Intent chartsIntent;
    long walkCurrentStepsCount;
    int onlyShowGoalAlertOnceCounter=0;
    String userEmail;
    SharedPreferences.Editor editPref;


    // Google Fit Set up
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

        currentGoal = (EditText)findViewById(R.id.currGoal);
        //currentGoal.setHint("Set Goal");

            System.out.println("goal is......." + currGoalNum );

        Button setGoalBtn = (Button)findViewById(R.id.goalBtn);
        setGoalBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                setGoal();
            }
        });

        // Keep track of user preferences
        userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);


        userDocString = userSharedPref.getString("userIDinDB", "");


        currGoalNum = userSharedPref.getLong("stepGoal", 0);
        currentGoal.setText(""+currGoalNum);

        height = userSharedPref.getInt("height",-1);
        stepsCount = userSharedPref.getLong("steps", 0);
        walkStepsCount = userSharedPref.getLong("walkedSteps", 0);

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
                    walkCurrentStepsCount = 0;
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
                    walkCurrentStepsCount =  stepper.getSteps(stepsCount);
                    walkStepsCount = walkStepsCount + walkCurrentStepsCount;
                    fetchWalkedStepsArray();
                    walkDistance = DistanceCalculator.stepsToDistance(walkCurrentStepsCount, height);
                    walkTime = timer.getWalkTime();
                    walkSpeed = SpeedCalculator.walkingSpeed(walkDistance, walkTime);

                    // Provide the user information about the walk
                    toaster("You walked for " + walkCurrentStepsCount + " steps over " + walkTime / 1000
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


        Button addfriendsBtn = (Button)findViewById(R.id.viewFriendsBtn);
        addfriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewFriendsPage();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 134);

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

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            userDocString = task.getResult().getEmail();

        } catch (Exception e) {
            e.printStackTrace();
        }




        if (resultCode == RESULT_OK) {
            SharedPreferences.Editor editor = userSharedPref.edit();
            editor.putString("userIDinDB", userDocString).apply();


            if (requestCode == fitnessService.getRequestCode()) {

                fitnessService.updateStepCount();
                DistanceCalculator.stepsToDistance(stepsCount, height);

            }
            addNewUser();
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void addNewUser(){
        //code for adding a new user to the database and for checking if the user already exists in
        //the database, then not add that user to the database.
        //userDocString = userSharedPref.getString("userIDinDB", "");
        if(userDocString.equals("")){
            System.out.println("userDocString is null....................................");
        }
        else {
            db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    int numOfDocs = task.getResult().size();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        numOfDocs = numOfDocs - 1;
                                        if (document.getId().equals(userDocString)) {
                                            userInDBBool = true;
                                            System.out.println("checkuserindatabase if case................................");
                                            return;
                                        } else {
                                            if (numOfDocs == 0) {
                                                stepsCount = 0;
                                                editPref = userSharedPref.edit();
                                                editPref.putLong("steps", 0);
                                                editPref.putLong("walkedSteps", 0);
                                                editPref.apply();
                                                // Create a new user with a first and last name
                                                Map<String, Object> user = new HashMap<>();
                                                ArrayList<String> regStepsDataArr = new ArrayList<String>();
                                                regStepsDataArr.add(0, ""+stepsCount);
                                                ArrayList<String> walkedStepsDataArr = new ArrayList<String>();
                                                walkedStepsDataArr.add(0, "" + walkStepsCount);
                                                user.put("regularStepsData", regStepsDataArr);
                                                user.put("walkedStepsData", walkedStepsDataArr);

                                                System.out.println("userDocString is elseif case...................................." + userDocString);

                                                // Add a new document with a generated ID
                                                db.collection("users")
                                                        .document(userDocString).set(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void v) {

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error adding document", e);
                                                            }
                                                        });
                                            }
                                        }

                                    }
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }//end of add user to DB code

    }


    /**
     * Set the number of steps
     */



    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount) + " Steps");
        SharedPreferences.Editor editor = userSharedPref.edit();
        if(stepsCount >= userSharedPref.getLong("stepGoal", 0) && onlyShowGoalAlertOnceCounter == 0){
            onlyShowGoalAlertOnceCounter++;
            updateGoal();
        }
        //System.out.println("INSIDE STEP COUNT FUNCTION..............................");
        this.stepsCount = stepCount;

        if(this.stepsCount == 0 && walkStepsCount != 0 && (userSharedPref.getLong("steps", 0) != 0)){
            System.out.println("ADD AN ARRAY ELEMENT IN FIREBASE..............................");
            walkStepsCount = 0;//update that to zero to reflect a new day
            regSteps.add(regSteps.size()-1, ""+0);
            walkedSteps.add(walkedSteps.size()-1, ""+0);
            updateRegStepsInDB(regSteps);
            updateWalkedStepsInDB(walkedSteps);
        }
        else if(userSharedPref.getLong("steps", 0) == this.stepsCount){
            System.out.println("DON'T ADD AN ARRAY ELEMENT IN FIREBASE, no update required.....");
            System.out.println(this.stepsCount + "," + userSharedPref.getLong("steps", 0));

        }
        else if(userSharedPref.getLong("steps", 0) < this.stepsCount) {
            System.out.println("UPDATE STEPCOUNT IN FIREBASE..............................");
            fetchRegStepsArray();
            fetchWalkedStepsArray();
            //System.out.println("REG STEPS ARRAY IS.............................." + regSteps);

        }

        editor.putLong("steps", stepCount);
        editor.putLong("walkedSteps", walkStepsCount);
        editor.apply();
    }

    public void goToViewFriendsPage(){
        Intent intent = new Intent(this, ViewFriendsActivity.class);
        startActivity(intent);
    }

    public void updateRegStepsInDB(ArrayList<String> regSteps){
        DocumentReference user = db.collection("users").document(userDocString);
        user.update("regularStepsData", regSteps);
        System.out.println("regSteps in update method are!!!!!!!!!!!!!!!" + regSteps);

    }

    public void updateWalkedStepsInDB(ArrayList<String> walkedSteps){
        DocumentReference user = db.collection("users").document(userDocString);
        user.update("walkedStepsData", walkedSteps);
        System.out.println("walkedSteps in update method are!!!!!!!!!!!!!!!" + walkedSteps);
    }

    //get the current steps arrays from the DB
    public void fetchRegStepsArray(){
        //hardcoded step values for Monday to Saturday since I didn't have access to an android phone then.
        db = FirebaseFirestore.getInstance();
        userDocString = userSharedPref.getString("userIDinDB", "");

        if(!userDocString.equals("")) {
            DocumentReference user = db.collection("users").document(userDocString);

            user.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                regSteps = (ArrayList<String>) task.getResult().get("regularStepsData");
                                //System.out.println("regSteps.................." + regSteps);
                                int index = regSteps.size() - 1;
                                if(index >= 0) {

                                    regSteps.set(index, "" + stepsCount);
                                    updateRegStepsInDB(regSteps);
                                }


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }

    }

    public void fetchWalkedStepsArray(){
        //hardcoded step values for Monday to Saturday since I didn't have access to an android phone then.
        db = FirebaseFirestore.getInstance();
        userDocString = userSharedPref.getString("userIDinDB", "");

        if(!userDocString.equals("")) {
            DocumentReference user = db.collection("users").document(userDocString);

            user.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                walkedSteps = (ArrayList<String>) task.getResult().get("walkedStepsData");

                               // System.out.println("walkedSteps.................." + walkedSteps);

                                int index = walkedSteps.size()-1;
                                if(index >= 0) {
                                    walkedSteps.set(index, "" + walkStepsCount);
                                    updateWalkedStepsInDB(walkedSteps);
                                }


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }

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
    public void goToChartsPage() {
        chartsIntent = new Intent(this, StepsChart.class);
            chartsIntent.putExtra("mySteps", stepsCount);
            chartsIntent.putExtra("walkedSteps", walkStepsCount);
            chartsIntent.putExtra("goal", currGoalNum);
            startActivity(chartsIntent);

    }

    /**
     * Updates your personal best
     */
    public void updatePersonalBest(){
        //System.out.println("Personal Best is " + userSharedPref.getLong("userdata", 0));
        if (userSharedPref.getLong("personalBest", 0) < stepsCount) {
            SharedPreferences.Editor editor = userSharedPref.edit();
            editor.putLong("personalBest", stepsCount);
            editor.apply();
        }
        TextView personalBest = (TextView)findViewById(R.id.personalBest);
        long bestSteps= userSharedPref.getLong("personalBest",0);
        personalBest.setText("Best: " + String.valueOf(bestSteps) + " Steps");
    }

    public void setGoal(){
        SharedPreferences.Editor editor = userSharedPref.edit();
        currGoalNum = Integer.parseInt(currentGoal.getText().toString());
        editor.putLong("stepGoal", currGoalNum);
        editor.apply();
        currentGoal.setText("" + currGoalNum);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Your new daily steps goal is " + currGoalNum,
                Toast.LENGTH_SHORT);

        toast.show();
    }

    public void updateGoal(){
        if(stepsCount >= userSharedPref.getLong("stepGoal", 0)){
            AlertDialog.Builder updateGoalDialog = new AlertDialog.Builder(this);
            updateGoalDialog.setMessage("Congrats on reaching your goal!!\nWould you like to update your\n" +
                    "goal to " + String.valueOf(currGoalNum+500) + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currGoalNum = currGoalNum + 500;
                            //updating the currentGoal
                            SharedPreferences.Editor editor = userSharedPref.edit();
                            editor.putLong("stepGoal", currGoalNum).apply();
                            currentGoal.setText("" + currGoalNum);
                            dialog.dismiss();
                            onlyShowGoalAlertOnceCounter = 0;

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            AlertDialog alert = updateGoalDialog.create();
            alert.setTitle("Update Goal!");
            alert.show();


        }
    }

}

