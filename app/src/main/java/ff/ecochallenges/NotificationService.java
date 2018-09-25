package ff.ecochallenges;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import static java.util.Calendar.DATE;

public class NotificationService extends Service {

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    private DatabaseReference todaysChallenge;
    private String cName;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //Test timer
//        Calendar tomorrowtest = Calendar.getInstance();
//        tomorrowtest.set(Calendar.HOUR_OF_DAY, 14);
//        tomorrowtest.set(Calendar.MINUTE, 48);
//        tomorrowtest.set(Calendar.SECOND, 0);
//        if(tomorrowtest.getTime().compareTo(new Date()) < 0)
//            tomorrowtest.add(Calendar.DAY_OF_MONTH, 1);
//        timer.schedule(timerTask, tomorrowtest.getTime(), 1000*60*60*24);

        //schedule the timer
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 17);
        tomorrow.set(Calendar.MINUTE, 2);
        tomorrow.set(Calendar.SECOND, 0);
        if(today.compareTo(tomorrow) > 0)
            tomorrow.add(DATE, 1);
        long initialDelay = tomorrow.getTimeInMillis() - today.getTimeInMillis();
        timer.scheduleAtFixedRate(timerTask, initialDelay, 1000*60*60*24);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Send simple notification using the NotificationCompat API.
     */
    public void sendNotification() {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String userUID = myPreferences.getString("uid", null);
        //Fetch current challenge
        final Calendar sCalendar = Calendar.getInstance();
        String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
        //check week day or weekend
        if (day.equals("Saturday") || day.equals("Sunday")) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase.orderByKey().equalTo(userUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String last = dataSnapshot.child(userUID).child("currentWeekendChallenge").getValue(String.class);
                        int current = Integer.parseInt(last);
                        todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf(current));
                        todaysChallenge.addValueEventListener(new ValueEventListener() {
                            public String TAG;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                cName = dataSnapshot.child("cName").getValue(String.class);
                                // Create an explicit intent for an Activity in your app
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                                        .setSmallIcon(R.drawable.applogo2)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.applogo2))
                                        .setContentTitle("Today's Challenge")
                                        .setContentText(cName)
                                        .setContentIntent(pendingIntent)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(12345, mBuilder.build());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase.orderByKey().equalTo(userUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String last = dataSnapshot.child(userUID).child("currentWeekdayChallenge").getValue(String.class);
                        int current = Integer.parseInt(last);
                        todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(String.valueOf(current));
                        todaysChallenge.addValueEventListener(new ValueEventListener() {
                            public String TAG;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                cName = dataSnapshot.child("cName").getValue(String.class);
                                // Create an explicit intent for an Activity in your app
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                                        .setSmallIcon(R.drawable.applogo2)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.applogo2))
                                        .setContentTitle("Today's Challenge")
                                        .setContentText(cName)
                                        .setContentIntent(pendingIntent)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(12345, mBuilder.build());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        sendNotification();

                    }
                });
            }
        };
    }
}
