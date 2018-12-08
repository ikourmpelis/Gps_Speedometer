package com.example.giannis.ergasia1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
      EditText editText ;
    EditText editText2 ;
    EditText editText3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editText =(EditText) findViewById(R.id.editText);
        editText2 =(EditText) findViewById(R.id.editText2);
        editText3 =(EditText) findViewById(R.id.editText3);
    }
    public  void setSpeed (View view){
// set speed limits via shared preferences
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("limit1", Integer.parseInt(editText.getText().toString()));
        editor.putInt("limit2", Integer.parseInt(editText2.getText().toString()));
        editor.putInt("limit3", Integer.parseInt(editText3.getText().toString()));
        editor.commit();
        Toast.makeText(this, "Speed Limits Setted!",
                Toast.LENGTH_LONG).show();
    }

}
