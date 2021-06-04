package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DashboardActivity extends AppCompatActivity {

    ImageButton edit1, classadd, log;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private FloatingActionButton addstudent;
    private TextView user, brch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();
        addstudent = (FloatingActionButton) findViewById(R.id.addstudent);
        classadd = (ImageButton) findViewById(R.id.addclass);
        log = (ImageButton) findViewById(R.id.logout);

        user = (TextView) findViewById(R.id.name);
        brch = (TextView) findViewById(R.id.branch);

        addstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, addclass.class);
                startActivity(i);
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i  = new Intent(DashboardActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });

        classadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, ClasstActivity.class);
                startActivity(i);
            }
        });

        edit1 = (ImageButton) findViewById(R.id.edit);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null) {
            Intent i = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(i);
            
        } else {
            
            updateName();
            
        }
    }

    private void updateName() {
        userref.child("Teachers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Name")) {
                    user.setText(snapshot.child("Name").getValue().toString());
                    brch.setText(snapshot.child("Branch").getValue().toString());
                    Toast.makeText(DashboardActivity.this, "Welcome " + snapshot.child("Name").getValue().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void updateProfile() {
        Intent i = new Intent(DashboardActivity.this, ProfileActivity.class);
        startActivity(i);
    }
}