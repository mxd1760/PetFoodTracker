package com.marcusdoucette.petfoodtracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.marcusdoucette.petfoodtracker.Data.DataManager
import com.marcusdoucette.petfoodtracker.DefaultView.DefaultView
import com.marcusdoucette.petfoodtracker.ui.theme.PetFoodTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity()  {
    companion object{
        val logTag = "PetFoodTrackerLog"
        val CHANNEL_ID = "PFT_notification_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        postTestNotification()

        setContent {
            PetFoodTrackerTheme {
                DefaultView()
            }
        }
    }

    override fun onStart(){
        super.onStart()
        val context = this
        lifecycleScope.launch(Dispatchers.IO){
            DataManager.LoadData(context)
        }
    }

    override fun onStop(){
        super.onStop()
        val context = this
        lifecycleScope.launch(Dispatchers.IO){
            DataManager.SaveData(context)
        }

    }

    fun postTestNotification(){
        createNotificationChannel()
        val intent = Intent(this,MainActivity::class.java)
//            .apply{
//                flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE)



        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Test")
            .setContentText("Testing Testing 1 2 3...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

//        with(NotificationManagerCompat.from(this)){
//            if(ActivityCompat.checkSelfPermission(
//                    this@MainActivity,
//                    Manifest.permission.POST_NOTIFICATIONS
//                )!= PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this@MainActivity,arrayOf("POST_NOTIFICATIONS"),0)
//
//                return@with
//            }
//            notify(0,builder.build())
//        }
        registerForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ){}
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            ==PackageManager.PERMISSION_GRANTED
            ){
            NotificationManagerCompat.from(this).notify(0,builder.build())
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.notification_channel_name)
            val descriptionText = "Channel for Pet Food Tracker App notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply{
                description=descriptionText
            }



            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}
