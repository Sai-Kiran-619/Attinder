package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText editText1,editText2;
    private Button signIn;
    private FirebaseAuth mAuth;
    private TextView forgot;
    private DatabaseReference useref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn = (Button) findViewById(R.id.button);
        forgot = (TextView) findViewById(R.id.forgot);
        useref = FirebaseDatabase.getInstance().getReference();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpass();
            }
        });

        editText1 = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editText2 = (EditText) findViewById(R.id.editTextTextPassword);
        Log.d("trace2","not here");

        mAuth = FirebaseAuth.getInstance();
    }

    private void resetpass() {
        Intent i = new Intent(MainActivity.this , ForgotActivity.class);
        startActivity(i);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            sendUser();
        } else {
            userLogin();
        }
    }

    private void sendUser() {
        Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private  void userLogin(){
        String email = editText1.getText().toString().trim();
        String password = editText2.getText().toString().trim();
        if(email.isEmpty()){
            editText1.setError("Email is required!");
            editText1.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText1.setError("please enter a vaild email!");
            editText1.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editText2.setError("Password is required!");
            editText2.requestFocus();
            return;
        }
        if(password.length() < 6){
            editText2.setError("Enter password length gearter than 6");
            editText2.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}