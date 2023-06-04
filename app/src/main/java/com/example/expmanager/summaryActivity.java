package com.example.expmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.expmanager.databinding.ActivitySummaryBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class summaryActivity extends AppCompatActivity {

    ActivitySummaryBinding binding;
    TextView t1,t2,t3,t4;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    Map<String, Long> map = new HashMap<>() ;

    RecyclerView mRecyclerView;

    private ExpenseAdapter expenseAdapter;
    String i,e,b,month;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getData();

        month = getIntent().getStringExtra("month");

        t4 = findViewById(R.id.month_tv);
        t4.setText(month+" Summary");


        MaterialSwitch switc = findViewById(R.id.reminder_toggle);
        SharedPreferences pref = getSharedPreferences("save",MODE_PRIVATE);
        switc.setChecked(pref.getBoolean("first", false));

        switc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(getApplicationContext());
                    if(notificationCompat.areNotificationsEnabled())
                    {
                        Toast.makeText(summaryActivity.this, "Daily reminder notification set up", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                        editor.putBoolean("first", true);
                        editor.apply();
                        switc.setChecked(true);
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY,22);
                        c.set(Calendar.MINUTE,00);
                        long delay = c.getTimeInMillis()-(System.currentTimeMillis());
                        scheduleNotification(getNotification( "Add expense" ) ,delay ) ;
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                        startActivity(intent);
                        switc.setChecked(false);
                    }


                }
                else {
                    Toast.makeText(summaryActivity.this, "Daily reminder notification turned off", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("first",false);
                    editor.apply();
                    switc.setChecked(false);

                }
            }
        });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottonnav);

        bottomNavigationView.setSelectedItemId(R.id.summary);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;

                    case R.id.summary:
                        return true;
                }
                return false;
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void all(View view)
    {
        Intent intent = new Intent(summaryActivity.this,TransactionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void manage(View view)
    {
        Intent intent = new Intent(summaryActivity.this,ManageCatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void scheduleNotification (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher.class ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, 0 , notificationIntent , PendingIntent. FLAG_IMMUTABLE ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,delay,AlarmManager.INTERVAL_DAY,pendingIntent);

    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Reminder: Add today's Expenses" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable.swlogo);
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    private void getData() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        long startTimestamp = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long endTimestamp = cal.getTimeInMillis();


        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                .whereGreaterThanOrEqualTo("time", startTimestamp)
                .whereLessThan("time", endTimestamp)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> dslist = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot ds:dslist)
                        {
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            String cat = expenseModel.getCategory();
                            if (map.containsKey(cat)) {
                                Long existingValue = map.get(cat);
                                Long newValue = existingValue + expenseModel.getAmount();
                                map.put(cat, newValue);
                            } else {
                                map.put(cat,expenseModel.getAmount());
                            }
                        }
                        mRecyclerView = findViewById(R.id.listView);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        map =sortByValue(map) ;

                        // Set up the adapter
                        MyAdapter adapter = new MyAdapter(map);
                        mRecyclerView.setAdapter(adapter);



                    }
                });
    }
    public static HashMap<String, Long> sortByValue(Map<String, Long> hm)
    {
        List<Map.Entry<String, Long> > list =
                new LinkedList<Map.Entry<String, Long> >(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long> >() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}