package com.example.myapplicationn;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class EmergencyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);



        Button call_btn;
        Button message_btn;


        call_btn = (Button)findViewById(R.id.call_button);
        message_btn = (Button)findViewById(R.id.send_button);

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 전화 걸기
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"));
                startActivity(mIntent);
            }
        });
/*
        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(StartActivity.this, SendMessageActivity.class);
                startActivity(mIntent);

            }
        });

*/







    }


}
