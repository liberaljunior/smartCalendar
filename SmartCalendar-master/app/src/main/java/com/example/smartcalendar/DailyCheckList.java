package com.example.smartcalendar;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

public class DailyCheckList extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView checklist;

    taskDB db;
    Button addButton;
    Button pieButton;
    String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_daily_check_list);
        db = MainActivity.db;

        //segment for navbarstuff
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_dailychecklist);
        //

        checklist = (ListView) findViewById(R.id.checklist);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        checklist.setAdapter(itemsAdapter);
        addButton = (Button) findViewById(R.id.addbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });
        date = getIntent().getExtras().getString("date");
        pieButton = (Button) findViewById(R.id.pieButton);
        pieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyCheckList.this, PiGraphView.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
            }
        });



//        // -----------------------------------
//        // Adjusting for border cases where it is end of month/year/leapyear
//        // -----------------------------------
//
//        String monthNum = date.substring(4,6);
//        String dayNum = date.substring(6);
//        String yearNum = date.substring(0,4);
//
//        boolean leapYear = false;
//        int nextLeapYear = 2016;
//
//        while (Integer.parseInt(yearNum) > nextLeapYear) {
//            nextLeapYear = nextLeapYear + 4;
//        }
//        if(nextLeapYear == Integer.parseInt(yearNum))
//            leapYear = true;
//
//        if(monthNum.equals("01") && dayNum.equals("32")){
//            monthNum = "02";
//            dayNum = "01";
//        }
//        if(monthNum.equals("02") && dayNum.equals("29") && leapYear == false){
//            monthNum = "03";
//            dayNum = "01";
//        }
//        if(monthNum.equals("02") && dayNum.equals("30") && leapYear == true){
//            monthNum = "03";
//            dayNum = "01";
//        }
//        if(monthNum.equals("03") && dayNum.equals("32")){
//            monthNum = "04";
//            dayNum = "01";
//        }
//        if(monthNum.equals("04") && dayNum.equals("31")){
//            monthNum = "05";
//            dayNum = "01";
//        }
//        if(monthNum.equals("05") && dayNum.equals("32")){
//            monthNum = "06";
//            dayNum = "01";
//        }
//        if(monthNum.equals("06") && dayNum.equals("31")){
//            monthNum = "06";
//            dayNum = "01";
//        }
//        if(monthNum.equals("07") && dayNum.equals("32")){
//            monthNum = "08";
//            dayNum = "01";
//        }
//        if(monthNum.equals("08") && dayNum.equals("32")){
//            monthNum = "09";
//            dayNum = "01";
//        }
//        if(monthNum.equals("09") && dayNum.equals("31")){
//            monthNum = "10";
//            dayNum = "01";
//        }
//        if(monthNum.equals("10") && dayNum.equals("32")){
//            monthNum = "11";
//            dayNum = "01";
//        }
//        if(monthNum.equals("11") && dayNum.equals("31")){
//            monthNum = "12";
//            dayNum = "01";
//        }
//        if(monthNum.equals("12") && dayNum.equals("32")){
//            monthNum = "01";
//            dayNum = "01";
//            yearNum = Integer.toString(Integer.parseInt(yearNum) + 1);
//        }
//
//        date = yearNum + monthNum + dayNum;
//
//        Log.d("3333333", "pause");
//        Log.d("$$$$", date);
//        Log.d("4444444", "pause");
//
//
//        //  -----------------------------------
//        //  -----------------------------------
//        //  -----------------------------------

        ArrayList<String> tasksOfDay = db.getTasksOfDay(date);
        for (int i = 0; i < tasksOfDay.size(); i++) {
            itemsAdapter.add(tasksOfDay.get(i));
        }

        setupListViewListener();

        gestureObject = new GestureDetectorCompat(this, new DailyCheckList.LearnGesture());

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        setContentView(R.layout.activity_daily_check_list);
//        db = MainActivity.db;
//
//        //segment for navbarstuff
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setOnNavigationItemSelectedListener(navListener);
//        bottomNav.setSelectedItemId(R.id.nav_dailychecklist);
//        //
//
//        checklist = (ListView) findViewById(R.id.checklist);
//        items = new ArrayList<String>();
//        itemsAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, items);
//        checklist.setAdapter(itemsAdapter);
//        addButton = (Button) findViewById(R.id.addbutton);
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addItem(v);
//            }
//        });
//
//        date = getIntent().getExtras().getString("date");
//
//        ArrayList<String> tasksOfDay = db.getTasksOfDay(date);
//        for (int i = 0; i < tasksOfDay.size(); i++) {
//            itemsAdapter.add(tasksOfDay.get(i));
//        }
//
//        setupListViewListener();
//
//        gestureObject = new GestureDetectorCompat(this, new DailyCheckList.LearnGesture());
//    }

    public void addItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.editText);
        String itemText = etNewItem.getText().toString();
        EditText hoursCompletion = (EditText) findViewById(R.id.editText2);
        String hoursText = hoursCompletion.getText().toString();
        int hoursInt = Integer.parseInt(hoursText);
        if(itemText.equals("") || hoursText.equals("")){
            Toast.makeText(DailyCheckList.this, "Please Enter Text, not just " +
                    "whitespace", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("$$$$$", date + " " + itemText);
            db.insert(date, itemText, hoursInt, "20190101");
            itemsAdapter.add(itemText);
            etNewItem.setText("");
            hoursCompletion.setText("");
            db.close();
        }
    }

    // deleting from task list
    private void setupListViewListener() {
        checklist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position

                        String delete_title = items.get(pos);
                        items.remove(pos);
                        delete_title=delete_title.replace("Task:","");
                        Log.d("??????",delete_title);
                        db.delete(delete_title,date);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e2.getX() > e1.getX()){
                // Left to Right
                Intent intent = new Intent(DailyCheckList.this, MainActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }

            else if (e2.getX() < e1.getX()){
                // Right to Left
                Intent intent = new Intent(DailyCheckList.this, weekView.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
            return true;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent intent = new Intent(DailyCheckList.this,
                                    MainActivity.class);
                            intent.putExtra("date", date);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                            finish();
                            break;
                        case R.id.nav_dailychecklist:
                            //Intent dintent = new Intent(weekView.this, DailyCheckList.class);
                            //dintent.putExtra("date", date);
                            //startActivity(dintent);
                            break;
                        case R.id.nav_weekview:
                            Intent wintent = new Intent(DailyCheckList.this,
                                    weekView.class);
                            wintent.putExtra("date", date);
                            startActivity(wintent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                            break;
                    }

                    return true;
                }
            };

}