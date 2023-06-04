package com.example.expmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expmanager.databinding.ActivityTransactionBinding;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TransactionActivity extends AppCompatActivity implements OnItemsClick{
    ActivityTransactionBinding binding;
    private ExpenseAdapter expenseAdapter;
    private long income=0,expense=0;

    DatePickerDialog.OnDateSetListener setListener,setListener1;

    private Button sbutton,ebutton;

    private long stmp=0,etmp;





    Intent intent =  new Intent(TransactionActivity.this,AddExpenseActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseAdapter =new ExpenseAdapter(this,this);
        binding.recycler.setAdapter(expenseAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        sbutton = findViewById(R.id.startdate);
        ebutton = findViewById(R.id.endDate);


        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar cal2 = Calendar.getInstance();
        etmp = cal2.getTimeInMillis();






        sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TransactionActivity.this, android.R.style.Theme_Material_Dialog_Alert
                        ,setListener,year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getWindow();
                datePickerDialog.show();

            }
        });
        ebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TransactionActivity.this,android.R.style.Theme_Material_Dialog_Alert
                        ,setListener1,year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getWindow();
                datePickerDialog.show();

            }
        });

        setListener = (view, year1, month1, dayOfMonth) -> {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.YEAR,year1);
            cal1.set(Calendar.MONTH,month1);
            cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal1.set(Calendar.HOUR_OF_DAY,0);
            cal1.set(Calendar.MINUTE,0);

            stmp = cal1.getTimeInMillis();
            month1 = month1 +1;
            String date = dayOfMonth+"/"+ month1 +"/"+ year1;
            sbutton.setText(date);

            Log.d("start",String.valueOf(stmp));



        };

        setListener1 = (view, year12, month12, dayOfMonth1) -> {
            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.YEAR,year12);
            cal1.set(Calendar.MONTH,month12);
            cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
            etmp = cal1.getTimeInMillis();
            month12 = month12 +1;
            String date = dayOfMonth1+"/"+ month12 +"/"+ year12;
            ebutton.setText(date);


        };

        binding.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income=0;
                expense=0;
                getData();
            }
        });


    }





    @Override
    protected void onStart(){
        super.onStart();
        binding.pbar.setVisibility(View.VISIBLE);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TransactionActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        income=0;
        expense=0;
        getData();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                .whereGreaterThanOrEqualTo("time", stmp)
                .whereLessThan("time", etmp)
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

                    }
                });
    }


    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }


}