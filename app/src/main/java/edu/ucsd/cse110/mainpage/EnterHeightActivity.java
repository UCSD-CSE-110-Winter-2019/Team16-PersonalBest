package edu.ucsd.cse110.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class EnterHeightActivity extends AppCompatActivity {

    NumberPicker feet;
    NumberPicker inches;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_height);

        feet = (NumberPicker) findViewById(R.id.feetPicker);
        inches = (NumberPicker) findViewById(R.id.inchesPicker);
        feet.setMinValue(0);
        feet.setMaxValue(10);
        inches.setMinValue(0);
        inches.setMaxValue(11);

        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);
                SharedPreferences.Editor editor = userSharedPref.edit();

                editor.putInt("height", (feet.getValue()*12 + inches.getValue()));
                editor.apply();

                setResult(RESULT_OK);

                finish();
            }
        });
    }
}
