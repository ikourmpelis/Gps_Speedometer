package com.example.giannis.ergasia1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SeeViolationsActivity extends AppCompatActivity {
    TextView textView1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference SpeedViolations = database.getReference("Speed Violation");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_violations);
        textView1 = (TextView)findViewById(R.id.textView1);
        final ArrayList<String> Violations = new ArrayList<>();
//get violations from db and show to screen
        SpeedViolations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                   Violations.add(postSnapshot.getValue().toString());

                   // System.out.println(postSnapshot.getValue().toString());

                }
                for(int i=0; i < Violations.size(); i++){

                    textView1.setText(textView1.getText() + Violations.get(i) + System.lineSeparator()+ System.lineSeparator());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

                // ...
            }
        });


       //textView1.setText(builder.toString());
    }

}
