package com.theappschef.boxnbiz

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.*
import com.theappschef.boxnbiz.databinding.ActivityProfileBinding
import java.io.*


class ProfileActivity : AppCompatActivity() {
    lateinit var imgProfile:ImageView

    private var loginViewModel: LoginViewModel? = null
    private var mDatabase: DatabaseReference? = null

    lateinit var     sharedPref:SharedPref

    private var binding: ActivityProfileBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        sharedPref = SharedPref(this)
        mDatabase = FirebaseDatabase.getInstance().reference
        imgProfile=findViewById(R.id.image)
        imgProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                perm()
            } else {
                ImagePicker.with(this)
                    .galleryOnly()
                    .saveDir(File(getFilesDir(), "boxnbix.png"))
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
        }


        try {
            if (File(filesDir.path + File.separatorChar +sharedPref.email+ ".png").exists()) {
                imgProfile.setImageURI(Uri.fromFile(File(filesDir.path + File.separatorChar + sharedPref.email+ ".png")))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }



        binding?.signOut?.setOnClickListener {

            startActivity(Intent(this,LoginActivity::class.java))
        }

            mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        for (dataSnapshot in snapshot.children) {
                            if (dataSnapshot.key ==sharedPref.email ) {
                                val profile = dataSnapshot.value as Map<*, *>?

                                if (profile != null) {
                                    setData(profile)
                                }

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


    }


    fun perm() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }
    @SuppressLint("SetTextI18n")
    fun setData(profile:  Map<*, *>){
        binding?.etName?.setText(profile.get("name").toString())
        binding?.etCompanyName?.setText(profile.get("company").toString())
        binding?.etContactNo?.setText(profile.get("phone").toString())
        binding?.etPromo?.setText(profile.get("promo").toString())
        binding?.etEmail?.setText(sharedPref.email+profile.get("suffix").toString())
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            imgProfile.setImageURI(uri)
            savefile(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    fun savefile(sourceuri: Uri) {
        val sourceFilename: String? =sourceuri.path
        val destinationFilename =
            filesDir.path + File.separatorChar + sharedPref.email+".png"
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(sourceFilename))
            bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) !== -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (bis != null) bis.close()
                if (bos != null) bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}