package com.example.expmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.example.expmanager.databinding.ActivityManageCatBinding;
import com.google.android.material.button.MaterialButton;
import java.util.Collections;
import java.util.List;


public class ManageCatActivity extends AppCompatActivity  {
    ActivityManageCatBinding binding;
    RecyclerView mRecyclerView;
    MaterialButton btnAdd;
    EditText inputLabel;
    List<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=  ActivityManageCatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnAdd = findViewById(R.id.btn_add1);
        inputLabel = findViewById(R.id.input_label);

        loadSpinnerData();



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String label = inputLabel.getText().toString().toLowerCase();
                if (label.trim().length() > 0) {

                    if(labels.contains(label))
                    {
                        Toast.makeText(ManageCatActivity.this, "Already Present", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.insertLabel(label);

                    inputLabel.setText("");
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(inputLabel.getWindowToken(), 0);

                    Toast.makeText(ManageCatActivity.this, label+" Added", Toast.LENGTH_SHORT).show();
                    loadSpinnerData();

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter label name",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void loadSpinnerData() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        labels = db.getAllLabels();

        mRecyclerView = findViewById(R.id.cat);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(labels);
        CatAdaptor adapter = new CatAdaptor(getApplicationContext(),labels);
        mRecyclerView.setAdapter(adapter);
    }

}
