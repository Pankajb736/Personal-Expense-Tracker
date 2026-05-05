package com.example.finora;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-={}\\[\\]|:;\"'<>.,/~`]).{8,}$");
    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;

    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserInfoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);

        registration();
    }
    private void registration(){
        mEmail=findViewById(R.id.email_reg);
        mPass=findViewById(R.id.password_reg);
        Button btnReg = findViewById(R.id.btn_reg);
        TextView mSignin = findViewById(R.id.signin_here);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Registration.this,home_screen.class);
                startActivity(intent);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email required.");
                    mEmail.requestFocus();
                    return;
                }
                if(!EMAIL_PATTERN.matcher(email).matches()){
                    mEmail.setError("Enter a valid email address.");
                    mEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPass.setError("Password required.");
                    mPass.requestFocus();
                    return;
                }
                if(!PASSWORD_PATTERN.matcher(pass).matches()){
                    mPass.setError("Password must be 8+ chars with upper, lower, digit, and special character, and no spaces.");
                    mPass.requestFocus();
                    return;
                }
                mDialog.setMessage("Processing...");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if(task.isSuccessful()){
                            sendEmailVerification();
                            FirebaseUser mUser=mAuth.getCurrentUser();
                            if(mAuth!=null && mUser!=null) {
                                String uid = mUser.getUid();
                                DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(uid);
                                myRootRef.child("Email").setValue(email);
                            }
                        } else {
                            String errorMsg = "Registration failed.";
                            Exception ex = task.getException();
                            if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                                String code = ((FirebaseAuthInvalidCredentialsException) ex).getErrorCode();
                                if ("ERROR_INVALID_EMAIL".equals(code)) {
                                    errorMsg = "Invalid email format.";
                                    mEmail.setError(errorMsg);
                                    mEmail.requestFocus();
                                } else {
                                    errorMsg = "Password is too weak. Please use at least 6 characters.";
                                    mPass.setError(errorMsg);
                                    mPass.requestFocus();
                                }
                            } else if (ex instanceof FirebaseAuthException) {
                                String code = ((FirebaseAuthException) ex).getErrorCode();
                                if ("ERROR_EMAIL_ALREADY_IN_USE".equals(code)) {
                                    errorMsg = "This email is already registered. Please login or use a different email.";
                                    mEmail.setError(errorMsg);
                                    mEmail.requestFocus();
                                } else {
                                    errorMsg = ex.getMessage() != null ? ex.getMessage() : errorMsg;
                                }
                            } else if (ex instanceof FirebaseNetworkException) {
                                errorMsg = "Network error. Please check your connection.";
                            } else if (ex != null && ex.getMessage() != null) {
                                errorMsg = ex.getMessage();
                            }
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Registration.this,"Registration Successful.Verification mail sent successfully..",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this,home_screen.class));
                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Error occurred sending verification mail..",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}