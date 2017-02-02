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

import gridwatch.kplc.R;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class UsageChartsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        LinearLayout chart = (LinearLayout) findViewById(R.id.chart);
        XYSeries series = new XYSeries("Usage");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int hour = 0;
        for (int i = 0; i < 12; i++) {
            series.add(i + 201601, Math.random()*200);
        }
        dataset.addSeries(series);
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setDisplayChartValues(true);

        multiRenderer.addSeriesRenderer((mRenderer));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setMarginsColor(Color.WHITE);
        multiRenderer.setXLabels(12);
        multiRenderer.setDisplayValues(true);
        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, multiRenderer);
        chart.addView(chartView);


    }

}
