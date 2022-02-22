package com.example.sinicxpress.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sinicxpress.activities.ui.home.HomeFragment
import com.example.sinicxpress.activities.utility.LoadingDialog
import com.example.sinicxpress.activities.utility.State
import com.example.sinicxpress.activities.viewModels.LoginViewModel
import com.example.sinicxpress.databinding.ActivityLoginBinding
import com.firebase.ui.auth.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.InternalCoroutinesApi

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var email: String
    private lateinit var password: String
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var database:FirebaseDatabase
    private lateinit var riderInfoRef:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        auth = Firebase.auth

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.txtForgetPassword.setOnClickListener {

//            startActivity(Intent(this, ForgetActivity::class.java))
        }


        binding.btnLogin.setOnClickListener {
            if (areFieldReady()) {
                lifecycleScope.launchWhenStarted {
                    loginViewModel.login(email, password).collect {
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

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()

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
            }
        }

        database = FirebaseDatabase.getInstance()
        riderInfoRef = database.getReference(Common.RIDER_INFO_REFERENCE)


    }

    private fun areFieldReady(): Boolean {

        email = binding.edtEmail.text.trim().toString()
        password = binding.edtPassword.text.trim().toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }


        var view: View? = null
        var flag = false

        when {

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

    private fun updateUI(user: FirebaseUser?) {

        if(user != null){
            if(user.isEmailVerified) {
                startActivity(Intent(this, NavActivity::class.java))
                finish()
            }else{
                Toast.makeText(baseContext, "Please verify your email address",
                    Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(baseContext, "Login failed.",
                Toast.LENGTH_SHORT).show()
        }

        riderInfoRef
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.exists())
                   {
                       Toast.makeText(this@LoginActivity,"User already register!",Toast.LENGTH_SHORT).show()
                   }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity,error.message,Toast.LENGTH_SHORT).show()
                }

            })

    }



//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            reload();
//        }
//    }
//
//    private fun reload() {
//        reload()
//    }
}