package com.example.smartcalendar;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartcalendar.MainActivity.db;

public class weekView extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView weeklist;

    taskDB db;
    Button addButton;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        db = MainActivity.db;

        //segment for navbarstuff
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_weekview);
        //


        weeklist = (ListView) findViewById(R.id.weeklist);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        weeklist.setAdapter(itemsAdapter);

        date = getIntent().getExtras().getString("date");
        Log.d("1111111", "pause");
        Log.d("$$$$", date);
        Log.d("22222222", "pause");

        for(int j = 0; j < 7; j++) {
            Integer parsedDate = Integer.parseInt(date);
            parsedDate++;
            date = String.valueOf(parsedDate);

            // -----------------------------------
            // Adjusting for border cases where it is end of month/year/leapyear
            // -----------------------------------

            String monthNum = date.substring(4,6);
            String dayNum = date.substring(6);
            String yearNum = date.substring(0,4);

            boolean leapYear = false;
            int nextLeapYear = 2016;

            while (Integer.parseInt(yearNum) > nextLeapYear) {
                nextLeapYear = nextLeapYear + 4;
            }
            if(nextLeapYear == Integer.parseInt(yearNum))
                leapYear = true;

            if(monthNum.equals("01") && dayNum.equals("32")){
                monthNum = "02";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("29") && leapYear == false){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("30") && leapYear == true){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("03") && dayNum.equals("32")){
                monthNum = "04";
                dayNum = "01";
            }
            if(monthNum.equals("04") && dayNum.equals("31")){
                monthNum = "05";
                dayNum = "01";
            }
            if(monthNum.equals("05") && dayNum.equals("32")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("06") && dayNum.equals("31")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("07") && dayNum.equals("32")){
                monthNum = "08";
                dayNum = "01";
            }
            if(monthNum.equals("08") && dayNum.equals("32")){
                monthNum = "09";
                dayNum = "01";
            }
            if(monthNum.equals("09") && dayNum.equals("31")){
                monthNum = "10";
                dayNum = "01";
            }
            if(monthNum.equals("10") && dayNum.equals("32")){
                monthNum = "11";
                dayNum = "01";
            }
            if(monthNum.equals("11") && dayNum.equals("31")){
                monthNum = "12";
                dayNum = "01";
            }
            if(monthNum.equals("12") && dayNum.equals("32")){
                monthNum = "01";
                dayNum = "01";
                yearNum = Integer.toString(Integer.parseInt(yearNum) + 1);
            }

            date = yearNum + monthNum + dayNum;

            Log.d("3333333", "pause");
            Log.d("$$$$", date);
            Log.d("4444444", "pause");


            //  -----------------------------------
            //  -----------------------------------
            //  -----------------------------------

            ArrayList<String> tasksOfWeek = db.getTasksOfWeek(date);

            for (int i = 0; i < tasksOfWeek.size(); i++) {
                itemsAdapter.add(tasksOfWeek.get(i));
            }
        }

        date = getIntent().getExtras().getString("date");

        gestureObject = new GestureDetectorCompat(this, new weekView.LearnGesture());

        //populating the graph
        AnyChartView anyChartView = findViewById(R.id.columnView);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();

        //yoink some tasks bruv
        ArrayList<String> titlesOfDay = db.getTitlesOfDay(date);
        ArrayList<Integer> HoursOfDay = db.getHoursOfDay(date);
        //populating dataset for the chart
        for(int i=0;i<HoursOfDay.size();i++){
            Log.d("titlefromlist",titlesOfDay.get(i));
            Log.d("hoursfromlist",Integer.toString(HoursOfDay.get(i)));
            data.add(new ValueDataEntry(titlesOfDay.get(i),HoursOfDay.get(i)));
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: } Tyler");

        cartesian.animation(true);
        cartesian.title("Time to Completion Per Task");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: } Hours");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Task");
        cartesian.yAxis(0).title("Hours Remaining");

        anyChartView.setChart(cartesian);




    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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
                Intent intent = new Intent(weekView.this, DailyCheckList.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }

            else if (e2.getX() < e1.getX()){
                // Right to Left
                Intent intent = new Intent(weekView.this, MainActivity.class);
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
                            Intent intent = new Intent(weekView.this,
                                    MainActivity.class);
                            intent.putExtra("date", date);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                            break;
                        case R.id.nav_dailychecklist:
                            Intent dintent = new Intent(weekView.this,
                                    DailyCheckList.class);
                            dintent.putExtra("date", date);
                            startActivity(dintent);
                            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                            finish();
                            break;
                        case R.id.nav_weekview:
                            //Intent wintent = new Intent(MainActivity.this, weekView.class);
                            //wintent.putExtra("date", date);
                            //startActivity(wintent);
                            break;
                    }

                    return true;
                }
            };



}
