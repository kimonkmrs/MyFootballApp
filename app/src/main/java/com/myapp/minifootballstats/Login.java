package com.myapp.minifootballstats;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText mPassword, mEmail;
    TextView mTextLogin;
    Button mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    FirebaseFirestore fStore;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPassword = findViewById(R.id.editPassword);
        mEmail = findViewById(R.id.editTextEmail);
        mLoginButton = findViewById(R.id.loginButton);
        mTextLogin = findViewById(R.id.textLogin);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        Log.d("Login", "onCreate: Activity created");  // Log activity creation
        mPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Index for drawableEnd (right-side drawable)

                if (mPassword.getCompoundDrawablesRelative()[drawableEnd] != null) {
                    int drawableWidth = mPassword.getCompoundDrawablesRelative()[drawableEnd].getBounds().width();
                    int touchX = (int) event.getRawX();
                    int rightEdge = mPassword.getRight();

                    // Check if touch is inside the drawable area
                    if (touchX >= rightEdge - drawableWidth - mPassword.getPaddingEnd()) {
                        int selection = mPassword.getSelectionEnd();

                        if (passwordVisible) {
                            // Change to password-hidden icon
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            // Change to password-visible icon
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }

                        passwordVisible = !passwordVisible;
                        mPassword.setSelection(selection); // Maintain cursor position
                        return true;
                    }
                }
            }
            return false;
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Login", "Login button clicked");  // Log button click

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // Validate email
                if (TextUtils.isEmpty(email)) {
                    Log.d("Login", "Email is empty");  // Log empty email
                    mEmail.setError("Email is Required.");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Log.d("Login", "Invalid email format: " + email);  // Log invalid email format
                    mEmail.setError("Invalid Email Format.");
                    return;
                }

                // Validate password

                //if (TextUtils.isEmpty(password)) {
                //    Log.d("Login", "Password is empty");  // Log empty password
                //    mPassword.setError("Password is Required.");
                //    return;
                //}
                //if (password.length() < 6) {
                //    Log.d("Login", "Password too short: " + password.length() + " characters");  // Log short password
                //    mPassword.setError("Weak Password. Password must be at least 6 characters long.");
                //    return;
                //}

                progressBar.setVisibility(View.VISIBLE);
                Log.d("Login", "Starting authentication for email: " + email);  // Log authentication start

                // Authenticate user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar regardless of outcome
                        Log.d("Login", "Authentication completed");  // Log authentication completion

                        if (task.isSuccessful()) {
                            Log.d("Login", "Authentication successful for email: " + email);  // Log successful login
                            checkUserRole(email);  // Check the user role
                        } else {
                            // Get the exception and its message
                            String errorMessage = "Login failed. Please try again.";
                            if (task.getException() instanceof FirebaseAuthException) {
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Log.e("Login", "FirebaseAuthException: " + e.getErrorCode() + " - " + e.getMessage());  // Log the specific error

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
                Log.d("Login", "Register link clicked");  // Log register link click
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    private void checkUserRole(String email) {
        Log.d("Login", "Checking user role for email: " + email);  // Log the email being checked

        fStore.collection("admin_emails").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Login", "Document found for email: " + email);  // Log if the document is found
                        // The user is an admin
                        Toast.makeText(Login.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity(true); // Admin user
                    } else {
                        Log.d("Login", "No document found for email: " + email);  // Log if the document is not found
                        // The user is a regular user
                        Toast.makeText(Login.this, "Logged in as User", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity(false); // Regular user
                    }
                } else {
                    Log.e("Login", "Error checking user role: " + task.getException().getMessage());  // Log any error
                    Toast.makeText(Login.this, "Error checking user role. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToMainActivity(boolean isAdmin) {
        Log.d("Login", "Navigating to MainActivity, isAdmin: " + isAdmin);  // Log navigation
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("isAdmin", isAdmin); // Pass the role information
        startActivity(intent);
        finish(); // Optionally finish the Login activity
    }
}
