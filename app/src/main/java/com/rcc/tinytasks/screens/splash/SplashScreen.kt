package com.rcc.tinytasks.screens.splash

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.rcc.tinytasks.R
import com.rcc.tinytasks.navigation.Screens
import com.rcc.tinytasks.ui.theme.Secondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val alpha = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        // Animar la opacidad
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000) // Duración de la animación
        )

        delay(2000)

        if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            navController.navigate(Screens.LoginScreen.name)
        } else {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(Screens.SplashScreen.name) {
                    inclusive = true
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .padding(15.dp)
            .size(250.dp)
            .alpha(alpha.value), // Aplicar la animación de opacidad
        shape = CircleShape,
        color = Color.Black,
        border = BorderStroke(width = 2.dp, color = Secondary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bg),
            contentDescription = "Logo de la App"
        )
    }
}
