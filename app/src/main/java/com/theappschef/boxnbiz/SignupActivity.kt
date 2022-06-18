package com.theappschef.boxnbiz

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.theappschef.boxnbiz.databinding.ActivitySignupBinding
import java.util.*

class SignupActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null

    private lateinit var binding: ActivitySignupBinding

    //    private DatabaseReference mDatabase;
    private lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        mDatabase = FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        binding.setLifecycleOwner(this)

        binding.setLoginViewModel(loginViewModel)

        binding.lblTagline.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))

        }

        loginViewModel!!.getUser().observe(this) { loginUser: LoginUser ->
            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).strEmailAddress)) {
                binding.txtEmailAddress.error = "Enter an E-Mail Address"
                binding.txtEmailAddress.requestFocus()
            } else if (!loginUser.isEmailValid) {
                binding.txtEmailAddress.error = "Enter a Valid E-mail Address"
                binding.txtEmailAddress.requestFocus()
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).strPassword)) {
                binding.txtPassword.error = "Enter a Password"
                binding.txtPassword.requestFocus()
            } else if (!loginUser.isPasswordLengthGreaterThan5) {
                binding.txtPassword.error = "Enter at least 6 Digit password"
                binding.txtPassword.requestFocus()
            } else if (binding.txtCompanyName.text.isEmpty()) {
                binding.txtCompanyName.error = "Enter a valid value"

            } else if (binding.txtname.text.isEmpty()) {
                binding.txtname.error = "Enter a valid value"

            } else if (binding.txtMobile.text.isEmpty()) {
                binding.txtMobile.error = "Enter a valid value"
            } else if (binding.promocode.text.isEmpty()) {
                binding.promocode.error = "Enter a valid value"
            } else {
                binding.lblEmailAnswer.text = loginUser.strEmailAddress
                binding.lblPasswordAnswer.text = loginUser.strPassword

                try {
                    var email = binding.lblEmailAnswer.text.toString()


                    var suffix=""
                    if (email.contains(".com")) {
                        email = email.removeSuffix(".com")
                        suffix=".com"
                    }
                    if (email.contains(".in")) {
                        email = email.replace(".in", "")
                        suffix=".in"
                    }
                    if (email.contains(".net")) {
                        email = email.replace(".net", "")
                        suffix=".net"
                    }

                    var b:Boolean=false

                    mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                for (dataSnapshot in snapshot.children) {
                                    if (dataSnapshot.key ==email ) {
                                        b=true
                                    }
                                }

                                if(b){
                                    Toast.makeText(this@SignupActivity,"User Already Exists",Toast.LENGTH_SHORT).show()
                                }else {

                                    val sharedPref = SharedPref(this@SignupActivity)
                                    sharedPref.email = email
                                    mDatabase.child(email)
//                        .child(binding.txtPassword.text.toString())
                                        .setValue(
                                            Profile(
                                                binding.txtCompanyName.text.toString(),
                                                binding.txtname.text.toString(),
                                                binding.txtMobile.text.toString(),
                                                binding.promocode.text.toString(),
                                                binding.txtPassword.text.toString(),suffix
                                            )
                                        ).addOnCompleteListener(
                                            OnCompleteListener {
                                                startActivity(
                                                    Intent(
                                                        this@SignupActivity,
                                                        OnBoardingActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }).addOnFailureListener {
                                            it.printStackTrace()
                                        }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onBackPressed() {

        startActivity(Intent(this,LoginActivity::class.java))
//        super.onBackPressed()
    }
}
