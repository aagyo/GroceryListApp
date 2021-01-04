package com.example.grocerylistapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView signIn;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.btn_reg);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString();
                String mPass = password.getText().toString();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required field");
                    return;
                }
                if(TextUtils.isEmpty(mPass)){
                    email.setError("Required field");
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Succsessful", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}