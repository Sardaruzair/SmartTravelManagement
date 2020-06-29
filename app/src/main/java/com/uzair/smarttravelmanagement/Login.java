package com.uzair.smarttravelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uzair.smarttravelmanagement.Models.User;

public class Login extends AppCompatActivity {

    private RelativeLayout loginLayout, registerLayout, forgetPasswordLayout;

    //For Login Form
    private TextView login_failed;
    private EditText email, password;
    private String mEmail, mPassword;

    //For Register Form
    private EditText regFirstName, regLastName, regMobileNumber, regEmail, regPassword, regConfirmPassword;
    private String mRegFirstName, mRegLastName, mRegMobileNumber, mRegEmail, mRegPassword, mRegConfirmPassword;

    //For Reset Password Form
    private EditText forgetPasswordEmail;
    private String mForgetPasswordEmail;

    private String TAG = "Login";

    private ProgressDialog progressDialog;

    //Firebase objects
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Login.this, drawer_main.class));
            finish();
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        initViews();

    }

    private void initViews() {
        loginLayout = findViewById(R.id.login_layout);
        registerLayout = findViewById(R.id.register_layout);
        forgetPasswordLayout = findViewById(R.id.forget_password_layout);

        //Login Form Fields
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_failed = findViewById(R.id.login_failed);

        //Register Form Fields
        regFirstName = findViewById(R.id.register_first_name);
        regLastName = findViewById(R.id.register_last_name);
        regMobileNumber = findViewById(R.id.register_mobile_number);
        regEmail = findViewById(R.id.register_email_address);
        regPassword = findViewById(R.id.register_password);
        regConfirmPassword = findViewById(R.id.register_confirm_password);

        //Forget Password Fields
        forgetPasswordEmail = findViewById(R.id.fp_email);

        //Progress Dialog
        progressDialog = new ProgressDialog(this);

        //Database Reference
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    //Login Button
    public void Login(View view) {
        progressDialog.setMessage("Logging in. Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mEmail = email.getText().toString();
        mPassword = password.getText().toString();
        if (mEmail.isEmpty()) {
            email.setError("Enter Your Email");
            email.requestFocus();
            return;
        }
        if (mPassword.isEmpty()) {
            password.setError("Enter Your Password");
            password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startActivity(new Intent(Login.this, drawer_main.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            login_failed.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();

                        }
                    }
                });

    }

    //Register Button
    public void CreateAccount(View view) {
        progressDialog.setMessage("Creating User. Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mRegFirstName = regFirstName.getText().toString();
        mRegLastName = regLastName.getText().toString();
        mRegMobileNumber = regMobileNumber.getText().toString();
        mRegEmail = regEmail.getText().toString();
        mRegPassword = regPassword.getText().toString();
        mRegConfirmPassword = regConfirmPassword.getText().toString();
        if (mRegFirstName.isEmpty()) {
            regFirstName.setError("Enter First Name");
            regFirstName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mRegLastName.isEmpty()) {
            regLastName.setError("Enter Last Name");
            regLastName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mRegMobileNumber.isEmpty()) {
            regMobileNumber.setError("Enter Mobile Number");
            regMobileNumber.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mRegEmail.isEmpty()) {
            regEmail.setError("Enter Your Email");
            regEmail.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mRegPassword.isEmpty()) {
            regPassword.setError("Enter Password");
            regPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (mRegConfirmPassword.isEmpty()) {
            regConfirmPassword.setError("Enter Confirm Password");
            regConfirmPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (!mRegPassword.equals(mRegConfirmPassword)) {
            regPassword.setError("Passwords should be matched");
            regPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mRegEmail, mRegPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userId = mAuth.getCurrentUser().getUid();

                            userRef.child(userId)
                                    .setValue(new User(mRegFirstName, mRegLastName, mRegMobileNumber, mRegEmail, "user"))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(Login.this, "User Added Successfully!!!", Toast.LENGTH_SHORT).show();
                                            mAuth.signInWithEmailAndPassword(mRegEmail, mRegPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(Login.this, drawer_main.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(Login.this, "Error creating user. please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //Send Email for reset password button
    public void ResetPassword(View view) {
        mForgetPasswordEmail = forgetPasswordEmail.getText().toString();
        if (mForgetPasswordEmail.isEmpty()) {
            forgetPasswordEmail.setError("Enter Your Email");
            forgetPasswordEmail.requestFocus();
            progressDialog.dismiss();
            return;
        }
    }

    //Forget Password Text
    public void ForgetPassword(View view) {
        loginLayout.setVisibility(View.GONE);
        forgetPasswordLayout.setVisibility(View.VISIBLE);
    }

    //Create Account Text
    public void Register(View view) {
        loginLayout.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
    }

    //Account Already Exist Text
    public void AccountExistBtn(View view) {
        registerLayout.setVisibility(View.GONE);
        forgetPasswordLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }
}
