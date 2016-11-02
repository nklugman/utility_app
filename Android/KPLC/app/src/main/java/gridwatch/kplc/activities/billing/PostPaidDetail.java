package gridwatch.kplc.activities.billing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import gridwatch.kplc.R;


/**
 * Created by guoxinyi on 11/1/16.
 */

public class PostPaidDetail extends Activity {

    ArrayList<HashMap<String, String>> billDetail = new ArrayList<>();

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpaid_detail);
        list = (ListView) findViewById(R.id.bill);
        Intent intent = getIntent();
        String msgBody = intent.getStringExtra("msgBody");
        parseMessage(msgBody);

    }
    private void parseMessage(String msgBody){

        msgBody = "Dear <name withheld>,ACCOUNT NO:4369164-01, Curr bill dated 03-08-2016 is KShs:-5785.72 " +
               "Curr Read:0 Prev Read:8122,Estimated Units:154KWh,Amount:2726.7 Fuel Cost:356,Levies:255,Taxes:365. " +
               "Prev Balance is KShs -8512.42 Due date 09-08-2016. Currently no bill to pay , Thank You.";

        String units;
        String amount;
        String fuel;
        String levies;
        String taxes;
        String currB;
        String prevB;
        String currR;
        String prevR;
        int start = 0;
        int end = 0;
        start = msgBody.indexOf("is KShs:");
        end = msgBody.indexOf("Curr Read");
        if(start > 0 && end > start && end < msgBody.length()) {
            currB = msgBody.substring(start + 8, end);
            Log.d("Current Bill", currB);
            addToList("Current Bill", currB);

        }
        start = end;
        end = msgBody.indexOf("Prev Read");
        if(start > 0 && end > start && end < msgBody.length()) {
            currR = msgBody.substring(start + 10, end);
            Log.d("Curr Read", currR);
            addToList("Curr Read", currR);
        }
        start = end;
        end = msgBody.indexOf("Estimated");
        if(start > 0 && end > start && end < msgBody.length()) {
            prevR = msgBody.substring(start + 10, end);
            Log.d("Prev Read", prevR);
            addToList("Prev Read", prevR);
        }
        start = end;
        end = msgBody.indexOf("Amount");
        if(start > 0 && end > start && end < msgBody.length()) {
            units = msgBody.substring(start + 16, end);
            Log.d("Estimated units", units);
            addToList("Estimated units", units);
        }
        start = end;
        end = msgBody.indexOf("Fuel");
        if(start > 0 && end > start && end < msgBody.length()) {
            amount = msgBody.substring(start + 7, end);
            Log.d("Amount", amount);
            addToList("Amount", amount);
        }
        start = end;
        end = msgBody.indexOf("Levies");
        if(start > 0 && end > start && end < msgBody.length()) {
            fuel = msgBody.substring(start + 10, end);
            Log.d("Fuel cost", fuel);
            addToList("Fuel cost", fuel);
        }
        start = end;
        end = msgBody.indexOf("Taxes");
        if(start > 0 && end > start && end < msgBody.length()) {
            levies = msgBody.substring(start + 7, end);
            Log.d("Levies", levies);
            addToList("Levies", levies);
        }
        start = end;
        end = msgBody.indexOf("Prev Balance");
        if(start > 0 && end > start && end < msgBody.length()) {
            taxes = msgBody.substring(start + 6, end);
            Log.d("Taxes", taxes);
            addToList("Taxes", taxes);
        }
        start = end;
        end = msgBody.indexOf("Due Date");
        if(start > 0 && end > start && end < msgBody.length()) {
            prevB = msgBody.substring(start + 13, end);
            Log.d("Previous Balance", prevB);
            addToList("Previous Balance", prevB);
        }



        SimpleAdapter mSchedule = new SimpleAdapter(this, billDetail, R.layout.postpaid_detail_listitem,
                new String[] {"ItemTitle", "ItemText"},
                new int[] {R.id.ItemTitle,R.id.ItemText});
        list.setAdapter(mSchedule);

    }
    private void addToList(String itemTitle, String itemText){
        HashMap<String, String> map = new HashMap<>();
        map.put("ItemTitle", itemTitle);
        map.put("ItemText", itemText);
        billDetail.add(map);
    }
}