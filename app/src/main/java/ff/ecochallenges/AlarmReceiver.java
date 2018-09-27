package ff.ecochallenges;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AlarmReceiver extends BroadcastReceiver {


    private static final int DAILY_REMINDER_REQUEST_CODE = 54321;
    String TAG = "AlarmReceiver";
    private DatabaseReference todaysChallenge;
    private String cName;
    private String userUID;
    private SharedPreferences myPreferences;


    @Override

    public void onReceive(final Context context, Intent intent) {

        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userUID = myPreferences.getString("uid", null);
        //Trigger the notification
        showNotification(context,MainActivity.class);
    }


        public void showNotification (final Context context, final Class<?> cls) {
            //Fetch current challenge
            final Calendar sCalendar = Calendar.getInstance();
            String day = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//get the day of week
            //check week day or weekend
            if (day.equals("Saturday") || day.equals("Sunday")) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                mDatabase.orderByKey().equalTo(userUID)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                //Check if user hasn't opened the app today to determine current challenge
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date DATE = new Date();
                                Object lastLogin = dataSnapshot.child("lastLoginDate").getValue();
                                String last = dataSnapshot.child("currentWeekendChallenge").getValue(String.class);
                                int current = Integer.parseInt(last);
                                if (lastLogin.toString().equals(formatter.format(DATE)))
                                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf(current));
                                else
                                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekendChallenges").child(String.valueOf((current % 4) + 1));
                                todaysChallenge.addValueEventListener(new ValueEventListener() {
                                    public String TAG;

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        cName = dataSnapshot.child("cName").getValue(String.class);
                                        // Create an explicit intent for an Activity in your app
                                        Intent intent = new Intent(context, cls);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setAction("challenge");
                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, DAILY_REMINDER_REQUEST_CODE, intent, 0);

                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                                                .setSmallIcon(R.drawable.applogo2)
                                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.applogo2))
                                                .setContentTitle("Today's Challenge")
                                                .setContentText(cName)
                                                .setContentIntent(pendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                                        // notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, mBuilder.build());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w(TAG, "onCancelled", databaseError.toException());
                                    }
                                });

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                mDatabase.orderByKey().equalTo(userUID)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                //Check if user hasn't opened the app today to determine current challenge
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date DATE = new Date();
                                Object lastLogin = dataSnapshot.child("lastLoginDate").getValue();
                                String last = dataSnapshot.child("currentWeekdayChallenge").getValue(String.class);
                                int current = Integer.parseInt(last);
                                if (lastLogin.toString().equals(formatter.format(DATE)))
                                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(String.valueOf(current));
                                else
                                    todaysChallenge = FirebaseDatabase.getInstance().getReference("WeekdayChallenges").child(String.valueOf((current % 26) + 1));
                                todaysChallenge.addValueEventListener(new ValueEventListener() {
                                    public String TAG;

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        cName = dataSnapshot.child("cName").getValue(String.class);
                                        // Create an explicit intent for an Activity in your app
                                        Intent intent = new Intent(context, cls);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setAction("challenge");
                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, DAILY_REMINDER_REQUEST_CODE, intent, 0);

                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                                                .setSmallIcon(R.drawable.applogo2)
                                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.applogo2))
                                                .setContentTitle("Today's Challenge")
                                                .setContentText(cName)
                                                .setContentIntent(pendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                                        // notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, mBuilder.build());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w(TAG, "onCancelled", databaseError.toException());
                                    }
                                });

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        }

    }

