package com.example.expmanager;

import static android.graphics.Color.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expmanager.databinding.ActivityDashBoardBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DashBoardActivity extends AppCompatActivity implements OnItemsClick{
   ActivityDashBoardBinding binding;
    private ExpenseAdapter expenseAdapter;
    private long income=0,expense=0,backPresseed;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Map<String, Long> map = new HashMap<>() ;
    Map<String, Long> map1 = new HashMap<>() ;
    Intent intent,intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseAdapter =new ExpenseAdapter(this,this);
        binding.recycler.setAdapter(expenseAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        intent = new Intent(DashBoardActivity.this,AddExpenseActivity.class);
        intent1 = new Intent(DashBoardActivity.this,summaryActivity.class);


        SimpleDateFormat sdf = new SimpleDateFormat("MMMM YYY");
        String month = sdf.format(new Date());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        int index = email.indexOf("@");
        String shortEmail = email.substring(0, index).toUpperCase();
        String shortEmail1 = email.substring(0, index);
        String intial = shortEmail.substring(0,1);
        String capemail = intial + shortEmail1.substring(1);

        binding.tr.setText(month);
        binding.emailText.setText(email);
        binding.shortEmailText.setText(capemail);
        binding.initialText.setText(intial);
        binding.initialText1.setText(intial);

        binding.usercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.usernameCard.setVisibility(View.VISIBLE);
            }
        });
        binding.closeUsernameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.usernameCard.setVisibility(View.GONE);
            }
        });




        BottomNavigationView bottomNavigationView  = binding.bottonnav;
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        return true;

                    case R.id.summary:
                        intent1.putExtra("income",String.valueOf(income));
                        intent1.putExtra("expense",String.valueOf(expense));
                        intent1.putExtra("balance",String.valueOf(income-expense));
                        intent1.putExtra("month",month);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                }
                return false;
            }
        });


        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(
                        "type","Expense"
                );
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = binding.bottonnav;

        if(backPresseed+3000 > System.currentTimeMillis())
        {
            bottomNavigationView.setSelectedItemId(R.id.dashboard);
          this.finishAffinity();
        }
        else {
            Toast.makeText(DashBoardActivity.this, "Press back again to exit",Toast.LENGTH_SHORT).show();
        }

        backPresseed=System.currentTimeMillis();
    }

    @Override
    protected void onStart(){
        BottomNavigationView bottomNavigationView = binding.bottonnav;
        super.onStart();
        binding.pbar.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DashBoardActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        BottomNavigationView bottomNavigationView = binding.bottonnav;
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        income=0;
        expense=0;
        getData();
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
                .orderBy("time",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        binding.pbar.setVisibility(View.GONE);
                        expenseAdapter.clear();
                        List<DocumentSnapshot> dslist = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot ds:dslist)
                        {
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            String cat = expenseModel.getCategory();
                            String tt =expenseModel.getType();

                            if(expenseModel.getType().equals("Expense"))
                            {
                                if (map.containsKey(cat)) {
                                    Long existingValue = map.get(cat);
                                    Long newValue = existingValue + expenseModel.getAmount();
                                    map.put(cat, newValue);
                                } else {
                                    map.put(cat,expenseModel.getAmount());

                                }
                            }
                            else {
                                if (map1.containsKey(cat)) {
                                    Long existingValue = map1.get(cat);
                                    Long newValue = existingValue + expenseModel.getAmount();
                                    map1.put(cat, newValue);
                                } else {
                                    map1.put(cat,expenseModel.getAmount());

                                }

                            }


                            if(expenseModel.getType().equals("Income"))
                            {
                                income +=expenseModel.getAmount();
                            }
                            else
                            {
                                expense +=expenseModel.getAmount();
                            }
                            expenseAdapter.add(expenseModel);
                        }
                        binding.sBalance.setText("₹"+(income-expense));
                        binding.sExpense.setText("₹"+expense);
                        binding.sIncome.setText("₹"+income);
                        setUpGraph();
                        setUpGraph1();

                    }
                });
    }

    private void setUpGraph(){
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();

        for (Map.Entry<String, Long> entry : map.entrySet())
        {
            pieEntryList.add(new PieEntry(entry.getValue(),entry.getKey()));
            int c = getrandomcolor();
            colorsList.add(c);
        }


            if(!pieEntryList.isEmpty())
        {
            PieDataSet pieDataSet =new PieDataSet(pieEntryList,"");
            pieDataSet.setColors(colorsList);
            PieData pieData = new PieData(pieDataSet);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            pieDataSet.setValueLineColor(parseColor("#f8f8f8"));
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setValueTextSize(12);
            pieDataSet.setValueTextColor(parseColor("#000000"));

            binding.pieChart.setData(pieData);
            binding.pieChart.invalidate();
            binding.pieChart.animateXY(1000,1000);
            binding.pieChart.setHoleRadius(70);
            binding.pieChart.setCenterText("Expense\n ₹"+expense);
            binding.pieChart.setCenterTextSize(17);
            binding.pieChart.setCenterTextColor(parseColor("#2bb0c0"));
            binding.pieChart.setRotationAngle(90);
            binding.pieChart.setExtraTopOffset(5f);
            binding.pieChart.setExtraBottomOffset(6f);
            binding.pieChart.getLegend().setEnabled(false);
            binding.pieChart.getDescription().setText(" ");
            binding.pieChart.setHoleColor(373535);
            binding.pieChart.setUsePercentValues(true);
        }
    }

    private int getrandomcolor() {
        Random rand = new Random();
       /* int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;*/
        int red = 128 + rand.nextInt(128);
        int green = 128 + rand.nextInt(128);
        int blue = 128 + rand.nextInt(128);
        int alpha = 255;
        int pastelColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
        return pastelColor;
    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setUpGraph1(){
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();

        for (Map.Entry<String, Long> entry : map1.entrySet())
        {
            pieEntryList.add(new PieEntry(entry.getValue(),entry.getKey()));
            int c = getrandomcolor();
            colorsList.add(c);
        }


        if(!pieEntryList.isEmpty())
        {
            PieDataSet pieDataSet =new PieDataSet(pieEntryList,"");
            pieDataSet.setColors(colorsList);
            PieData pieData = new PieData(pieDataSet);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            pieDataSet.setValueLineColor(parseColor("#f8f8f8"));
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setValueTextSize(10);
            pieDataSet.setValueTextColor(parseColor("#000000"));

            binding.pieChart1.setData(pieData);
            binding.pieChart1.invalidate();
            binding.pieChart1.animateXY(1000,1000);
            binding.pieChart1.setHoleRadius(70);
            binding.pieChart1.setCenterText("Income\n ₹"+income);
            binding.pieChart1.setCenterTextColor(parseColor("#2bb0c0"));
            binding.pieChart1.setCenterTextSize(17);
            binding.pieChart1.setRotationAngle(102);
            binding.pieChart1.setExtraTopOffset(5f);
            binding.pieChart1.setExtraBottomOffset(6f);
            binding.pieChart1.getLegend().setEnabled(false);
            binding.pieChart1.getDescription().setText(" ");
            binding.pieChart1.setHoleColor(373535);
            binding.pieChart1.setUsePercentValues(true);

        }
    }
    public void createSignOutDialog(View view) {
        binding.usernameCard.setVisibility(View.GONE);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashBoardActivity.this,R.style.RoundShapeTheme);

        builder.setTitle("Log Out")
                .setMessage("Are you sure you want to Log Out ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

}