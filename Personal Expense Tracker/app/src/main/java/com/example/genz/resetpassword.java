package com.example.finora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;

public class resetpassword extends AppCompatActivity {
    private EditText passwordEmail;
    private Button resetpassword;
    private FirebaseAuth firebaseAuth;

    public resetpassword() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        passwordEmail=(EditText)findViewById(R.id.pass_email);
        resetpassword=(Button)findViewById(R.id.reset_pass);
        firebaseAuth=FirebaseAuth.getInstance();

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=passwordEmail.getText().toString().trim();

                if(useremail.equals(""))
                {
                  passwordEmail.setError("Email Required...",null);
                  return;
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(resetpassword.this,"Password reset email sent. Check your inbox.",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(resetpassword.this,home_screen.class));
                            }
                            else
                            {
                                String errorMsg = "Error sending reset email.";
                                Exception ex = task.getException();
                                if (ex instanceof FirebaseAuthException) {
                                    String code = ((FirebaseAuthException) ex).getErrorCode();
                                    if ("ERROR_INVALID_EMAIL".equals(code)) {
                                        errorMsg = "Invalid email format.";
                                        passwordEmail.setError(errorMsg, null);
                                        passwordEmail.requestFocus();
                                    } else if ("ERROR_USER_NOT_FOUND".equals(code)) {
                                        errorMsg = "No account found with this email.";
                                        passwordEmail.setError(errorMsg, null);
                                        passwordEmail.requestFocus();
                                    } else {
                                        errorMsg = ex.getMessage() != null ? ex.getMessage() : errorMsg;
                                    }
                                } else if (ex instanceof FirebaseNetworkException) {
                                    errorMsg = "Network error. Please check your connection.";
                                } else if (ex != null && ex.getMessage() != null) {
                                    errorMsg = ex.getMessage();
                                }
                                Toast.makeText(resetpassword.this,errorMsg,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}