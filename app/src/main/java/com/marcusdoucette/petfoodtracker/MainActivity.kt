package com.marcusdoucette.petfoodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.marcusdoucette.petfoodtracker.Data.DataManager
import com.marcusdoucette.petfoodtracker.DefaultView.DefaultView
import com.marcusdoucette.petfoodtracker.ui.theme.PetFoodTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
