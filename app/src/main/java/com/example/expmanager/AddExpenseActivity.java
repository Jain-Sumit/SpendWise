package com.example.expmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expmanager.databinding.ActivityAddExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityAddExpenseBinding binding;
    private String type;
    private String ct;
    private Spinner spinner;
    private DatePickerDialog.OnDateSetListener setListener;
    private long stmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

        setContentView(binding.getRoot());

        spinner = findViewById(R.id.category);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        stmp = System.currentTimeMillis();

        spinner.setOnItemSelectedListener(this);
        loadSpinnerData();

        binding.saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExpense();
            }
        });

        binding.cancelExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.anc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddExpenseActivity.this, ManageCatActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        type = getIntent().getStringExtra("type");
        if ("Income".equals(type)) {
            binding.incomRadio.setChecked(true);
        } else {
            binding.expenseRadio.setChecked(true);
        }

        binding.incomRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Income";
            }
        });

        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Expense";
            }
        });

        binding.startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddExpenseActivity.this,
                        android.R.style.Theme_Material_Dialog_Alert,
                        setListener,
                        year,
                        month,
                        day
                );
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getWindow();
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year1, int month1, int dayOfMonth) {
                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.YEAR, year1);
                cal1.set(Calendar.MONTH, month1);
                cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cal1.set(Calendar.HOUR_OF_DAY, 12);
                cal1.set(Calendar.MINUTE, 0);
                stmp = cal1.getTimeInMillis();
                int mo = month1 + 1;
                String date = dayOfMonth + "/" + mo + "/" + year1;
                binding.startdate.setText(date);
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void createExpense() {
        String expenseId = UUID.randomUUID().toString();
        String amount = binding.amount.getText().toString();
        String note = binding.note.getText().toString();

        boolean incomeChecked = binding.incomRadio.isChecked();
        type = incomeChecked ? "Income" : "Expense";

        if (amount.trim().length() == 0) {
            binding.amount.setError("Empty");
            return;
        }

        ExpenseModel expenseModel = new ExpenseModel(
                expenseId,
                note,
                ct,
                type,
                Long.parseLong(amount),
                stmp,
                FirebaseAuth.getInstance().getUid()
        );

        FirebaseFirestore.getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel);

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ct = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void loadSpinnerData() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, db.getAllLabels());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }
}
