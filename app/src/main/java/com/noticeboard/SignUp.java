package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private TextInputLayout textEmailLayout, textPwdLayout;
    EditText emailaddress, password;
    FirebaseAuth firebaseAuth;
    Button signupbtn;
    ProgressBar progressBar;
    FirebaseUser user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textEmailLayout = (TextInputLayout) findViewById(R.id.emailTIL);
        textPwdLayout = (TextInputLayout) findViewById(R.id.pwdTIL);
        emailaddress = (EditText) findViewById(R.id.idemail);
        password = (EditText) findViewById(R.id.pwd);
        signupbtn = (Button) findViewById(R.id.signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        firebaseAuth = FirebaseAuth.getInstance();


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){

                    String email = emailaddress.getText().toString().trim();
                    String pwd = password.getText().toString().trim();

                    ProgressDialog signUpProgressDialog = new ProgressDialog(SignUp.this);
                    signUpProgressDialog.setMessage("Signing Up...");
                    signUpProgressDialog.setCancelable(false);
                    signUpProgressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                startActivity(new Intent(SignUp.this, SignUpExtended.class));
                                finish();

                            } else
                            {

                                Toast.makeText(SignUp.this, "Authentication Failed" + task.getException(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

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
        } else {password.setError(null);}

        if (email.isEmpty()){
            emailaddress.setError("Email Address cannot be empty");
            valid = false;
        }

        else if (!email.contains(".") ||!email.contains("@")) {
            emailaddress.setError("Bad Email Address Format!");
            valid = false;
        }
        else{emailaddress.setError(null);}

        return valid;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void loginlink(View view) {

        startActivity(new Intent(SignUp.this, Login.class));
    }
}
