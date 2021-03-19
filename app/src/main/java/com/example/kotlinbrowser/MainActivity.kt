package com.example.kotlinbrowser

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var mAdminComponentName: ComponentName
    private lateinit var mDevicePolicyManager: DevicePolicyManager
//    companion object {
//        const val LOCK_ACTIVITY_KEY = "browser.kiosk.MainActivity"
//    }

    override fun onBackPressed() {
        try {
            Toast.makeText(this, "TEST", Toast.LENGTH_LONG)
        } catch (e: Error)
        {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdminComponentName = AdminReceiver.getComponentName(this)
        mDevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        mDevicePolicyManager.removeActiveAdmin(mAdminComponentName)

        val www: WebView = findViewById(R.id.web)
        www.webViewClient = WebViewClient()

        val url:String = applicationContext.resources.getString(R.string.url)
        www.loadUrl(url)
        www.settings.javaScriptEnabled = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            www.settings.safeBrowsingEnabled = true
        }

        val isAdmin = isAdmin()
        setImmersiveMode(true)

        var isLocked: Boolean = true

        val btnLock: Button = findViewById(R.id.out_button)
//        btnLock.text = "LOCK"
        setKioskPolicies(true, isAdmin)

        btnLock.setOnLongClickListener(){
            if(!isLocked){
                isLocked = true
                setKioskPolicies(true, isAdmin)
//                btnLock.text = "UNLOCK"
            } else {
                isLocked = false
                setKioskPolicies(false, isAdmin)
                setImmersiveMode(true)
//                btnLock.text = "LOCK"
            }
            true
        }

//        btnLock.setOnClickListener {
//            if(!isLocked){
//                isLocked = true
//                setKioskPolicies(true, isAdmin)
//                btnLock.text = "UNLOCK"
//            } else {
//                isLocked = false
//                setKioskPolicies(false, isAdmin)
//                btnLock.text = "LOCK"
//            }
//        }
    }

    private fun isAdmin() = mDevicePolicyManager.isDeviceOwnerApp(packageName)

    private fun setKioskPolicies(enable: Boolean, isAdmin: Boolean) {
        setLockTask(enable, isAdmin)
        setImmersiveMode(enable)
    }

    private fun setLockTask(start: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            mDevicePolicyManager.setLockTaskPackages(
                    mAdminComponentName, if (start) arrayOf(packageName) else arrayOf())
        }
        if (start) {
            startLockTask()
        } else {
            stopLockTask()
        }
    }

    private fun setImmersiveMode(enable: Boolean) {
        if (enable) {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.decorView.systemUiVisibility = flags
        } else {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.decorView.systemUiVisibility = flags
        }
    }
}