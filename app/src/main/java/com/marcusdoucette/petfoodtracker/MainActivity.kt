package com.marcusdoucette.petfoodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.marcusdoucette.petfoodtracker.Data.DataManager
import com.marcusdoucette.petfoodtracker.DefaultView.DefaultView
import com.marcusdoucette.petfoodtracker.ui.theme.PetFoodTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object{
        val logTag = "PetFoodTrackerLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}
