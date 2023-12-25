package com.example.demoappspa.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.demoappspa.Entity.OnLogoutListener;
import com.example.demoappspa.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {
    View view;
    CircleImageView imgAvatarProfile;
    TextView txtNameProfile,txtEmailProfile;
    LinearLayout btnNotificationsProfile, btnLogoutProfile;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mgoogleSignInClient;
    private OnLogoutListener mListener;
    public ProfileFragment(OnLogoutListener Listener){
        mListener = Listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth  = FirebaseAuth.getInstance();
        mapping();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        loadingUser();
        btnLogoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAccount();
            }
        });
        openNotification();
        return view;
    }
    private void openNotification(){
        btnNotificationsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationFragment notificationFragment = new NotificationFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out);  // popExit
                MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
                fragmentTransaction.replace(R.id.frame_container, notificationFragment);
                fragmentTransaction.addToBackStack(ProfileFragment.class.getSimpleName());//thêm Fragment vào stack để quay lại
                fragmentTransaction.commit();
            }
        });
    }
    private void logoutAccount() {
        mgoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mListener.onLogout();
                } else Toast.makeText(getActivity(), "Logout faild", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadingUser() {
        Picasso.get().load(MainActivity.user.getPhotoURL()).into(imgAvatarProfile);
        txtNameProfile.setText(MainActivity.user.getName());
        txtEmailProfile.setText(MainActivity.user.getEmail());
    }

    private void mapping() {
        imgAvatarProfile = view.findViewById(R.id.imgAvatarProfile);
        txtNameProfile = view.findViewById(R.id.txtNameProfile);
        txtEmailProfile = view.findViewById(R.id.txtEmailProfile);
        btnNotificationsProfile = view.findViewById(R.id.btnNotificationsProfile);
        btnLogoutProfile = view.findViewById(R.id.btnLogoutProfile);
    }
}
