package com.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.noticeboard.Utils.AppUtils;

public class SignUp extends AppCompatActivity {

    EditText emailaddress, password;
    FirebaseAuth firebaseAuth;
    Button signupbtn;
    ProgressBar progressBar;
    Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailaddress = findViewById(R.id.idemail);
        password = findViewById(R.id.pwd);
        signupbtn = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isNetworkConnected(context)) {

                    if (validate()) {

                        String email = emailaddress.getText().toString().trim();
                        String pwd = password.getText().toString().trim();

                        ProgressDialog signUpProgressDialog = new ProgressDialog(SignUp.this);
                        signUpProgressDialog.setMessage("Signing Up");
                        signUpProgressDialog.setCancelable(false);
                        signUpProgressDialog.show();

                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    startActivity(new Intent(SignUp.this, SignUpExtended.class));
                                    finish();

                                } else {

                                    Toast.makeText(SignUp.this, "Authentication Failed" + task.getException(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }

                } else {

                    Toast.makeText(SignUp.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private boolean validate() {

        boolean valid = true;

        String email = emailaddress.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.isEmpty() || pwd.length() < 6) {
            password.setError("Enter at least 6 characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (email.isEmpty()) {
            emailaddress.setError("Email Address cannot be empty");
            valid = false;
        } else if (!email.contains(".") || !email.contains("@")) {
            emailaddress.setError("Bad Email Address Format!");
            valid = false;
        } else {
            emailaddress.setError(null);
        }

        return valid;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void loginlink(View view) {

        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        finish();
    }
}
