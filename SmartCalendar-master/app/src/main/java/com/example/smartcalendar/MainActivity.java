//calendar view

package com.example.smartcalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;

    public static taskDB db;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notification channel
        createNotificationChannel();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        date = df.format(c);
        Log.d("%%%%" , date);

        //segment for navbarstuff
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_home);
        //

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                Toast toast = Toast.makeText(getApplicationContext(), ""+month+"-"+dayOfMonth+
                        "-"+year, Toast.LENGTH_LONG);
                if(month < 10 && dayOfMonth < 10){
                    date = "" + year + "0" + month + "0" + dayOfMonth;
                }
                else if (month < 10){
                    date = "" + year + "0" + month + dayOfMonth;
                }
                else if (dayOfMonth < 10){
                    date = "" + year + month + "0" + dayOfMonth;
                }
                else{
                    date = "" + year + month + dayOfMonth;
                }
                Intent intent = new Intent(MainActivity.this, DailyCheckList.class);
                intent.putExtra("date", date);
                startActivity(intent);
                toast.show();
            }
        });

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        db = new taskDB(this, db.DB_NAME, null, 1);

        //checking for things due today and add notifcation
        ArrayList<String> tasksOfDay = db.getTasksOfDay(date);
        if(tasksOfDay != null) {
            addNotification(tasksOfDay);
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addDueDate.class);
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
                Intent intent = new Intent(MainActivity.this, weekView.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();

            }

            else if (e2.getX() < e1.getX()){
                // Right to Left
                Intent intent = new Intent(MainActivity.this, DailyCheckList.class);
                intent.putExtra("date", date);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
            return true;
        }
    }

    //navigation heckery--------------------
    //used https://codinginflow.com/tutorials/android/bottomnavigationview as a reference
    //    for navigation views
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            //Intent intent = new Intent(weekView.this, MainActivity.class);
                            //intent.putExtra("date", date);
                            //startActivity(intent);
                            break;
                        case R.id.nav_dailychecklist:
                            Intent intent = new Intent(MainActivity.this,
                                    DailyCheckList.class);
                            intent.putExtra("date", date);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                            break;
                        case R.id.nav_weekview:
                            Intent wintent = new Intent(MainActivity.this,
                                    weekView.class);
                            wintent.putExtra("date", date);
                            startActivity(wintent);
                            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                            finish();
                            break;
                    }

                    return true;
                }
            };


    //---------------------------------

    private void addNotification(ArrayList<String> tasksOfDay){
        //build notification
        StringBuilder content = new StringBuilder("");
        String title = "Smart Calendar";
        if(tasksOfDay.size() == 1){
            content.append("You have one task to do today: " + "\n" + tasksOfDay.get(0));
        }
        else{
            content.append("You have tasks to do today: " +"\n");
            for (int i = 0; i < tasksOfDay.size(); i++) {
                content.append(tasksOfDay.get(i) + " " + "\n");
            }
        }
        String CHANNEL_ID = "my_channel_01";

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logoooo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Channel";
            String description = "A new channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_01", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
