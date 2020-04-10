package com.example.chatapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private TextView mLogin;
    private EditText mEmail,mName,mPassword;
    private FirebaseAuth mAuth;
    private Button mRegister;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mPassword  = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        mLogin = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Sign Up");

        onRegisterButtonClick();
        onLoginClick();
    }

    private void onLoginClick() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

    }


    private void onRegisterButtonClick() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String name = mName.getText().toString();
                String password = mPassword.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Credentials missing",Toast.LENGTH_LONG).show();

                }else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password too Short",Toast.LENGTH_LONG).show();
                }else{
                    registerUser(email,password,name);
                }
            }
        });

    }

    private void registerUser(final String email, String password, final String name) {
        final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Registering new User Please Wait...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this,"New user registration successful",Toast.LENGTH_SHORT).show();
                    List<String> list = new ArrayList<>();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("name",name);
                    map.put("email",email);
                    map.put("id",mAuth.getCurrentUser().getUid());
                    map.put("imageUrl","default");
                    map.put("friends",list);
                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this,"New User Registration successful. Now update the profile",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            finish();
                        }
                    });

                }else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this,"New user registration failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
