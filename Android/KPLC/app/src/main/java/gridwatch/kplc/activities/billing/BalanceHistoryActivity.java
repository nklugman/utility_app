package gridwatch.kplc.activities.billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gridwatch.kplc.R;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class BalanceHistoryActivity extends AppCompatActivity {
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_history);
        ListView list = (ListView) findViewById(R.id.listView);

        realm = Realm.getDefaultInstance();

        /*realm.beginTransaction();

        realm.deleteAll();
        realm.commitTransaction();
        realm.beginTransaction();
        for(int i=0;i<12;i++)
        {
            BalanceHistory item = realm.createObject(BalanceHistory.class); // Create a new object
            item.setAccount(0);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2016);
            cal.set(Calendar.MONTH, i);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            item.setDate(cal.getTime());
            item.setBalance(Math.random()*30);
            item.setUsage(Math.random()*200);
        }
        realm.commitTransaction();
        */
        Log.i("MIN_YEAR", "start");
        Date min = realm.where(BalanceHistory.class).minimumDate("date");
        Date max = realm.where(BalanceHistory.class).maximumDate("date");
        Calendar cal = Calendar.getInstance();
        cal.setTime(min);
        final int MIN_YEAR = cal.get(Calendar.YEAR);
        cal.setTime(max);
        final int MAX_YEAR = cal.get(Calendar.YEAR);
        int interval = MAX_YEAR - MIN_YEAR + 1;
        String[] year_spinner=new String[interval];
        Log.i("MIN_YEAR", String.valueOf(MIN_YEAR));
        Log.i("MAX_YEAR", String.valueOf(MAX_YEAR));
        for (int i = 0; i < interval; i++) {
            year_spinner[i] = String.valueOf(i + MIN_YEAR);
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


        ArrayList<HashMap<String, String>> mylist = requestFromRealm(MIN_YEAR, 0, MAX_YEAR, 11);

        SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.listitem,
                new String[] {"history_date", "history_balance"},
                new int[] {R.id.history_date,R.id.history_balance});
        list.setAdapter(mSchedule);

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.i("SPINNER", "1");
                Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
                Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
                Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
                int year1 = spinner1.getSelectedItemPosition();
                int month1 = spinner2.getSelectedItemPosition();
                int year2 = spinner3.getSelectedItemPosition();
                int month2 = position;
                ListView list = (ListView) findViewById(R.id.listView);

                ArrayList<HashMap<String, String>> mylist = requestFromRealm(MIN_YEAR + year1, month1, MIN_YEAR + year2, month2);
                Log.i("YEAR1", String.valueOf(year1));
                Log.i("month2", String.valueOf(month2));

                SimpleAdapter mSchedule = new SimpleAdapter(getBaseContext(), mylist, R.layout.listitem,
                        new String[] {"history_date", "history_balance"},
                        new int[] {R.id.history_date,R.id.history_balance});
                list.setAdapter(mSchedule);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                ListView list = (ListView) findViewById(R.id.listView);
                ArrayList<HashMap<String, String>> mylist = requestFromRealm(MIN_YEAR, 0, MAX_YEAR, 11);

                SimpleAdapter mSchedule = new SimpleAdapter(getBaseContext(), mylist, R.layout.listitem,
                        new String[] {"history_date", "history_balance"},
                        new int[] {R.id.history_date,R.id.history_balance});
                list.setAdapter(mSchedule);
            }

        });

    }
    private ArrayList<HashMap<String, String>> requestFromRealm(int year1, int month1, int year2, int month2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.YEAR, year1);
        cal1.set(Calendar.MONTH, month1);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, year2);
        cal2.set(Calendar.MONTH, month2);
        cal2.set(Calendar.DAY_OF_MONTH, 1);
        Log.i("YEAR1", String.valueOf(cal2));
        Log.i("month2", String.valueOf(cal2));
        RealmResults<BalanceHistory> items = realm.where(BalanceHistory.class).lessThan("date", cal2.getTime()).greaterThanOrEqualTo("date", cal1.getTime()).findAllSorted("date");
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(BalanceHistory item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getDate());
            String date;
            int month = cal.get(Calendar.MONTH) + 1;
            if (month > 9) {
                date = cal.get(Calendar.YEAR) + "" + month;
            } else {
                date = cal.get(Calendar.YEAR) + "0" + month;
            }
            map.put("history_date", date);
            map.put("history_balance", String.valueOf((double)Math.round(item.getBalance() * 100) / 100));
            mylist.add(map);
        }
        return mylist;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
