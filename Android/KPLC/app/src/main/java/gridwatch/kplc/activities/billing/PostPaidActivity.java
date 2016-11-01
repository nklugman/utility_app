package gridwatch.kplc.activities.billing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gridwatch.kplc.R;
// only sending sms function
public class PostPaidActivity extends Activity {


    Button sendBtn;
    String number = "4084764087";
    String message = "Dear <name withheld>,ACCOUNT NO:4369164-01, Curr bill dated 03-08-2016 is KShs:-5785.72 " +
            "Curr Read:0 Prev Read:8122,Estimated Units:154KWh,Amount:2726.7 Fuel Cost:356,Levies:255,Taxes:365. " +
            "Prev Balance is KShs -8512.42 Due date 09-08-2016. Currently no bill to pay , Thank You.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_paid);
        sendBtn = (Button) findViewById(R.id.button);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getPermission();
            }
        });
    }
    protected void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    2);
        }else{
            sendMessage();
        }
    }
    public int sendMessage() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            return 1;

        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendMessage();
            } else {
                Toast.makeText(getApplicationContext(), "Can't send SMS without permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
