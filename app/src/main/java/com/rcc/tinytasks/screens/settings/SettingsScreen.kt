package com.rcc.tinytasks.screens.settings

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.R
import com.rcc.tinytasks.navigation.MyBottomBar
import com.rcc.tinytasks.navigation.Screens
import com.rcc.tinytasks.ui.theme.Dark_Gray
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Primary
import com.rcc.tinytasks.ui.theme.Secondary

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomBar(navController) },
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.Black
    ) { innerpadding ->
        Settings(modifier = Modifier.padding(innerpadding), navController)
    }
}

@Composable
fun Settings(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Settings",
            color = Light_Gray,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Surface(
            modifier = Modifier
                .padding(15.dp)
                .size(150.dp),
            shape = CircleShape,
            color = Color.Black,
            border = BorderStroke(width = 2.dp, color = Secondary)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_bg),
                contentDescription = "Logo de la App"
            )
        }

        Spacer(modifier = Modifier.padding(30.dp))

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        val email = auth.currentUser?.email
        var displayName by remember { mutableStateOf<String?>(null) }

        // Recuperar datos del usuario desde Firestore
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("user_Id", userId) // Filtrar por user_Id
                .get()
                .addOnSuccessListener { documents ->
                    val userDocument = documents.documents.firstOrNull()
                    if (userDocument != null) {
                        displayName = userDocument.getString("display_name") // Obtener display_name
                    } else {
                        displayName = "No display name found" // No se encontró el documento
                    }
                }
                .addOnFailureListener {
                    displayName = "Error loading display name" // Manejo de errores
                }
        }

        Column(
            modifier = Modifier
                .background(color = Dark_Gray, shape = RoundedCornerShape(15.dp))
                .padding(15.dp)
        ) {
            Text(
                text = "User Data",
                color = Primary,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "User_Id (Vista Temporal): ", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
            Text(text = "${userId}", color = Light_Gray, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Email:", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
            Text(text = "${email}", color = Light_Gray, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.padding(8.dp))


            Text(text = "Username", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
            Text(text = "${displayName ?: "Loading..."}", color = Light_Gray, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.padding(30.dp))

        // Visualizar Tareas completadas
        Button(
            onClick = {
                navController.navigate(Screens.CompletedTasks.name)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Secondary,
                contentColor = Primary,
                disabledContainerColor = Color(0xFF101010),
                disabledContentColor = Color.Gray,
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Completed Tasks",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.padding(15.dp))

        // Log out
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()

                navController.navigate(Screens.LoginScreen.name) {
                    popUpTo(Screens.LoginScreen.name) {
                        inclusive = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Secondary,
                contentColor = Primary,
                disabledContainerColor = Color(0xFF101010),
                disabledContentColor = Color.Gray,
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Log out",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}