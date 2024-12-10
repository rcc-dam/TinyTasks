package com.rcc.tinytasks.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Dark_Gray
import com.rcc.tinytasks.ui.theme.Primary


//Al final no la voy a usar pero la dejo por si acaso
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    TopAppBar(
        title = { },
        colors = topAppBarColors(
            containerColor = Light_Gray,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        modifier = Modifier.height(56.dp)
    )
}

@Composable
fun MyBottomBar(navController: NavController) {
    val currentDestination = navController.currentDestination?.route

    NavigationBar(
        containerColor = Dark_Gray,
        contentColor = Dark_Gray
    ) {

        // Botón de Home
        NavigationBarItem(
            selected = currentDestination == Screens.HomeScreen.name,
            onClick = {
                navController.navigate(Screens.HomeScreen.name)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(text = "Home", color = Primary, fontSize = 15.sp)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                unselectedIconColor = Dark_Gray,
                selectedTextColor = Primary,
                unselectedTextColor = Dark_Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Botón de Add Task
        NavigationBarItem(
            selected = currentDestination == Screens.AddTaskScreen.name,
            onClick = {
                navController.navigate(Screens.AddTaskScreen.name)
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(text = "Add Task", color = Primary, fontSize = 15.sp)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                unselectedIconColor = Dark_Gray,
                selectedTextColor = Primary,
                unselectedTextColor = Dark_Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Botón de Settings
        NavigationBarItem(
            selected = currentDestination == Screens.SettingsScreen.name,
            onClick = {
                navController.navigate(Screens.SettingsScreen.name)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            label = {
                Text(text = "Settings", color = Primary, fontSize = 15.sp)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                unselectedIconColor = Dark_Gray,
                selectedTextColor = Primary,
                unselectedTextColor = Dark_Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}