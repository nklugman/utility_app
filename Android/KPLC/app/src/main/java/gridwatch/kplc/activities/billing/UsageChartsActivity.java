package gridwatch.kplc.activities.billing;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

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
import java.util.HashMap;

import io.realm.Realm;
import gridwatch.kplc.R;
import io.realm.RealmResults;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class UsageChartsActivity extends AppCompatActivity {

    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        realm = Realm.getDefaultInstance();
        LinearLayout chart = (LinearLayout) findViewById(R.id.chart);
/*
        XYSeries series = new XYSeries("");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        RealmResults<BalanceHistory> items = realm.where(BalanceHistory.class).findAll();

        for(BalanceHistory item : items) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getDate());
            String date;
            int month = cal.get(Calendar.MONTH) + 1;
            if (month > 9) {
                date = cal.get(Calendar.YEAR) + "" + month;
            } else {
                date = cal.get(Calendar.YEAR) + "0" + month;
            }
            series.add(Integer.valueOf(date), (double)Math.round(item.getUsage() * 100) / 100);
        }
        dataset.addSeries(series);
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setDisplayChartValues(true);
        mRenderer.setChartValuesTextSize(20);
        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.addSeriesRenderer((mRenderer));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setXLabels(12);
        multiRenderer.setXTitle("Time");
        multiRenderer.setYTitle("Usage(kWh)");
        multiRenderer.setDisplayValues(true);
        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        //multiRenderer.setPanEnabled(true, false);
        double[] panLimits={series.getMinX(),series.getMaxX(),series.getMinY(), series.getMaxY()}; // [panMinimumX, panMaximumX, panMinimumY, panMaximumY]
        multiRenderer.setPanLimits(panLimits);
        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, multiRenderer);
        chart.addView(chartView);
*/

    }

}
