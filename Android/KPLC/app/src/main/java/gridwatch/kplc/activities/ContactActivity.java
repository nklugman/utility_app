package gridwatch.kplc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gridwatch.kplc.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        final EditText name = (EditText) findViewById(R.id.feedbackbox);
        Button email = (Button) findViewById(R.id.send);
        email.setOnClickListener(new View.OnClickListener()  {

            @Override
            public void onClick(View v){
                //TODO Auto-generated method stub
                Intent email = new Intent(android.content.Intent.ACTION_SEND);

                email.setType("plain/text");
                email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"gridwatch.capstone@gmail.com"});
                email.putExtra(android.content.Intent.EXTRA_SUBJECT, "KPLC Contact Us");
                email.putExtra(android.content.Intent.EXTRA_TEXT,
                        name.getText().toString());

                startActivity(Intent.createChooser(email, "Send mail..."));
            }
        });
    }
}
