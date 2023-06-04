package com.example.expmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expmanager.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();

        binding.goToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                try {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                catch (Exception e){

                }
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.emailForSignUp.getText().toString();
                String password = binding.passwordForSignUp.getText().toString();
                if(email.trim().length()<=0 || password.trim().length()<=0){
                    return;
                }
                binding.pbar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(SignUpActivity.this,"User Created",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.pbar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });
    }
    public void ShowHidePass(View view){
        EditText et = findViewById(R.id.password_for_signUp);

        if(view.getId()==R.id.eye){

            if(et.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                // ((ImageView(view)).setImageResource(R.drawable.hide_password);
                //Show Password
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                // ((ImageView)(view)).setImageResource(R.drawable.show_password);
                //Hide Password
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }
}