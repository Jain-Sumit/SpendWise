package com.example.expmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expmanager.databinding.ActivityUpdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityUpdateBinding binding;
    public String newType, ct,expenseId;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.category);
        spinner.setOnItemSelectedListener(this);

        expenseId = getIntent().getStringExtra("expenseId");
        String amount = getIntent().getStringExtra("amount");
        String note = getIntent().getStringExtra("note");
        String category = getIntent().getStringExtra("category");
        String type = getIntent().getStringExtra("type");
        loadSpinnerData(category);


        binding.amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        binding.amount.setText(amount);
        // binding.category.setText(category);
        binding.note.setText(note);

        switch (type) {
            case "Income":
                newType = "Income";
                binding.incomRadio.setChecked(true);
                break;

            case "Expense":
                newType = "Expense";
                binding.expenseRadio.setChecked(true);
                break;
        }

        binding.anc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateActivity.this,ManageCatActivity.class);
                startActivity(i);
            }
        });

        binding.incomRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newType = "Income";
                binding.incomRadio.setChecked(true);
                binding.expenseRadio.setChecked(false);

            }
        });

        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newType = "Expense";
                binding.expenseRadio.setChecked(true);
                binding.incomRadio.setChecked(false);


            }
        });


        binding.updateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = binding.amount.getText().toString();
                String note = binding.note.getText().toString();
                //String category = binding.category.getText().toString();

                FirebaseFirestore
                        .getInstance()
                        .collection("expenses")
                        .document(expenseId)
                        .update("amount", Long.parseLong(amount), "note", note, "category", ct, "type", newType)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();

                                Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        binding.deleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeleteDialog(v);
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ct = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadSpinnerData(String category) {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        List<String> labels = db.getAllLabels();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, labels);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        for (int i = 0; i < dataAdapter.getCount(); i++) {
            if (dataAdapter.getItem(i).equals(category)) {
                spinner.setSelection(i);
                break;
            }

        }
    }

    private void delete(){
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .delete();
        Toast.makeText(UpdateActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
    public void createDeleteDialog(View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(UpdateActivity.this,R.style.RoundShapeTheme);

        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       delete();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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