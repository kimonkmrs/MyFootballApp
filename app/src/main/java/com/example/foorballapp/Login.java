package com.example.foorballapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText mPassword, mEmail;
    TextView mTextLogin;
    Button mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //mName=findViewById(R.id.editTextTextPersonName);
        mPassword = findViewById(R.id.editPassword);
        mEmail = findViewById(R.id.editTextEmail);
        //mConfirmPassword=findViewById(R.id.editConfirmPassword);
        mLoginButton = findViewById(R.id.loginButton);
        mTextLogin = findViewById(R.id.textLogin);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //String confirm_password=mConfirmPassword.getText().toString().trim();


                // Validate email
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Invalid Email Format.");
                    return;
                }

                // Validate password
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Weak Password. Password must be at least 6 characters long.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //authenticate user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar regardless of outcome

                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // Get the exception and its message
                            String errorMessage = "Login failed. Please try again.";
                            if (task.getException() instanceof FirebaseAuthException) {
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                switch (e.getErrorCode()) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email format.";
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        errorMessage = "Incorrect password. Please try again.";
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        errorMessage = "No account found with this email.";
                                        break;
                                    case "ERROR_USER_DISABLED":
                                        errorMessage = "Account has been disabled.";
                                        break;
                                    case "ERROR_TOO_MANY_REQUESTS":
                                        errorMessage = "Too many requests. Please try again later.";
                                        break;
                                    default:
                                        errorMessage = "Login failed. Please try again.";
                                        break;
                                }
                            }

                            Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        mTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
    }
}