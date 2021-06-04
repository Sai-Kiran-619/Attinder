package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ClasstActivity extends AppCompatActivity {
    EditText count, name;
    Button enter, submit;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classt);

        userref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        name = (EditText) findViewById(R.id.classname);

        enter = (Button) findViewById(R.id.enter);




        HashMap<String, Object> classes = new HashMap<String, Object>();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    userref.child("Classes").child(name.getText().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            classes.put("Class", name.getText().toString());
                            classes.put("Strength" ,snapshot.child("Strength").getValue().toString());
                            userref.child("Teachers").child(mAuth.getCurrentUser().getUid()).child("Classes").child(name.getText().toString()).updateChildren(classes).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    Toast.makeText(ClasstActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(ClasstActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }

        });



    }
}