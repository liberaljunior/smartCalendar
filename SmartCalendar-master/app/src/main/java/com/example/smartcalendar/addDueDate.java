package com.example.smartcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addDueDate extends AppCompatActivity {
    taskDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_due_date);

        db = MainActivity.db;
        final String date = getIntent().getExtras().getString("date");

        Button addbut = (Button) findViewById(R.id.addbutton);
        addbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleText = (EditText) findViewById(R.id.editText3);
                String title = titleText.getText().toString();
                EditText dueDateText = (EditText) findViewById(R.id.editText4);
                String dueDate = dueDateText.getText().toString();
                EditText hoursText = (EditText) findViewById(R.id.editText5);
                String hours_string = hoursText.getText().toString();

                if(title.equals("") || dueDate.equals("") || hours_string.equals("")){
                    Toast.makeText(addDueDate.this, "Please fulfill all" +
                            " criteria correctly", Toast.LENGTH_SHORT).show();
                }
                else {
                    int hours = Integer.parseInt(hours_string);
                    dueDate = dueDate.replaceAll("\\s+", "");
                    dueDate = dueDate.replaceAll("-", "");
                    dueDate = dueDate.replaceAll("/", "");
                    if(dueDate.length() != 8){
                        Toast.makeText(addDueDate.this, "Please correctly" +
                                " format date: make sure to add a leading 0 to month or day if " +
                                " necessary", Toast.LENGTH_LONG).show();
                    }
                    else {
                        String duerDater = dueDate.substring(4) + dueDate.substring(0, 2) +
                                dueDate.substring(2, 4);
                        db.insert(duerDater, title, hours, date);
                        Intent intent = new Intent(addDueDate.this,
                                MainActivity.class);
                        intent.putExtra("date", date);
                        startActivity(intent);
                    }
                }
            }
        });

        Button cancbutton = (Button) findViewById(R.id.CancelButton);
        cancbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addDueDate.this,
                        MainActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
