package com.rcc.tinytasks.screens.addtask

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.navigation.MyBottomBar
import com.rcc.tinytasks.navigation.Screens
import com.rcc.tinytasks.ui.theme.Dark_Gray
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Primary
import com.rcc.tinytasks.ui.theme.Secondary
import java.util.*

@Composable
fun AddTaskScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomBar(navController) },
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.Black
    ) { innerPadding ->
        AddTask(modifier = Modifier.padding(innerPadding), navController = navController)
    }
}

@Composable
fun AddTask(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Add Task",
            color = Light_Gray,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(text = "Titulo", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
        val titulo = rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = titulo.value,
            onValueChange = { titulo.value = it },
            label = { Text("Titulo") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.2f))
        )

        Text(text = "Fecha", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())

        val selectedDate = rememberSaveable { mutableStateOf("") }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate.value = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            year, month, day
        )

        val isNotificarEnabled = rememberSaveable { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Dark_Gray.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                Text(
                    text = if (selectedDate.value.trim().isEmpty()) "Seleccione una fecha" else "Fecha: ${selectedDate.value}",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 15.sp
                )
            }

            Text(text = "Notificar: ", color = Light_Gray)

            Switch(
                checked = isNotificarEnabled.value,
                onCheckedChange = { isNotificarEnabled.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Primary,
                    uncheckedThumbColor = Light_Gray,
                    checkedTrackColor = Secondary,
                    uncheckedTrackColor = Dark_Gray
                )
            )
        }

        var selectedOption = rememberSaveable { mutableStateOf("Diariamente") }
        if (isNotificarEnabled.value) {
            RadioButtonGroup(selectedOption)
        }


        // Descripción
        Text(text = "Descripción", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
        val descripcion = rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = descripcion.value,
            onValueChange = { descripcion.value = it },
            label = { Text("Descripción") },
            singleLine = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.2f))
        )


        var priorityValue = rememberSaveable { mutableFloatStateOf(0.5f) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Prioridad", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())
            Slider(
                value = priorityValue.value,
                onValueChange = { priorityValue.value = it },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Primary,
                    activeTrackColor = Primary,
                    inactiveTrackColor = Dark_Gray
                )
            )
        }

        Button(
            onClick = {
                val auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    val task = mapOf(
                        "title" to titulo.value,
                        "description" to descripcion.value,
                        "date" to selectedDate.value,
                        "priority" to priorityValue.value,
                        "notify" to isNotificarEnabled.value,
                        "notification_frequency" to if (isNotificarEnabled.value) selectedOption.value else null,
                        "user_Id" to userId,
                        "completed" to false
                    )

                    addTaskToFirebase(
                        task = task,
                        onSuccess = {
                            navController.navigate(Screens.HomeScreen.name)
                        },
                        onFailure = { exception ->
                            Log.e("AddTask", "Failed to add task", exception)
                        }
                    )
                } else {
                    Log.e("AddTask", "User not authenticated")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Secondary,
                contentColor = Primary,
                disabledContainerColor = Color(0xFF101010),
                disabledContentColor = Color.Gray,
            ),
            enabled = true,
        ) {
            Text(
                text = "Add Task",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun RadioButtonGroup(selectedOption: MutableState<String>) {

    val options = listOf("Diariamente", "Semanalmente", "Un dia antes")

    Column {
        Text(text = "Como desea recibir la notificación:", color = Primary, textAlign = TextAlign.Left, modifier = Modifier.fillMaxWidth())

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (selectedOption.value == option),
                    onClick = { selectedOption.value = option },
                    colors = RadioButtonColors(
                        selectedColor = Primary,
                        unselectedColor = Dark_Gray,
                        disabledSelectedColor = Primary,
                        disabledUnselectedColor = Dark_Gray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, color = Light_Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun addTaskToFirebase(
    task: Map<String, Any?>,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userTasksRef = db.collection("tasks")

    userTasksRef.add(task)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}
