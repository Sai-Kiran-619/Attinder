package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class cards extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    List<String> data;
    SwipeFlingAdapterView flingAdapterView;
    int c=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        flingAdapterView=findViewById(R.id.swipe);

        String cls = getIntent().getExtras().get("clsname").toString();

        data=new ArrayList<>();

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Classes").child(cls);
        DatabaseReference atten = database.getReference().child("Attendance").child(date).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cls);

        myRef.child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.exists()) {
                    c = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String plate = ds.child("RollNo").getValue().toString();
                        data.add(plate);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(cards.this, "cards din't work", Toast.LENGTH_SHORT).show();
            }
        });

        data.add("Start");

        int count = data.size();

        arrayAdapter=new ArrayAdapter<>(cards.this, R.layout.item, R.id.data, data);

        flingAdapterView.setAdapter(arrayAdapter);

        flingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {


            String s ;
            int absent=0, present=0;


            @Override
            public void removeFirstObjectInAdapter() {
                s = data.get(0);
                data.remove(0);

                arrayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onLeftCardExit(Object o) {

                HashMap<String, Object> update = new HashMap<String, Object>();
                update.put("Status", "Absent");
                if(!(s.equals("Start")))
                atten.child(s).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            absent++;
                            HashMap<String,Object> total = new HashMap<String, Object>();
                            total.put("Absent",absent);
                            total.put("Present", present);
                            total.put("Total", c);
                            atten.child("Count").updateChildren(total);
                            Toast.makeText(cards.this,s +"Absent", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(cards.this, "Couldn't update", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onRightCardExit(Object o) {

                HashMap<String, Object> update = new HashMap<String, Object>();
                update.put("Status", "Present");
                if(!(s.equals("Start")))
                atten.child(s).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            present++;
                            HashMap<String,Object> total = new HashMap<String, Object>();
                            total.put("Absent",absent);
                            total.put("Present", present);
                            total.put("Total", c);
                            atten.child("Count").updateChildren(total);
                            Toast.makeText(cards.this,s +"Present", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(cards.this, "Couldn't update", Toast.LENGTH_SHORT).show();
                    }
                });


            }


            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }



        });


        flingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {
                Toast.makeText(cards.this, "data is "+data.get(i)+" "+data.size(),Toast.LENGTH_SHORT).show();
            }
        });

        Button like,dislike;

        like=findViewById(R.id.like);
        dislike=findViewById(R.id.dislike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectRight();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingAdapterView.getTopCardListener().selectLeft();
            }
        });



    }
}