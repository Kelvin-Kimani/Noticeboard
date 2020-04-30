package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import jp.wasabeef.blurry.Blurry;

public class Login extends AppCompatActivity {

   private TextInputLayout textEmailLayout, textPwdLayout;
   private EditText email, pwd;
   private FirebaseAuth firebaseAuth;
   private ProgressBar progressBar;
   private Button login;
   private ProgressDialog loginProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!=null){

            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }


        setContentView(R.layout.login);

        textEmailLayout = (TextInputLayout) findViewById(R.id.emailTIL);
        textPwdLayout = (TextInputLayout) findViewById(R.id.pwdTIL);
        email = (EditText) findViewById(R.id.loginemail);
        pwd = (EditText) findViewById(R.id.loginpwd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login = (Button) findViewById(R.id.loginbutton);


        firebaseAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {

                    String emailAddress = email.getText().toString().trim();
                    String password = pwd.getText().toString().trim();

                    /*loginProgressDialog = new ProgressDialog(Login.this);
                    loginProgressDialog.setMessage("Logging in...");
                    loginProgressDialog.setCancelable(false);
                    loginProgressDialog.show();*/


                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(emailAddress,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
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
        } else {pwd.setError(null);}

        if (emailAddress.isEmpty()){
            email.setError("Email Address cannot be empty");
            valid = false;
        }

        else if (!emailAddress.contains(".") ||!emailAddress.contains("@")) {
            email.setError("Bad Email Address Format!");
            valid = false;
        }
        else{email.setError(null);}

        return valid;
    }


    public void signuplink(View view) {

        Intent intent = new Intent(Login.this,SignUp.class);
        startActivity(intent);
    }

    public void resetlink(View view) {

        Intent intent = new Intent(Login.this, ResetPassword.class);
        startActivity(intent);
    }

}
