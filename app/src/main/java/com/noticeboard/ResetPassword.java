package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private TextInputLayout textEmailLayout;
    EditText resetemail;
    Button resetpwdbutton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        auth = FirebaseAuth.getInstance();

        textEmailLayout = (TextInputLayout) findViewById(R.id.resetEmailTIL);
        resetemail = (EditText) findViewById(R.id.resetemail);
        resetpwdbutton = (Button) findViewById(R.id.resetbutton);

        resetpwdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){

                    String email = resetemail.getText().toString().trim();


                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(ResetPassword.this,"Please check your email account",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, Login.class));
                            }

                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPassword.this,"Error occured:" +message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }

    private boolean validate() {
        boolean valid = true;

        String email = resetemail.getText().toString().trim();

        if (email.isEmpty()){
            resetemail.setError("Email Address cannot be empty");
            valid = false;
        }

        else if (!email.contains(".") ||!email.contains("@")) {
            resetemail.setError("Bad Email Address Format!");
            valid = false;
        }
        else{resetemail.setError(null);}

        return valid;
    }
}
