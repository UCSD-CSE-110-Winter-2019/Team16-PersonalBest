package edu.ucsd.cse110.mainpage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StepsChart extends AppCompatActivity {

    private LineChart lineChart;
    private LineData lineData;
    ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_chart);

        lineChart = (LineChart)findViewById(R.id.linechart);
        //lineData = new LineData(getXvalues(), getLineDataValues());
        //lineChart.setData(lineData);
        ArrayList<String> xAxes = getXvalues();
        String[] xAxis = new String[xAxes.size()];
        for(int i = 0; i < xAxes.size(); i++){
            xAxis[i] = xAxes.get(i).toString();
        }
        LineDataSet lineDataSet = new LineDataSet(getLineDataValues(), "Steps Taken");
        lineDataSet.setDrawCircles(true);
        lineDataSet.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet);

        lineChart.setData(new LineData(xAxis, lineDataSets));

        lineChart.setVisibleXRangeMaximum(1000f);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);

    }

    private ArrayList<Entry> getLineDataValues() {
        ArrayList<Entry> entryArrayList = new ArrayList<>();
        Entry e1 = new Entry(3460f, 0);
        Entry e2 = new Entry(3400f, 1);
        Entry e3 = new Entry(6700f, 2);
        Entry e4 = new Entry(2000f, 3);
        Entry e5 = new Entry(5600f, 4);
        Entry e6 = new Entry(1500f, 5);
        Entry e7 = new Entry(1200f, 6);

        entryArrayList.add(e1);
        entryArrayList.add(e2);
        entryArrayList.add(e3);
        entryArrayList.add(e4);
        entryArrayList.add(e5);
        entryArrayList.add(e6);
        entryArrayList.add(e7);

        return entryArrayList;
    }

    private ArrayList<String> getXvalues() {
        ArrayList<String> xvalues = new ArrayList<>();
        xvalues.add("Monday");
        xvalues.add("Tuesday");
        xvalues.add("Wednesday");
        xvalues.add("Thursday");
        xvalues.add("Friday");
        xvalues.add("Saturday");
        xvalues.add("Sunday");
        return xvalues;
    }
}
