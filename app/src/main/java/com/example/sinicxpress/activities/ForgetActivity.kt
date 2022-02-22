package com.example.sinicxpress.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sinicxpress.activities.utility.LoadingDialog
import com.example.sinicxpress.activities.utility.State
import com.example.sinicxpress.activities.viewModels.LoginViewModel
import com.example.sinicxpress.databinding.ActivityForgetBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@InternalCoroutinesApi
class ForgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetBinding
    private lateinit var loadingDialog: LoadingDialog
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnForgetPassword.setOnClickListener {
            val email = binding.edtFEmail.text.trim().toString()

            if (email.isEmpty()) {
                binding.edtFEmail.error = "Field is required"
                binding.edtFEmail.requestFocus()
            } else {
                lifecycleScope.launchWhenStarted {
                    loginViewModel.forget(email).collect{
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
            }
        }
    }
}

fun <T> Flow<T>.collect(collector: () -> Unit) {

}
