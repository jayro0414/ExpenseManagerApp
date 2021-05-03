package com.example.expensemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassowrd;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;

    private ProgressDialog mdiolog;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mdiolog=new ProgressDialog(this);
        login();
    }

    private void login() {

        mEmail=findViewById(R.id.email_login);
        mPassowrd=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.btn_login);
        mForgetPassword=findViewById(R.id.forgot_password);
        mSignupHere=findViewById(R.id.sign_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email=mEmail.getText().toString().trim();
               String password= mPassowrd.getText().toString().trim();

               if (TextUtils.isEmpty(email)) {
                   mEmail.setError("Invalid Input");
                   return;
               }
               if (TextUtils.isEmpty(password)) {
                   mForgetPassword.setError("Invalid Input");
                   return;
               }

               mdiolog.setMessage("Processing...");
               mdiolog.show();

               mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           mdiolog.dismiss();
                           startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                           Toast.makeText(getApplicationContext(), "Log In Successful!", Toast.LENGTH_SHORT).show();
                       } else {
                           mdiolog.dismiss();
                           Toast.makeText(getApplicationContext(), "Log In Failed.", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           }
        });

        // Registration Activity

        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        //Reset password activity

        mForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }
}