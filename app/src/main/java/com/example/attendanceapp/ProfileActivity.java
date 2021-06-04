package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, design, inch, strn;
    private String[] branch = {"Select", "CSE","ECE","IT","MECH","AERO","EEE"};
    private  String selected = "";
    private  Spinner hb;
    private GridLayout lay;
    private Button upld;
    private CheckBox ta,tb,tc,td,tha,thb,thc,thd,fa,fb,fc,fd;

    private FirebaseAuth mAuth;
    private DatabaseReference useref;
    private ArrayList<String> sections ;
    int a;
    private HashMap<String, Object> up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        useref = FirebaseDatabase.getInstance().getReference();

        sections = new ArrayList<String>();
        up = new HashMap<String, Object>();


        initialize();

        hb = (Spinner) findViewById(R.id.branch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,branch);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hb.setAdapter(adapter);
        hb.setOnItemSelectedListener(this);

        upld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });

    }

    private void uploadDetails() {
        HashMap<String, Object> details = new HashMap<String, Object>();
        details.put("Name", name.getText().toString());
        details.put("Designation",design.getText().toString());
        details.put("Branch",branch[a]);
        details.put("Incharge of",inch.getText().toString());
        details.put("Strength", strn.getText().toString());

        useref.child("Teachers").child(mAuth.getCurrentUser().getUid()).updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()) {
                    HashMap<String , Object> details = new HashMap<String, Object>();
                    details.put("Class",inch.getText().toString());
                    details.put("Strength", strn.getText().toString());

                    useref.child("Classes").child(branch[a] + "_" + inch.getText().toString()).updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ProfileActivity.this, DashboardActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Error : " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                    }
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {

        name = (EditText) findViewById(R.id.name1);
        design = (EditText) findViewById(R.id.design);
        inch = (EditText) findViewById(R.id.incharge);
        strn = (EditText) findViewById(R.id.strength);
        upld = (Button) findViewById(R.id.upload);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected = branch[position];
        a = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}