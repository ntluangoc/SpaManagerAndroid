package com.example.demoappspa.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.demoappspa.Entity.OnLogoutListener;
import com.example.demoappspa.Entity.User;
import com.example.demoappspa.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements OnLogoutListener {
    FrameLayout frame_container;
    public static MeowBottomNavigation bottomNavigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public static User user;
    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        Log.d("user", "User google: " + user.toString());
        mapping();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        db = FirebaseFirestore.getInstance();
        bottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.icon_home));
        bottomNavigationView.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_list_alt_24));
        bottomNavigationView.add(new MeowBottomNavigation.Model(3, R.drawable.icon_schedule));
        bottomNavigationView.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_person_50));

        loadFragment(new HomeFragment());
        bottomNavigationView.show(1, true);
        bottomNavigationView.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Fragment fragment;
                switch (model.getId()){
                    case 1:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        break;
                    case 2:
                        fragment = new ListProductFragment();
                        loadFragment(fragment);
                        break;
                    case 3:
                        fragment = new ScheduleFragment();
                        loadFragment(fragment);
                        break;
                    case 4:
                        fragment = new ProfileFragment(MainActivity.this::onLogout);
                        loadFragment(fragment);
                        break;
                }
                return null;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    private void mapping() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        frame_container = findViewById(R.id.frame_container);

    }

    @Override
    public void onLogout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}