package com.example.ismail.wadup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;

public class First_activity extends AppCompatActivity {
private String strID ;
private String  recipientId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_activity);
        TextView id= (TextView) findViewById(R.id.yourid) ;
        strID=SessionId();
        id.setText("Your Id : "+strID);
        setTitle("");
    }
    public String SessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(10, random).toString(32);
}
public void next (View v)
{EditText rid= (EditText) findViewById(R.id.recipint) ;
    DataHolder.getInstance().setType(v.getId());
    recipientId=rid.getText().toString() ;
    Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
    intent.putExtra("callerId", strID);
    intent.putExtra("recipientId", recipientId);
    startActivity(intent);
    finish() ;
}
}
