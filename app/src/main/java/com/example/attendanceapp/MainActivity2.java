package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity2 extends AppCompatActivity
{
    PieChartView pieChartView;
    TextView tv1,tv2,per;
    PieChartView pie;
    Button b1, b2;
    String m_Text = "";
    CSVWriter writer = null;

    FirebaseAuth mauth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mauth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        tv1 = (TextView) findViewById(R.id.absentee);
        tv2 = (TextView) findViewById(R.id.presentee);
        per = (TextView) findViewById(R.id.percentage);
        pie = (PieChartView) findViewById(R.id.pie);
        b1 = (Button) findViewById(R.id.download);
        b2 = (Button) findViewById(R.id.dash);

        tv1.setText("Absentee Count : "+getIntent().getExtras().get("Absent"));
        tv2.setText("Presentee Count : "+getIntent().getExtras().get("Present"));

        int ab = Integer.parseInt((String) getIntent().getExtras().get("Absent"));
        int pr = Integer.parseInt((String) getIntent().getExtras().get("Present"));
        int tt = Integer.parseInt((String) getIntent().getExtras().get("Total"));

        String classname = getIntent().getExtras().get("Class").toString();

        String pp = String.format("%.2f",(float) pr/tt*100);

        per.setText(pp);

        pieChartView = findViewById(R.id.pie);

        List pieData = new ArrayList<>();
        pieData.add(new SliceValue(100 - (float) pr/tt*100, Color.RED).setLabel("Absentees : " + ab));
        pieData.add(new SliceValue((float) pr/tt*100, Color.GREEN).setLabel("Presentees : " + pr));


        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(12);
        pieChartData.setHasCenterCircle(true).setCenterText1("Attendance").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity2.this,DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });





        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setTitle("Enter Date :");

                final EditText date = new EditText(MainActivity2.this);
                date.setHint("dd-mm-yyyy");
                builder.setView(date);




                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        m_Text = date.getText().toString();
                        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/" + classname+"_"+m_Text+".csv");


                        ref.child("Attendance").child(m_Text).child(mauth.getCurrentUser().getUid()).child(classname).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                try{
                                    writer = new CSVWriter(new FileWriter(csv));

                                    List<String[]> data = new ArrayList<String[]>();
                                    data.add(new String[] {"RollNo","Status"});
                                    if(snapshot.hasChildren()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            if(!(ds.getKey().equals("Count"))) {
                                                String[] addup = {ds.getKey() , String.valueOf(snapshot.child(ds.getKey()).child("Status").getValue())};
                                                data.add(addup);

                                            }
                                        }

                                    }

                                    writer.writeAll(data);
                                    writer.close();
                                    Toast.makeText(MainActivity2.this, "File Downloaded to /Download/ as " + classname+"_"+m_Text+".csv", Toast.LENGTH_LONG).show();

                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();

                    }
                });

                builder.show();

            }
        });




    }
}