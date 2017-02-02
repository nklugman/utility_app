package gridwatch.kplc.activities.billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import gridwatch.kplc.R;


/**
 * Created by guoxinyi on 1/19/17.
 */

public class StatementHistoryActivity extends AppCompatActivity{
    private final int MAX_YEAR = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_statement);
        ListView list = (ListView) findViewById(R.id.listView);


        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<30;i++)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("history_date", "201610");
            map.put("history_balance", "PDF");
            mylist.add(map);
        }

        SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.listitem,
                new String[] {"history_date", "history_balance"},
                new int[] {R.id.history_date,R.id.history_balance});
        //添加并且显示
        list.setAdapter(mSchedule);
        Calendar cal = Calendar.getInstance();
        String[] year_spinner=new String[MAX_YEAR];
        int year = cal.get(Calendar.YEAR);
        for (int i = 0; i < MAX_YEAR; i++) {
            year_spinner[i] = String.valueOf(year--);
        }
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
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
}
