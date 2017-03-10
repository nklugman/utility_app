package gridwatch.kplc.activities.news_feed;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.util.Date;
import java.util.HashMap;

import gridwatch.kplc.R;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by guoxinyi on 1/19/17.
 */

public class NewsfeedActivity extends AppCompatActivity{
    private Realm realm;
    String MAX_TIME;

    EditText inputSearch;


    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        listView = (ListView) findViewById(R.id.listview_newsfeed);
        list = new ArrayList<HashMap<String, Object>>();
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String application_host_server = prefs.getString("setting_key_application_host_server", "graphs.grid.watch");
        final String application_host_port = prefs.getString("setting_key_application_host_port", "3100");

        final String SERVER = application_host_server + ":" + application_host_port;
        //final String SERVER = "http://192.168.1.10:3100";
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        adapter = new SimpleAdapter(getBaseContext(), list, R.layout.newsfeed_listitem,
                new String[] {"newsfeedlogo", "newsfeedsource", "newsfeedtime", "newsfeedcontent"},
                new int[] {R.id.newsfeedlogo, R.id.newsfeedsource, R.id.newsfeedtime, R.id.newsfeedcontent});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                HashMap<String, Object> current= (HashMap) adapter.getItemAtPosition(position);

                TextView contentText = (TextView) v.findViewById(R.id.newsfeedcontent);
                int lines = TextViewCompat.getMaxLines(contentText);
                if (lines == 3) {
                    contentText.setMaxLines(Integer.MAX_VALUE);
                } else {
                    contentText.setMaxLines(3);
                    //contentText.setText(trueContent);
                }

            }
        });
        display();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //String currentTime = "2017-02-01%2000:00:00%2B3";
                Date max = realm.where(Newsfeed.class).maximumDate("time");
                MAX_TIME = getStringFromDate(max);
                new RefreshContent().execute(MAX_TIME, SERVER);

            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Log.i("list", cs.toString());
                /*if (NewsfeedActivity.this.adapter != null) {
                    NewsfeedActivity.this.adapter.getFilter().filter(cs);
                } else {
                    Log.e("list", "null adapter");
                }*/
                ArrayList<HashMap<String, Object>> tempList= new ArrayList<>();
                for(int i=0 ;i< list.size();i++) {
                    if (list.get(i).get("newsfeedcontent").toString().toLowerCase().contains(cs.toString().toLowerCase())) {
                        tempList.add(list.get(i));
                        Log.i("good", list.get(i).get("newsfeedcontent").toString());
                    }
                }
                adapter = new SimpleAdapter(getBaseContext(), tempList, R.layout.newsfeed_listitem,
                        new String[] {"newsfeedlogo", "newsfeedsource", "newsfeedtime", "newsfeedcontent"},
                        new int[] {R.id.newsfeedlogo, R.id.newsfeedsource, R.id.newsfeedtime, R.id.newsfeedcontent});
                listView.setAdapter(adapter);


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }



    private String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(date);
    }
    private String getShortStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    private Date getDateFromString(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {

            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void display(){
        requestFromRealm();

    }
    private void storeToRealm(String result) {
        if (result == null) {
            return;
        }
        result = result.replaceAll("\\r", System.getProperty("line.separator"));
        //Log.i("a",result);
        try {
            JSONArray json = new JSONArray(result);
            int length = json.length();
            realm.beginTransaction();
            for(int i = 0; i < length; i++){//遍历JSONArray
                JSONObject oj = json.getJSONObject(i);
                String source = oj.getString("source");
                String time = oj.getString("time");
                String content = oj.getString("content");
                Newsfeed item = realm.createObject(Newsfeed.class);

                item.setSource(Integer.parseInt(source));
                item.setContent(content);
                item.setTime(getDateFromString(time));

            }
            realm.commitTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void requestFromRealm() {
        RealmResults<Newsfeed> items;
        if(MAX_TIME == null) {
            items = realm.where(Newsfeed.class).findAllSorted("time", Sort.DESCENDING);
        } else {
            items = realm.where(Newsfeed.class).greaterThan("time", getDateFromString(MAX_TIME)).findAllSorted("time", Sort.DESCENDING);
        }
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
            map.put("newsfeedtime", getShortStringFromDate(time));
            map.put("newsfeedcontent", content);

            list.add(map);
            adapter = new SimpleAdapter(getBaseContext(), list, R.layout.newsfeed_listitem,
                    new String[] {"newsfeedlogo", "newsfeedsource", "newsfeedtime", "newsfeedcontent"},
                    new int[] {R.id.newsfeedlogo, R.id.newsfeedsource, R.id.newsfeedtime, R.id.newsfeedcontent});
            listView.setAdapter(adapter);
        }

    }
    private class RefreshContent extends AsyncTask<String, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(String... time) {
            Log.i("max_time", "refresh");
            String result = null;
            try {
                result = connectToServer(time[0], time[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result != null) {
                //Log.d("debugTest", result);
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
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private String connectToServer(String time, String server) throws IOException {
        URL url = new URL(server + "/newsfeed?time=" + time);
        Log.e("url", url.toString());
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
