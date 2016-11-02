package gridwatch.kplc.activities.billing;

import android.app.Activity;
import android.app.ActivityManager;
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

import java.util.List;

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
                        Intent it =new Intent(context, PostPaidDetail.class);
                        //it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        it.putExtra("msgBody", msgBody);
                        context.startActivity(it);
                        //parseMessage(msgBody);


                    }
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
    private static void openApplicationFromBackground(Context context, String msgBody) {
        Intent intent;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (!list.isEmpty() && list.get(0).topActivity.getPackageName().equals(context.getPackageName())) {

            return;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())) {
                intent = new Intent();
                intent.setComponent(info.topActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (! (context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                //intent.putExtra("msgBody",msgBody);
                context.startActivity(intent);
                return;
            }
        }
        intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intent);

    }

}
