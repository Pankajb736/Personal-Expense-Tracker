package com.example.finora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import java.util.regex.Pattern;

public class home_screen extends AppCompatActivity {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-={}\\[\\]|:;\"'<>.,/~`]).{8,}$");
    private EditText mEmail;
    private EditText mPass;
    private CheckBox remember;

    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        // Check if user is already logged in and remembered
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true") && mAuth.getCurrentUser() != null) {
            startActivity(new Intent(home_screen.this, first_home_page.class));
            finish();
            return;
        }

        loginDetails();
    }

    private void loginDetails() {
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView mforget_password = findViewById(R.id.forgot_password);
        TextView mSignUpHere = findViewById(R.id.signup_reg);
        remember = findViewById(R.id.checkBox2);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required.");
                    mEmail.requestFocus();
                    Toast.makeText(home_screen.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    mEmail.setError("Enter a valid email address.");
                    mEmail.requestFocus();
                    Toast.makeText(home_screen.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password required.");
                    mPass.requestFocus();
                    Toast.makeText(home_screen.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PASSWORD_PATTERN.matcher(pass).matches()) {
                    mPass.setError("Password must be 8+ chars with upper, lower, digit, and special character, and no spaces.");
                    mPass.requestFocus();
                    Toast.makeText(home_screen.this, "Weak password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDialog.setMessage("Logging in...");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            checkEmailVerification();
                        } else {
                            String errorMsg = "Login failed.";
                            Exception ex = task.getException();
                            if (ex instanceof FirebaseAuthInvalidUserException) {
                                String code = ((FirebaseAuthInvalidUserException) ex).getErrorCode();
                                if ("ERROR_USER_DISABLED".equals(code)) {
                                    errorMsg = "This account has been disabled.";
                                } else if ("ERROR_USER_NOT_FOUND".equals(code)) {
                                    errorMsg = "User not found. Please register first.";
                                    mEmail.setError(errorMsg);
                                    mEmail.requestFocus();
                                } else {
                                    errorMsg = "No account found with this email.";
                                    mEmail.setError(errorMsg);
                                    mEmail.requestFocus();
                                }
                            } else if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                                String code = ((FirebaseAuthInvalidCredentialsException) ex).getErrorCode();
                                if ("ERROR_INVALID_EMAIL".equals(code)) {
                                    errorMsg = "Invalid email format.";
                                    mEmail.setError(errorMsg);
                                    mEmail.requestFocus();
                                } else if ("ERROR_WRONG_PASSWORD".equals(code)) {
                                    errorMsg = "Wrong password. Please try again.";
                                    mPass.setError(errorMsg);
                                    mPass.requestFocus();
                                } else if ("ERROR_INVALID_LOGIN_CREDENTIALS".equals(code)) {
                                    errorMsg = "Email or password is incorrect.";
                                } else {
                                    errorMsg = ex.getMessage() != null ? ex.getMessage() : errorMsg;
                                }
                            } else if (ex instanceof FirebaseNetworkException) {
                                errorMsg = "Network error. Please check your connection.";
                            } else if (ex instanceof FirebaseAuthException) {
                                String code = ((FirebaseAuthException) ex).getErrorCode();
                                if ("ERROR_TOO_MANY_REQUESTS".equals(code)) {
                                    errorMsg = "Too many failed attempts. Try again later.";
                                } else {
                                    errorMsg = ex.getMessage() != null ? ex.getMessage() : errorMsg;
                                }
                            } else if (ex != null && ex.getMessage() != null) {
                                errorMsg = ex.getMessage();
                            }
                            Toast.makeText(home_screen.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(home_screen.this,"Remember me Checked..",Toast.LENGTH_SHORT).show();
                }
                else if(!buttonView.isChecked())
                {
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(home_screen.this,"Remember me Unchecked..",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSignUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_screen.this, Registration.class);
                startActivity(intent);
                finish(); // Close login activity when going to registration
            }
        });

        mforget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_screen.this, resetpassword.class);
                startActivity(intent);
                // Don't finish here to allow back navigation
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Authentication error occurred", Toast.LENGTH_LONG).show();
            return;
        }

        if (firebaseUser.isEmailVerified()) {
            // Save remember me preference if checked
            if (remember.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "true");
                editor.apply();
            }
            
            Intent intent = new Intent(home_screen.this, first_home_page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please verify your email first", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            // Optionally, you could show a dialog to resend verification email
        }
    }
}