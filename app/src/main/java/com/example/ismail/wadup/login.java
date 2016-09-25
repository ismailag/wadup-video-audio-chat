package com.example.ismail.wadup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private EditText email , pw;
    private Button login , signiup;
    private ProgressDialog progress ;
    private FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progress=new ProgressDialog(this) ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.email);
        pw=(EditText) findViewById(R.id.password) ;
        login=(Button) findViewById(R.id.login);
        signiup=(Button) findViewById(R.id.signup);
        auth= FirebaseAuth.getInstance();
    }
    void register(View v)
    {
        final String email = this.email.getText().toString().trim() ;
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter your Email",Toast.LENGTH_SHORT).show();
            return;
        }
        String pw=this.pw.getText().toString().trim() ;
        if (TextUtils.isEmpty(pw))
        {
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (pw.length()<6)
        {
            Toast.makeText(this,"Password should contain more than 6 chars",Toast.LENGTH_SHORT).show();
            return;
        }
    if (v==signiup)
    {auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful())
            {

                Intent intent = new Intent(getApplicationContext(), Users.class);
                //ntent.putExtra("callerId",email);
                //intent.putExtra("recipientId", "jj");
                startActivity(intent);
                progress.dismiss();
                finish() ;

            }
            else
            {progress.cancel();Toast.makeText(login.this,"Could not register",Toast.LENGTH_LONG).show();}
            }

    }) ;
        progress.setMessage("Registering the new user ...");
        progress.show();
    }

    else
    {
        auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(getApplicationContext(), Users.class);
                   // intent.putExtra("callerId",email);
                    //intent.putExtra("recipientId", "jj");
                    progress.dismiss();
                    startActivity(intent);
                    finish() ;
                }
                else
                {progress.cancel();Toast.makeText(login.this,"Could not login",Toast.LENGTH_LONG).show();}
            }

        }) ;
        progress.setMessage("Cheking your account ...");
        progress.show();
    }
    }

}
