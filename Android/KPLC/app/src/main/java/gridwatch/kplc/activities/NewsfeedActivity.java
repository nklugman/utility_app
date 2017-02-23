package gridwatch.kplc.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.Locale;


import gridwatch.kplc.R;
import gridwatch.kplc.activities.billing.Postpaid;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class NewsfeedActivity extends AppCompatActivity{
    private Realm realm;
    String MAX_TIME;
    private static final String SERVER= "http://192.168.1.5:3000";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> mylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        listView = (ListView) findViewById(R.id.listview_newsfeed);
        //mylist = new ArrayList<HashMap<String, Object>>();
        realm = Realm.getDefaultInstance();
        /*for(int i=0;i<2;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("newsfeedlogo", R.drawable.facebooklogo);
            map.put("newsfeedsource", "Facebook");
            map.put("newsfeedtime", "2015034");
            map.put("newsfeedcontent", "sfafda");
            list.add(map);
        }
        adapter = new SimpleAdapter(this, list, R.layout.newsfeed_listitem,
                new String[] {"newsfeedlogo", "newsfeedsource", "newsfeedtime", "newsfeedcontent"},
                new int[] {R.id.newsfeedlogo, R.id.newsfeedsource, R.id.newsfeedtime, R.id.newsfeedcontent});
        listView.setAdapter(adapter);*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String currentTime = "2017-02-01%2000:00:00%2B3";
                new RefreshContent().execute(currentTime);

            }
        });
    }
    private String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.ENGLISH);
        return format.format(date);
    }
    private Date getDateFromString(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void display(){
        Date max = realm.where(Postpaid.class).maximumDate("month");
        if (max == null) {
            return;
        }
        MAX_TIME = getStringFromDate(max);


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
    private void requestFromRealm() {
        RealmResults<Newsfeed> items = realm.where(Newsfeed.class).findAllSorted("time", Sort.DESCENDING);
        for(Newsfeed item : items) {
            HashMap<String, Object> map = new HashMap<>();
            int source = item.getSource();
            switch(source) {
                case 0:
                    map.put("newsfeedlogo", R.drawable.facebooklogo);
                    map.put("newsfeedsource", "Facebook");
                    break;
                case 1:
                    map.put("newsfeedlogo", R.drawable.twitterlogo);
                    map.put("newsfeedsource", "Twitter");
                    break;
            }
            String content = item.getContent();
            Date time = item.getTime();
            map.put("newsfeedtime", getStringFromDate(time));
            map.put("newsfeedcontent", content);
            mylist.add(map);
        }

    }
    private class RefreshContent extends AsyncTask<String, Void, String> {
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
            try {
                JSONArray json = new JSONArray(result);
                int length = json.length();
                for(int i = 0; i < length; i++){//遍历JSONArray
                    JSONObject oj = json.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    String source = oj.getString("source");
                    switch(source) {
                        case "0":
                            map.put("newsfeedlogo", R.drawable.facebooklogo);
                            map.put("newsfeedsource", "Facebook");
                            break;
                        case "1":
                            map.put("newsfeedlogo", R.drawable.twitterlogo);
                            map.put("newsfeedsource", "Twitter");
                            break;
                    }
                    map.put("newsfeedtime", oj.getString("time"));
                    map.put("newsfeedcontent", oj.getString("content"));
                    mylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new SimpleAdapter(getBaseContext(), mylist, R.layout.newsfeed_listitem,
                    new String[] {"newsfeedlogo", "newsfeedsource", "newsfeedtime", "newsfeedcontent"},
                    new int[] {R.id.newsfeedlogo, R.id.newsfeedsource, R.id.newsfeedtime, R.id.newsfeedcontent});
            listView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private String connectToServer(String time) throws IOException {
        URL url = new URL(SERVER + "/newsfeed?time=" + time);
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
}
