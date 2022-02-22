package com.example.sinicxpress.activities

import android.app.Activity
import android.content.Intent
import android.content.SyncRequest
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import io.reactivex.Completable
import com.firebase.ui.auth.AuthUI
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.sinicxpress.R
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    companion object{
        private val LOGIN_REQUEST_CODE = 7171
    }

    lateinit var providers:List<AuthUI.IdpConfig>
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var listener: FirebaseAuth.AuthStateListener



    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    override fun onStop() {
        if (firebaseAuth != null && listener != null) firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun delaySplashScreen() {
        Completable.timer(3,TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe({
                firebaseAuth.addAuthStateListener(listener)
            })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        init()




                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

//            } else {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//        }, 3000)


//    private fun init(){
//        providers = Arrays.asList(
//        AuthUI.IdpConfig.GoogleBuilder().build(),
//        )
//
//        firebaseAuth = FirebaseAuth.getInstance()
//        listener = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
//            val user = myFirebaseAuth.currentUser
//            if (user != null)
//                Toast.makeText(this@SplashActivity, "Welcome:"+user.uid,Toast.LENGTH_SHORT).show()
//            else
//                showLoginLayout()
//        }
//
//    }
//
//    private fun showLoginLayout() {
//       val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.activity_login)
////           .setPhoneButtonId(R.id.bt)
//           .setGoogleButtonId(R.id.btnGoogle)
//           .build()
//
//        startActivityForResult(
//            AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAuthMethodPickerLayout(authMethodPickerLayout)
//                .setAvailableProviders(providers)
//                .setIsSmartLockEnabled(false)
//                .build()
//            ,LOGIN_REQUEST_CODE
//        )
//    }
//
//    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?){
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == LOGIN_REQUEST_CODE){
//            val response = IdpResponse.fromResultIntent(data)
//            if (requestCode == Activity.RESULT_OK){
//                val user = FirebaseAuth.getInstance().currentUser
//            }
//            else
//                Toast.makeText(this@SplashActivity,""+response!!.error!!.message,Toast.LENGTH_SHORT).show()
//        }
//
//
//    }


}