package gridwatch.kplc.activities.billing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import gridwatch.kplc.R;

/**
 * Created by guoxinyi on 10/27/16.
 */

public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("mylog","receive");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        Toast.makeText(context, msgBody, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
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
        }
        start = end;
        end = msgBody.indexOf("Prev Read");
        if(start > 0 && end > start && end < msgBody.length()) {
            currR = msgBody.substring(start + 10, end);
            Log.d("Curr Read", currR);
        }
        start = end;
        end = msgBody.indexOf("Estimated");
        if(start > 0 && end > start && end < msgBody.length()) {
            prevR = msgBody.substring(start + 10, end);
            Log.d("Prev Read", prevR);
        }
        start = end;
        end = msgBody.indexOf("Amount");
        if(start > 0 && end > start && end < msgBody.length()) {
            units = msgBody.substring(start + 16, end);
            Log.d("Estimated units", units);
        }
        start = end;
        end = msgBody.indexOf("Fuel");
        if(start > 0 && end > start && end < msgBody.length()) {
            amount = msgBody.substring(start + 7, end);
            Log.d("Amount", amount);
        }
        start = end;
        end = msgBody.indexOf("Levies");
        if(start > 0 && end > start && end < msgBody.length()) {
            fuel = msgBody.substring(start + 10, end);
            Log.d("Fuel cost", fuel);
        }
        start = end;
        end = msgBody.indexOf("Taxes");
        if(start > 0 && end > start && end < msgBody.length()) {
            levies = msgBody.substring(start + 7, end);
            Log.d("Levies", levies);
        }
        start = end;
        end = msgBody.indexOf("Prev Balance");
        if(start > 0 && end > start && end < msgBody.length()) {
            taxes = msgBody.substring(start + 6, end);
            Log.d("Taxes", taxes);
        }
        start = end;
        end = msgBody.indexOf("Due Date");
        if(start > 0 && end > start && end < msgBody.length()) {
            prevB = msgBody.substring(start + 13, end);
            Log.d("Previous Balance", prevB);
        }
    }
}
