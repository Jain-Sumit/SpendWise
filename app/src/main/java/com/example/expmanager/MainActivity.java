package com.example.expmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expmanager.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;

    private long backPresseed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    try {
                        startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
                        finish();
                    }
                    catch(Exception e)
                    {

                    }
                }
            }
        });
        binding.goToSignUpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                try {
                    startActivity(intent);
                }
                catch (Exception e){

                }
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailLogin.getText().toString().trim();
                String password = binding.passwordLogin.getText().toString().trim();
                if (email.length()<=0 || password.length()<=0)
                {
                    return;
                }
                binding.passwordLogin.onEditorAction(EditorInfo.IME_ACTION_DONE);
                binding.pbar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                try {
                                    startActivity(new Intent(MainActivity.this,DashBoardActivity.class));

                                }
                                catch (Exception e){
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                binding.pbar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
    @Override
    public void onBackPressed() {

        if(backPresseed+3000 > System.currentTimeMillis())
        {
            this.finishAffinity();
        }
        else {
            Toast.makeText(MainActivity.this, "Press back again to exit",Toast.LENGTH_SHORT).show();
        }

        backPresseed=System.currentTimeMillis();
    }

   public void ShowHidePass(View view){
       EditText et = findViewById(R.id.password_login);

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