package com.example.guoxinyi.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button sendBtn;
    EditText number;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = (Button) findViewById(R.id.button);
        number = (EditText) findViewById(R.id.editText);
        message = (EditText) findViewById(R.id.editText2);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getPermission();
            }
        });
    }
    protected void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    1);
        }else{
            sendMessage();
        }
    }
    protected void sendMessage() {
        String txtNumber = number.getText().toString();
        String txtMessage = message.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(txtNumber, null, txtMessage, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
