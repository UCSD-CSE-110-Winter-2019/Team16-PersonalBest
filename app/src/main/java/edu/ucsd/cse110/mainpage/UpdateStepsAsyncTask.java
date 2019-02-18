package edu.ucsd.cse110.mainpage;

import android.os.AsyncTask;

import edu.ucsd.cse110.mainpage.fitness.FitnessService;

public class UpdateStepsAsyncTask extends AsyncTask {

    private FitnessService service;

    public UpdateStepsAsyncTask(FitnessService service) {
        this.service = service;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while(true) {
            publishProgress();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //return null;
        }
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        service.updateStepCount();
    }

    //@Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
