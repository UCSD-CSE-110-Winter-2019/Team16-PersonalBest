package edu.ucsd.cse110.mainpage.fitness;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucsd.cse110.mainpage.MainActivity;

public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    String SCOPE = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
    private final String TAG = "GoogleFitAdapter";

    public String username;
    public String userEmail;
    public String userDocString;
    long total;
    public List<String> regularStepsData;
    public DocumentReference graphData;
    GoogleSignInOptions gso;

    private MainActivity activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GoogleFitAdapter(MainActivity activity) {
        this.activity = activity;
    }


    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();




        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);


            System.out.println("GOOGLE ACCOUNT if..................." + GoogleSignIn.getLastSignedInAccount(activity));


        } else {

            updateStepCount();
            startRecording();
            System.out.println("GOOGLE ACCOUNT else..................." + GoogleSignIn.getLastSignedInAccount(activity).getAccount());



        }

    }

    private void startRecording() {
        final GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        /*System.out.println("GOOGLE ACCOUNT start...................******************************" + GoogleSignIn.getLastSignedInAccount(activity));
        SharedPreferences pref = activity.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userIDinDB", "").apply();

        if(pref.getString("userIDinDB", "").equals("")){
            editor.putString("userIDinDB", ""+ GoogleSignIn.getLastSignedInAccount(activity));
            System.out.println("GOOGLE ACCOUNT start if condition..................." + GoogleSignIn.getLastSignedInAccount(activity).getEmail());


        }

        editor.apply();*/


        Fitness.getRecordingClient(activity, lastSignedInAccount)
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });

    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }
        //System.out.println("GOOGLE ACCOUNT update..................." + GoogleSignIn.getLastSignedInAccount(activity));
        Fitness.getHistoryClient(activity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                //Log.d(TAG, dataSet.toString());
                                total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                activity.setStepCount(total);
                                //Log.d(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the step count.", e);
                            }
                        });



    }


    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }
}
