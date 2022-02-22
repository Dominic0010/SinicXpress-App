package com.example.sinicxpress.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.sinicxpress.activities.constant.AppConstant
import com.example.sinicxpress.activities.permissions.AppPermissions
import com.example.sinicxpress.activities.utility.LoadingDialog
import com.example.sinicxpress.activities.utility.State
import com.example.sinicxpress.activities.viewModels.LoginViewModel
import com.example.sinicxpress.databinding.ActivitySignUpBinding
import com.firebase.ui.auth.AuthUI.TAG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var appPermissions: AppPermissions
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var FirstName: String
    private lateinit var LastName: String
    private lateinit var PhoneNumber:String
    private lateinit var email: String
    private lateinit var password: String
    private var image: Uri? = null
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var database:FirebaseDatabase
    private lateinit var riderInfoRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPermissions = AppPermissions()
        loadingDialog = LoadingDialog(this)
        auth = Firebase.auth

        init()

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.txtLogin.setOnClickListener { onBackPressed() }


        binding.btnSignUp.setOnClickListener {
            if (areFieldReady()) {
                if (image != null) {
                    lifecycleScope.launchWhenStarted {
                        loginViewModel.signUp(email, password, FirstName,  image!!).collect {
                            when (it) {
                                is State.Loading<*> -> {
                                    if (it.flag == true)
                                        loadingDialog.startLoading()
                                }

                                is State.Success<*> -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.data.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    onBackPressed()

                                }
                                is State.Failed<*> -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.error,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Please select image",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
        }


    }

    private fun init(){
        database = FirebaseDatabase.getInstance()
        riderInfoRef = database.getReference(Common.RIDER_INFO_REFERENCE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstant.STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Snackbar.make(
                    binding.root,
                    "Storage Permission Denied",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }




    private fun areFieldReady(): Boolean {
        FirstName = binding.firstName.text.trim().toString()
        LastName = binding.lastName.text.trim().toString()
        PhoneNumber = binding.phoneNumber.text.trim().toString()
        email = binding.edtEmail.text.trim().toString()
        password = binding.edtPassword.text.trim().toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this,NavActivity::class.java))
                                finish()
                            }
                        }
                } else {
                   Toast.makeText(baseContext,"Sign Up failed. Try again later",Toast.LENGTH_SHORT).show()
                }
            }

        var view: View? = null
        var flag = false

        when {
            FirstName.isEmpty() -> {
                binding.firstName.error = "Field is required"
                view = binding.firstName
                flag = true
            }

            LastName.isEmpty() -> {
                binding.lastName.error = "Field is required"
                view = binding.lastName
                flag = true
            }

            PhoneNumber.isEmpty() -> {
                binding.phoneNumber.error = "Field is required"
                view = binding.phoneNumber
                flag = true
            }

            email.isEmpty() -> {
                binding.edtEmail.error = "Field is required"
                view = binding.edtEmail
                flag = true
            }
            password.isEmpty() -> {
                binding.edtPassword.error = "Field is required"
                view = binding.edtPassword
                flag = true
            }
            password.length < 8 -> {
                binding.edtPassword.error = "Minimum 8 characters"
                view = binding.edtPassword
                flag = true
            }


        }

        return if (flag) {
            view?.requestFocus()
            false
        } else
            true

    }

}