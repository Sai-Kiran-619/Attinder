package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class addclass extends AppCompatActivity {

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    DatabaseReference ref;

    FirebaseAuth mAuth;

    TextView tot, ent;
    String css;
    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addclass);

        Button btn3 = findViewById(R.id.btn);
        tot = (TextView) findViewById(R.id.total);


        mAuth = FirebaseAuth.getInstance();
        String CurrentUID = mAuth.getCurrentUser().getUid();

        ref = db.getReference();

        ref.child("Teachers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                tot.setText("Total : "+ snapshot.child("Strength").getValue().toString());
                css = snapshot.child("Branch").getValue().toString() + "_" + snapshot.child("Incharge of").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSetter();
            }
        });


    }

    public  void dataSetter(){
        EditText sname,sroll;
        sname = findViewById(R.id.sname);
        sroll = findViewById(R.id.sroll);

        String name , roll ;


        name = sname.getText().toString();
        roll = sroll.getText().toString();








        if(!(name.isEmpty()) && !(roll.isEmpty())) {
            HashMap<String, Object> details = new HashMap<String, Object>();
            details.put("Name",name);
            details.put("RollNo", roll);
            ref.child("Classes").child(css).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("Strength") ) {
                        if (String.valueOf((int) snapshot.child("Students").getChildrenCount()).equals(snapshot.child("Strength").getValue().toString())) {
                            Intent i = new Intent(addclass.this, DashboardActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            ref.child("Teachers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild("Branch") && snapshot.hasChild("Incharge of")) {
                                        String cdet = snapshot.child("Branch").getValue().toString() + "_" + snapshot.child("Incharge of").getValue().toString();
                                        ref.child("Classes").child(cdet).child("Students").child(roll).updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                Toast.makeText(addclass.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(addclass.this, "Error" + e, Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });




            sname.setText(" ");
            sroll.setText(" ");
        } else {
            Toast.makeText(this, "Enter student details", Toast.LENGTH_SHORT).show();
        }

    }





}