package com.example.myapplicationn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;

public class SendMessageActivity extends Activity {
    Button send_btn;
    Button call_btn;
    EditText phone_num;
    EditText message;
    TextView word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        send_btn = (Button) findViewById(R.id.send_button);
        call_btn = (Button) findViewById(R.id.call_button);
        phone_num = (EditText) findViewById(R.id.num_edit);
        message = (EditText) findViewById(R.id.sms_edit);
        word = (TextView) findViewById(R.id.message_word);

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 전화 걸기
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-3362-9085"));
                startActivity(mIntent);
            }
        });

        //버튼 클릭이벤트
        send_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //입력한 값을 가져와 변수에 담는다
                String num = phone_num.getText().toString();
                String sms = message.getText().toString();

                try {
                    //전송
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "메세지 전송이 완료되었습니다!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "메세지 전송을 실패했습니다. 다시 시도해주세요!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                byte[] bytes = null;

                try {

                    bytes = charSequence.toString().getBytes("KSC5601"); // 한글 완성형 표준
                    int strCount = bytes.length;
                    word.setText(strCount + " / 500바이트");
                } catch(UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            } // 텍스트박스의 내용이 바뀔때마다 글자수 출력

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                try {
                    byte[] strBytes = str.getBytes("KSC5601"); // 한글 완성형 표준
                    if(strBytes.length > 500){
                        editable.delete(editable.length()-2, editable.length()-1);
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            } // 글자수 제한하기
        };
        message.addTextChangedListener(textWatcher);

    }


}