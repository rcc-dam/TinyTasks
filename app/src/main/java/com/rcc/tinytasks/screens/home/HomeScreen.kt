package com.rcc.tinytasks.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.navigation.MyBottomBar
import com.rcc.tinytasks.navigation.Screens
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Primary

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomBar(navController) },
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.Black
    ) { innerpadding ->
        Home(modifier = Modifier.padding(innerpadding), navController = navController)
    }
}

@Composable
fun Home(modifier: Modifier, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    fun loadTasks() {
        if (userId != null) {
            db.collection("tasks")
                .whereEqualTo("user_Id", userId)
                .whereEqualTo("completed", false) // Filtrar solo tareas no completadas
                .get()
                .addOnSuccessListener { result ->
                    tasks = result.documents.mapNotNull { it.data?.plus("id" to it.id) }
                }
        }
    }

    LaunchedEffect(userId) {
        loadTasks()
    }

    val sortedTasks = tasks.sortedByDescending {
        (it["priority"] as? Number)?.toFloat() ?: 0f
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "My Tasks",
            color = Light_Gray,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (sortedTasks.isEmpty()) {
            Text(
                text = "No tasks found.",
                color = Light_Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedTasks) { task ->
                    TaskItem(task = task, navController = navController, onActionCompleted = { loadTasks() })
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Map<String, Any>,
    navController: NavController,
    onActionCompleted: () -> Unit
) {
    val priority = (task["priority"] as? Number)?.toFloat() ?: 0f
    val date = task["date"] as? String ?: "No Date"
    val description = task["description"] as? String ?: "No Description"
    val notify = task["notify"] as? Boolean ?: false
    val notificationFrequency = task["notification_frequency"] as? String ?: "No Notification"
    val taskId = task["id"] as? String
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded },
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

            if (notify) {
                Text(
                    text = "Notification: $notificationFrequency",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Priority: ${(priority * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Light_Gray
                )
                androidx.compose.material3.LinearProgressIndicator(
                    progress = priority / 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Primary,
                    trackColor = Color.Gray
                )
            }

            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { taskId?.let { completeTask(it, onActionCompleted) } }) {
                        Icon(Icons.Default.Check, contentDescription = "Complete", tint = Primary)
                    }
                    IconButton(onClick = { taskId?.let { editTask(it, navController) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Primary)
                    }
                    IconButton(onClick = { taskId?.let { deleteTask(it, onActionCompleted) } }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Primary)
                    }
                }
            }
        }
    }
}

fun completeTask(taskId: String, onActionCompleted: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("tasks").document(taskId)
        .update("completed", true)
        .addOnSuccessListener {
            onActionCompleted()
        }
}

fun editTask(taskId: String, navController: NavController) {
    navController.navigate("${Screens.EditTaskScreen.name}/$taskId")
}

fun deleteTask(taskId: String, onDelete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("tasks").document(taskId)
        .delete()
        .addOnSuccessListener {
            onDelete()
        }
}
