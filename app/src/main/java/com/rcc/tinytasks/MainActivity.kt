package com.rcc.tinytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.navigation.Navigation
import com.rcc.tinytasks.ui.theme.TinyTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Para que la barra de notificaciones sea de color gris
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_gray)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {

            TinyTasksTheme {
                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> }
                // A surface container using the 'background' color from the theme
                // OJO !!!! HABILITAR INTERNET EN EL MANIFEST !!!!!!!!!!!!!!!

                FirebaseFirestore.setLoggingEnabled(true)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }
}

