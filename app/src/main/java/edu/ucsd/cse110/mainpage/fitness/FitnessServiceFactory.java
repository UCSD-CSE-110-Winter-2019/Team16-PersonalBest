package edu.ucsd.cse110.mainpage.fitness;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.ucsd.cse110.mainpage.MainActivity;

public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static FitnessService create(String key, MainActivity stepCountActivity) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        // v returns null
        return blueprints.get(key).create(stepCountActivity);
    }

    public interface BluePrint {
        FitnessService create(MainActivity stepCountActivity);
    }
}
