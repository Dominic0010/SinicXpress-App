package com.example.sinicxpress.activities.utility

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater

import com.example.sinicxpress.databinding.DialogLayoutBinding
import com.example.sinicxpress.R

class LoadingDialog(private val activity: Activity) {

    private var alertDialog: AlertDialog? = null

    fun startLoading() {
        val builder = AlertDialog.Builder(activity, R.style.loadingDialogStyle)
        val binding = DialogLayoutBinding.inflate(LayoutInflater.from(activity), null, false)

        builder.setView(binding.root)
        builder.setCancelable(false)
        alertDialog = builder.create()
        binding.rotateLoading.start()
        alertDialog?.show()
    }

    fun stopLoading() {
        alertDialog?.dismiss()
    }
}