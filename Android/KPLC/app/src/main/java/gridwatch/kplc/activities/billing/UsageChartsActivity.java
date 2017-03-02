package gridwatch.kplc.activities.billing;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import gridwatch.kplc.R;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class UsageChartsActivity extends AppCompatActivity {
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Button enter;
    private Realm realm;
    private LinearLayout chart;
    private int MIN_YEAR;
    private int MAX_YEAR;
    private GraphicalView chartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chart);
        realm = Realm.getDefaultInstance();
        chart = (LinearLayout) findViewById(R.id.chart);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner4 = (Spinner) findViewById(R.id.spinner4);
        enter = (Button) findViewById(R.id.usage_enter);
        display();
        displayChart(MIN_YEAR, 0, MAX_YEAR, 11);
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("SPINNER", "1");
                int year1 = spinner1.getSelectedItemPosition();
                int month1 = spinner2.getSelectedItemPosition();
                int year2 = spinner3.getSelectedItemPosition();
                int month2 = spinner4.getSelectedItemPosition();
                if (checkDateInput(year1, month1, year2, month2)) {
                    displayChart(MIN_YEAR + year1, month1, MIN_YEAR + year2, month2);
                }
            }
        });



    }
    private void display(){
        Log.i("SPINNER", "22222");
        Date min = realm.where(Postpaid.class).minimumDate("month");
        Date max = realm.where(Postpaid.class).maximumDate("month");
        Log.i("newsfeed minyear1", String.valueOf(min));
        Log.i("newsfeed MAX_YEAR1", String.valueOf(max));
        if (min == null || max == null) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(min);
        MIN_YEAR = cal.get(Calendar.YEAR);
        cal.setTime(max);
        MAX_YEAR = cal.get(Calendar.YEAR);
        int interval = MAX_YEAR - MIN_YEAR + 1;
        String[] year_spinner=new String[interval];
        for (int i = 0; i < interval; i++) {
            year_spinner[i] = String.valueOf(i + MIN_YEAR);
        }
        ArrayAdapter<CharSequence> year_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, year_spinner);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> month_adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_months, android.R.layout.simple_spinner_item);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(year_adapter);
        spinner3.setAdapter(year_adapter);
        spinner2.setAdapter(month_adapter);
        spinner4.setAdapter(month_adapter);


    }
    private Date getDateOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void displayChart(int year1, int month1, int year2, int month2) {
        XYSeries series = new XYSeries("");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        Date min = getDateOfMonth(year1, month1);
        Date max = getDateOfMonth(year2, month2);
        Log.i("newsfeed min", String.valueOf(min));
        Log.i("newsfeed max", String.valueOf(max));
        RealmResults<Postpaid> items = realm.where(Postpaid.class).lessThanOrEqualTo("month", max).greaterThanOrEqualTo("month", min).findAllSorted("month", Sort.ASCENDING);
        Log.i("newsfeed",items.toString());
        int i = 0;
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        for(Postpaid item : items) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getMonth());
            String date;
            int month = cal.get(Calendar.MONTH) + 1;
            if (month > 9) {
                date = cal.get(Calendar.YEAR) + "" + month;
            } else {
                date = cal.get(Calendar.YEAR) + "0" + month;
            }
            //Log.i("newsfeed time", date);
            //Log.i("newsfeed usage", ""+item.getUsage());
            multiRenderer.addXTextLabel(i, date);
            series.add(i++, (double)Math.round(item.getUsage() * 100) / 100);



        }
        if (chart != null && chartView != null)  {
            chart.removeView(chartView);

        }
        chartView = ChartFactory.getLineChartView(this, dataset, multiRenderer);
        chart.addView(chartView);
        dataset.addSeries(series);

        mRenderer.setDisplayChartValues(true);
        mRenderer.setChartValuesTextSize(20);
        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.addSeriesRenderer((mRenderer));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        //multiRenderer.setXLabels(12);

        multiRenderer.setXTitle("Time");
        multiRenderer.setYTitle("Usage(kWh)");
        multiRenderer.setDisplayValues(true);
        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        //multiRenderer.setPanEnabled(true, false);
        double[] panLimits={series.getMinX(),series.getMaxX(),series.getMinY(), series.getMaxY()}; // [panMinimumX, panMaximumX, panMinimumY, panMaximumY]
        multiRenderer.setPanLimits(panLimits);



    }
    private boolean checkDateInput(int year1, int month1, int year2, int month2) {
        if (year1 > year2) {
            return false;
        }
        if (year1 == year2 && month1 > month2) {
            return false;
        }
        return true;
    }
}
