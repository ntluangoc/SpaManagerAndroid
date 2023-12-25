package com.example.demoappspa.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoappspa.Entity.User;
import com.example.demoappspa.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {
    Button btnSignIn;

    public GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private static String UID;
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();
        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_sign_in);
        btnSignIn.startAnimation(animation);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            UID = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            String phoneNumber = firebaseUser.getPhoneNumber();
            String photoURL = firebaseUser.getPhotoUrl().toString();
            user = new User(UID, name, email, phoneNumber, photoURL);
            Log.d("user", "User Login: " + user.toString());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void requestGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                UID = account.getId();
                Log.d("UID", "ID google: " + account.getId());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "Login failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("UID", "ID google: " + UID);
                            onStart();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    // [END auth_with_google]

    private void mapping() {
        btnSignIn = findViewById(R.id.btnSignIn);

    }


}