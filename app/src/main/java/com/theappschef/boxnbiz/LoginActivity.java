package com.theappschef.boxnbiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private DatabaseReference mDatabase;
    // ...
    private com.theappschef.boxnbiz.databinding.ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

        loginViewModel.getUser().observe(this, loginUser -> {

            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrEmailAddress())) {
                binding.txtEmailAddress.setError("Enter an E-Mail Address");
                binding.txtEmailAddress.requestFocus();
            } else if (!loginUser.isEmailValid()) {
                binding.txtEmailAddress.setError("Enter a Valid E-mail Address");
                binding.txtEmailAddress.requestFocus();
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrPassword())) {
                binding.txtPassword.setError("Enter a Password");
                binding.txtPassword.requestFocus();
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                binding.txtPassword.setError("Enter at least 6 Digit password");
                binding.txtPassword.requestFocus();
            } else {
                try {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            try {

                                binding.lblEmailAnswer.setText(loginUser.getStrEmailAddress());
//                                binding.lblPasswordAnswer.setText(loginUser.getStrPassword());

                                String email = binding.lblEmailAnswer.getText().toString();
                                if (email.endsWith(".com")) {
                                    email = email.replace(".com", "");
                                }
                                if (email.endsWith(".in")) {
                                    email = email.replace(".in", "");
                                }
                                if (email.endsWith(".net")) {
                                    email = email.replace(".net", "");
                                }
                                SharedPref sharedPref = new SharedPref(LoginActivity.this);
                                sharedPref.setEmail(email);
                                boolean f=false;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.getKey().equals(email)) {
                                        Map<String, String> profile = (Map<String, String>) dataSnapshot.getValue();
//                                        Profile profile= (Profile) dataSnapshot.getValue();
                                        if (profile.get("pass").equals(binding.txtPassword.getText().toString())) {
                                         f=true;
                                            startActivity(new Intent(LoginActivity.this, OnBoardingActivity.class));
                                            finish();
                                        }
                                    }
                                }
                                if(!f)
                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                                binding.lblPasswordAnswer.setText("Invalid username or password");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.signup.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));

        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}