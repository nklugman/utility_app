package gridwatch.kplc.activities.billing;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gridwatch.kplc.R;
import gridwatch.kplc.activities.NewsfeedActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class StatementHistoryActivity extends AppCompatActivity {
    private Realm realm;
    private Spinner spinner1;
    private Spinner spinner2;
    private Spinner spinner3;
    private Spinner spinner4;
    private Button enter;
    private ListView list;
    private ArrayList<HashMap<String, String>> mylist;
    private static final String SERVER= "http://141.212.11.206:3100";//"http://192.168.1.5:3000";
    private String ACCOUNT = "3202667";
    private int MIN_YEAR;
    private int MAX_YEAR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_statement);
        list = (ListView) findViewById(R.id.listView);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner4 = (Spinner) findViewById(R.id.spinner4);
        enter = (Button) findViewById(R.id.statement_enter);


        realm = Realm.getDefaultInstance();
        Date max = realm.where(Postpaid.class).maximumDate("month");
        new RefreshData().execute(getStringFromDate(max));

        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("SPINNER", "1");
                int year1 = spinner1.getSelectedItemPosition();
                int month1 = spinner2.getSelectedItemPosition();
                int year2 = spinner3.getSelectedItemPosition();
                int month2 = spinner4.getSelectedItemPosition();
                if (checkDateInput(year1, month1, year2, month2)) {
                    mylist=requestStatementFromRealm(MIN_YEAR + year1, month1, MIN_YEAR + year2, month2);;

                    SimpleAdapter mSchedule = new SimpleAdapter(getBaseContext(), mylist, R.layout.listitem,
                            new String[]{"history_date", "history_balance"},
                            new int[]{R.id.history_date, R.id.history_balance});
                    list.setAdapter(mSchedule);
                    Log.i("refresh", "end");
                }
            }
        });

    }
    private void display(){
        Date min = realm.where(Postpaid.class).minimumDate("month");
        Date max = realm.where(Postpaid.class).maximumDate("month");
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
        Log.i("MIN_YEAR", String.valueOf(MIN_YEAR));
        Log.i("MAX_YEAR", String.valueOf(MAX_YEAR));
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
        mylist = requestStatementFromRealm(MIN_YEAR, 0, MAX_YEAR, 11);
        if (mylist == null) {
            return;
        }
        SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.listitem,
                new String[] {"history_date", "history_balance"},
                new int[] {R.id.history_date,R.id.history_balance});
        list.setAdapter(mSchedule);

    }
    private class RefreshData extends AsyncTask<String, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(String... time) {
            String result = null;
            try {
                result = connectToServer(time[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result != null) {
                Log.d("debugTest", result);
            } else {
                Log.d("debugTest", "wrong");
            }
            return result;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            storeToRealm(result);
            display();
        }
    }
    private void storeToRealm(String result) {
        if (result == null) {
            return;
        }
        try {
            JSONArray json = new JSONArray(result);
            int length = json.length();
            realm.beginTransaction();
            for(int i = 0; i < length; i++){//遍历JSONArray
                JSONObject oj = json.getJSONObject(i);
                String account = oj.getString("account");
                String usage = oj.getString("usage");
                String month = oj.getString("month");
                String balance = oj.getString("balance");
                String dueDate = oj.getString("dueDate");
                String payDate = oj.getString("payDate");
                String statement = oj.getString("statement");
                Postpaid item = realm.createObject(Postpaid.class); // Create a new object
                item.setAccount(account);
                item.setMonth(getDateFromString(month));
                item.setBalance(Double.parseDouble(balance));
                item.setUsage(Double.parseDouble(usage));
                item.setDueDate(getDateFromString(dueDate));
                item.setPayDate(getDateFromString(payDate));
                item.setStatement(statement);

            }
            realm.commitTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(date);
    }
    private Date getDateFromString(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Date getDateOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    private String connectToServer(String date) throws IOException {
        URL url = new URL(SERVER + "/postpaid?date=" + date + "&account=" + ACCOUNT);
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
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
    private ArrayList<HashMap<String, String>> requestStatementFromRealm(int year1, int month1, int year2, int month2) {
        Date min = getDateOfMonth(year1, month1);
        Date max = getDateOfMonth(year2, month2);
        RealmResults<Postpaid> items = realm.where(Postpaid.class).lessThanOrEqualTo("month", max).greaterThanOrEqualTo("month", min).findAllSorted("month", Sort.DESCENDING);
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(Postpaid item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getMonth());
            String date;
            int month = cal.get(Calendar.MONTH) + 1;
            if (month > 9) {
                date = cal.get(Calendar.YEAR) + "" + month;
            } else {
                date = cal.get(Calendar.YEAR) + "0" + month;
            }
            map.put("history_date", date);
            map.put("history_balance", item.getStatement() );
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
