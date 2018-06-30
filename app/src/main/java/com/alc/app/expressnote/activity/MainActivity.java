package com.alc.app.expressnote.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alc.app.expressnote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginBtn;
    private Button mSignup;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mSignup = (Button) findViewById(R.id.signupBtn);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null ){
                    startActivity(new Intent(MainActivity.this, DisplayActivity.class));
                }
            }
        };

        if (isNetworkConnected() || isWifiConnected()) {

            Toast.makeText(MainActivity.this, "You are Connected to the internet", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setMessage("It looks like your internet connection is off. Please turn it " +
                            "on and try again")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(R.drawable.warning).show();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startSignIn();
            }
        });
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignup();
            }
        });

    }

    private void startSignup() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        progressDialog.setMessage("Please wait Signing up User.....");
        progressDialog.setTitle("Register");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            progressDialog.hide();
            Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.hide();
                        Toast.makeText(MainActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();

                    }
                    if (!task.isSuccessful()){
                        progressDialog.hide();
                        Toast.makeText(MainActivity.this, "There was a problem Signing up", Toast.LENGTH_LONG).show();


                    }
                }
            });

        }

    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }


    @Override
    protected void onStart(){ //Listens for changes to Auth
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        progressDialog.setMessage("Please wait Signing in User.....");
        progressDialog.setTitle("Login");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            progressDialog.hide();
            Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        progressDialog.hide();
                        Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }

    }
}
