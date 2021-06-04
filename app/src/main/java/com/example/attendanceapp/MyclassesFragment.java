package com.example.attendanceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MyclassesFragment extends Fragment {

    private  View list;
    private RecyclerView cycle;
    private DatabaseReference userref, userref1;
    private FirebaseAuth mAuth;
    private String currentUserID="";
    int absent = 0,present = 0,c = 0;


    public MyclassesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list = inflater.inflate(R.layout.fragment_myclasses, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        userref = FirebaseDatabase.getInstance().getReference().child("Teachers").child(mAuth.getCurrentUser().getUid()).child("Classes");
        userref1 = FirebaseDatabase.getInstance().getReference().child("Attendance").child(date).child(mAuth.getCurrentUser().getUid());
        cycle = (RecyclerView)list.findViewById(R.id.mylist);
        cycle.setLayoutManager(new LinearLayoutManager(getContext()));

        return list;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<classInfo> options = new FirebaseRecyclerOptions.Builder<classInfo>().setQuery(userref,classInfo.class).build();

        FirebaseRecyclerAdapter<classInfo, MyclassesFragment.ListHolder> adapter =
                new FirebaseRecyclerAdapter<classInfo, MyclassesFragment.ListHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull MyclassesFragment.ListHolder listHolder, int i, @NonNull classInfo classInfo) {

                        final  String incharges = getRef(i).getKey();
                        userref.child(incharges).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    if (snapshot.hasChildren()) {
                                        final String classname = snapshot.child("Class").getValue().toString();
                                        final String strength = snapshot.child("Strength").getValue().toString();


                                        listHolder.classn.setText(classname);
                                        listHolder.strn.setText(strength);

                                        userref1.child(classname).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if(snapshot.exists()) {
                                                    listHolder.stats.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(getContext(), MainActivity2.class);
                                                            i.putExtra("Class", classname);
                                                            i.putExtra("Absent", snapshot.child("Count").child("Absent").getValue().toString());
                                                            i.putExtra("Present",  snapshot.child("Count").child("Present").getValue().toString());
                                                            i.putExtra("Total", snapshot.child("Count").child("Total").getValue().toString());
                                                            startActivity(i);

                                                        }
                                                    });
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });



                                        listHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(getContext(), cards.class);
                                                i.putExtra("clsname", classname);
                                                startActivity(i);
                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public MyclassesFragment.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_view,parent,false);
                        return new MyclassesFragment.ListHolder(view);
                    }


                };

        cycle.setAdapter(adapter);
        adapter.startListening();


    }


    public static class ListHolder extends RecyclerView.ViewHolder {

        TextView classn, strn;
        ImageButton stats;

        public ListHolder(@NonNull View itemView) {
            super(itemView);

            stats = itemView.findViewById(R.id.stats);
            classn = itemView.findViewById(R.id.nclass);
            strn = itemView.findViewById(R.id.strenth);

        }
    }
}