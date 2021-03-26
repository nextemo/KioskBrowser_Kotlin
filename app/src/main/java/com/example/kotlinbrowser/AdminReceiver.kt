package com.example.kotlinbrowser

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import android.widget.Toast
import androidx.annotation.RequiresApi

class AdminReceiver : DeviceAdminReceiver() {
    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, AdminReceiver::class.java)
        }
        private val TAG = AdminReceiver::class.java.simpleName
    }

//    @RequiresApi(Build.VERSION_CODES.P)
    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
    val dpm = getManager(context)
    val admin = getWho(context)
    //min targeted SDK has to be 28 to be able to apply LOCK_TASK_FEATURE_SYSTEM_INFO
    try {
        dpm.setLockTaskFeatures(
            admin,
        DevicePolicyManager.LOCK_TASK_FEATURE_NONE
//              DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW
        )
    }catch (error: Exception){
        print(error.message.toString())
    }
        super.onLockTaskModeEntering(context, intent, pkg)
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
    }
}