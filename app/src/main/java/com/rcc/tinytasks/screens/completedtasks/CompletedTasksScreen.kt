package com.rcc.tinytasks.screens.completedtasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.navigation.MyBottomBar
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Primary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@Composable
fun CompletedTasksScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomBar(navController) },
        containerColor = Color.Black
    ) { innerPadding ->
        CompletedTasks(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun CompletedTasks(modifier: Modifier) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    var completedTasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    fun loadCompletedTasks() {
        if (userId != null) {
            db.collection("tasks")
                .whereEqualTo("user_Id", userId)
                .whereEqualTo("completed", true) // Filtrar tareas completadas
                .get()
                .addOnSuccessListener { result ->
                    completedTasks = result.documents.mapNotNull { it.data?.plus("id" to it.id) }
                }
        }
    }

    LaunchedEffect(userId) {
        loadCompletedTasks()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Completed Tasks",
            color = Light_Gray,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (completedTasks.isEmpty()) {
            Text(
                text = "No completed tasks found.",
                color = Light_Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(completedTasks) { task ->
                    CompletedTaskItem(task = task, onActionCompleted = { loadCompletedTasks() })
                }
            }
        }
    }
}

@Composable
fun CompletedTaskItem(
    task: Map<String, Any>,
    onActionCompleted: () -> Unit
) {
    val date = task["date"] as? String ?: "No Date"
    val description = task["description"] as? String ?: "No Description"
    val taskId = task["id"] as? String

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task["title"] as? String ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Light_Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Date: $date",
                style = MaterialTheme.typography.bodySmall,
                color = Light_Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { taskId?.let { deleteTask(it, onActionCompleted) } }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Primary)
                }
            }
        }
    }
}

fun deleteTask(taskId: String, onDelete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("tasks").document(taskId)
        .delete()
        .addOnSuccessListener {
            onDelete()
        }
}
