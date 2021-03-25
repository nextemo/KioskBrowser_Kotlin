package com.example.kotlinbrowser

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import kotlin.system.exitProcess


//Todo
//    Scaling the Web
//    read external Folder
//    back button function
//    ADB CL
//    to list devices connected adb devices
//    to select device adb -s name_of_the_device shell
//    set device owner in ADB
//    adb shell dpm set-device-owner com.example.kotlinbrowser/.AdminReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var mAdminComponentName: ComponentName
    private lateinit var mDevicePolicyManager: DevicePolicyManager

    lateinit var  www: WebView

    override fun onBackPressed() {
        if(www.canGoBack()){
            www.goBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdminComponentName = AdminReceiver.getComponentName(this)
        mDevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        www = findViewById(R.id.web)
        www.webViewClient = WebViewClient()
        var externalUrl:String = ""

        try {
            val externalForlder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val fl:File = File(externalForlder, "url.xml")
            externalUrl = fl.readText().trim()
        } catch (er: Exception) {
            println(er.message.toString())
            onAlertDialog(this, "URL can't be loaded. \nError: ${er.message}", "Try again!")
        }

//        val url:String = applicationContext.resources.getString(R.string.url)
        www.loadUrl(externalUrl)
        www.settings.javaScriptEnabled =  true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            www.settings.safeBrowsingEnabled = true
        }

        val isAdmin = isAdmin()
        setKioskPolicies(true, isAdmin)
        var isLocked: Boolean = true

        val lock:FloatingActionButton = findViewById(R.id.fab)
        lock.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e63946"))
        lock.setOnLongClickListener(){
            if(!isLocked){
                lock.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e63946"))
                isLocked = true
                setKioskPolicies(true, isAdmin)
                Toast.makeText(this, "LOCKED", Toast.LENGTH_SHORT)
            } else {
                lock.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2a9d8f"))
                isLocked = false
                stopLockTask()
                Toast.makeText(this, "UNLOCKED", Toast.LENGTH_SHORT)
            }
            true
        }
//Remove the device admin for this app
//        lock.setOnClickListener(){
//            mDevicePolicyManager.clearDeviceOwnerApp(packageName)
//            true
//        }
    }

    fun onAlertDialog(view: MainActivity, message:String, toastMsg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!")
        builder.setMessage(message)
        builder.setPositiveButton("OK"){
            _, _ ->

            true
//            _, _ -> Toast.makeText(this, toastMsg,Toast.LENGTH_SHORT).show()
        }
        builder.show()
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
        }
//        else if(isLocked) {
//            stopLockTask()
//        }
    }



    private fun setImmersiveMode(enable: Boolean) {
        if (enable) {
            val flags = (
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            View.STATUS_BAR_VISIBLE
//                    and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    View.SYSTEM_UI_FLAG_FULLSCREEN
//                    and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
                    window.decorView.systemUiVisibility = flags
        }
//        else {
//            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//                    window.decorView.systemUiVisibility = flags
//        }
    }

}