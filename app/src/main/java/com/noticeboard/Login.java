package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.noticeboard.Utils.AppUtils;

public class Login extends AppCompatActivity {

    Context context = this;
    private TextInputLayout textEmailLayout, textPwdLayout;
    private EditText email, pwd;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }


        setContentView(R.layout.login);

        textEmailLayout = findViewById(R.id.emailTIL);
        textPwdLayout = findViewById(R.id.pwdTIL);
        email = findViewById(R.id.loginemail);
        pwd = findViewById(R.id.loginpwd);
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.loginbutton);


        firebaseAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isNetworkConnected(context)) {

                    if (validateForm()) {

                        String emailAddress = email.getText().toString().trim();
                        String password = pwd.getText().toString().trim();

                        progressBar.setVisibility(View.VISIBLE);

                        firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                            }
                        });

                    }

                } else {

                    Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateForm() {

        boolean valid = true;

        String emailAddress = email.getText().toString().trim();
        String password = pwd.getText().toString().trim();

        if (password.isEmpty() || password.length() < 6) {
            pwd.setError("Enter at least 6 characters");
            valid = false;
        } else {
            pwd.setError(null);
        }

        if (emailAddress.isEmpty()) {
            email.setError("Email Address cannot be empty");
            valid = false;
        } else if (!emailAddress.contains(".") || !emailAddress.contains("@")) {
            email.setError("Bad Email Address Format!");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }


    public void signuplink(View view) {

        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }

    public void resetlink(View view) {

        Intent intent = new Intent(Login.this, ResetPassword.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
