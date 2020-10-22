package com.noticeboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.noticeboard.Utils.AppUtils;

public class EditUser extends AppCompatActivity {

    Context context = this;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String userID, email, fullname, phonenumber;
    FirebaseUser user;
    Dialog dialog;
    private EditText fullnamesET, phonenumberET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fullnamesET = findViewById(R.id.fullnames);
        phonenumberET = findViewById(R.id.phonenumber);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                fullname = documentSnapshot.getString("fullname");
                phonenumber = documentSnapshot.getString("phonenumber");
                fullnamesET.setText(fullname);
                phonenumberET.setText(phonenumber);

            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            email = user.getEmail();

        }


    }

    private boolean validateForm() {

        boolean valid = true;

        String fname = fullnamesET.getText().toString().trim();
        String pno = phonenumberET.getText().toString().trim();

        if (fname.length() < 2) {
            fullnamesET.setError("Name should be at least 2 characters");
            valid = false;
        } else {
            fullnamesET.setError(null);
        }

        if (pno.length() < 9) {
            phonenumberET.setError("Phonenumber too short!");
            valid = false;
        } else if (pno.length() > 9) {
            phonenumberET.setError("Phonenumber Long!");
            valid = false;
        } else {
            phonenumberET.setError(null);
        }


        return valid;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.accept, menu);
        return true;
    }

    public void updateProfile(MenuItem item) {

        final String updatedFname = fullnamesET.getText().toString().trim();
        final String updatedPno = phonenumberET.getText().toString().trim();

        if (fullname.equals(updatedFname) && phonenumber.equals(updatedPno)) {
            finish();
        } else {

            if (AppUtils.isNetworkConnected(context)) {

                if (validateForm()) {

                    openConfirmationDialog();

                }
            } else {

                Toast.makeText(EditUser.this, "Check your internet connection", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void openConfirmationDialog() {

        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Update Profile");
        text = dialog.findViewById(R.id.dialogtext);
        text.setText("Do you wish to continue?");

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {

                    final String fname = fullnamesET.getText().toString().trim();
                    final String pno = phonenumberET.getText().toString().trim();

                    DocumentReference documentReference = firestore.collection("Users").document(userID);
                    documentReference.update("fullname", fname);
                    documentReference.update("phonenumber", pno).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditUser.this, "Updated Successfully ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(EditUser.this, UserProfile.class));
                                finish();
                            } else {
                                Toast.makeText(EditUser.this, "An Error Occured, Try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

    public void openChangePasswordPopUp(View view) {

        TextView title;
        final EditText oldpwd, newpwd;
        RelativeLayout change;
        final ProgressBar progressBar;

        dialog.setContentView(R.layout.change_password_pop_up);
        oldpwd = dialog.findViewById(R.id.oldpwd);
        newpwd = dialog.findViewById(R.id.newpwd);
        progressBar = dialog.findViewById(R.id.changepasswordPB);
        change = dialog.findViewById(R.id.changepwdRL);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isNetworkConnected(context)) {


                    String oldpassword = oldpwd.getText().toString().trim();
                    final String newpassword = newpwd.getText().toString().trim();

                    if (oldpassword.isEmpty() || oldpassword.length() < 6) {

                        oldpwd.setError("Enter at least 6 characters");

                    } else if (newpassword.isEmpty() || newpassword.length() < 6) {

                        newpwd.setError("New Password should be at least 6 characters");

                    } else {

                        progressBar.setVisibility(View.VISIBLE);

                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpassword);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                progressBar.setVisibility(View.INVISIBLE);

                                if (task.isSuccessful()) {
                                    user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditUser.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                } else {

                                    Toast.makeText(EditUser.this, "Please check your old password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {

                    Toast.makeText(EditUser.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void openChangeEmailPopUp(View view) {

        TextView title;
        final EditText email, pwd, newEmail;
        RelativeLayout change;
        final ProgressBar progressBar;

        dialog.setContentView(R.layout.change_email_popup);
        email = dialog.findViewById(R.id.email);
        pwd = dialog.findViewById(R.id.pwd);
        newEmail = dialog.findViewById(R.id.newEmail);
        change = dialog.findViewById(R.id.changeemail);
        progressBar = dialog.findViewById(R.id.changeemailPB);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isNetworkConnected(context)) {

                    final String emailAddress = email.getText().toString().trim();
                    String password = pwd.getText().toString().trim();
                    final String newEmailAddress = newEmail.getText().toString().trim();

                    if (emailAddress.isEmpty() || !emailAddress.contains(".") || !emailAddress.contains("@") || emailAddress.startsWith(".") || emailAddress.startsWith("@")) {
                        email.setError("Check your email address");
                    } else if (password.isEmpty() || password.length() < 6) {
                        pwd.setError("Enter at least 6 characters");
                    } else if (newEmailAddress.isEmpty() || !newEmailAddress.contains(".") || !newEmailAddress.contains("@") || newEmailAddress.startsWith(".") || newEmailAddress.startsWith("@")) {
                        newEmail.setError("Check your email address");
                    } else {

                        progressBar.setVisibility(View.VISIBLE);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(emailAddress, password);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(newEmailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        progressBar.setVisibility(View.INVISIBLE);

                                        if (task.isSuccessful()) {

                                            DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userID);
                                            reference.update("email", newEmailAddress);
                                            Toast.makeText(EditUser.this, "Email Updated Successfully", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                            }
                        });
                    }

                } else {

                    Toast.makeText(EditUser.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}
