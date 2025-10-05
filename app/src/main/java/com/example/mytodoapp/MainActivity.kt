package com.example.mytodoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.mytodoapp.ui.theme.MyToDoAppTheme
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class TaskBlockStatus(val id: Int, var isDone: Boolean, val taskContent: String)

@Composable
fun Screen(modifier: Modifier){
    val tasks = remember { mutableStateListOf<TaskBlockStatus>() }
    var nextId by remember { mutableStateOf(0) }
    val text = remember { mutableStateOf("") }

    val doneTasks = tasks.filter { it.isDone }
    val undoneTasks = tasks.filter { !it.isDone }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewTask(modifier = modifier, taskText = text, onClick = { tasks.add(TaskBlockStatus(nextId++, false, text.value.trim())) })
        Text(
            "To Do",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Column(Modifier.weight(1f)) {
            if(undoneTasks.isEmpty()){
                Text("Add items...")
            }else {
                undoneTasks.forEach { task ->
                    var isVisible by remember(task.id) { mutableStateOf(true) }
                    AnimatedVisibility(
                        visible = isVisible,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        TaskBlock(
                            modifier = modifier,
                            taskContent = task.taskContent,
                            done = task.isDone,
                            onTrans = {
                                isVisible = false
                            },
                            onDel = {
                                tasks.remove(task)
                            }
                        )
                    }
                    LaunchedEffect(isVisible) {
                        if(!isVisible) {
                            delay(200)
                            val idx = tasks.indexOf(task)
                            tasks[idx] = task.copy(isDone = true)
                        }
                    }
                }
            }
        }
        Text("Done", style = MaterialTheme.typography.headlineLarge)
        Column(Modifier.weight(1f)) {
            if(doneTasks.isEmpty()){
                Text("Complete To Do items!")
            }else {
                doneTasks.forEach { task ->
                    var isVisible by remember(task.id) { mutableStateOf(true) }
                    AnimatedVisibility(
                        visible = isVisible,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        TaskBlock(
                            modifier = modifier,
                            taskContent = task.taskContent,
                            done = task.isDone,
                            onTrans = {
                                isVisible = false
                            },
                            onDel = {
                                tasks.remove(task)
                            }
                        )
                    }
                    LaunchedEffect(isVisible) {
                        if(!isVisible) {
                            delay(200)
                            val idx = tasks.indexOf(task)
                            tasks[idx] = task.copy(isDone = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewTask(modifier: Modifier, taskText: MutableState<String>, onClick: () -> Unit){
    val context = LocalContext.current
    val addTask: () -> Unit = if(!taskText.value.isEmpty()){
        onClick
    }else{
        { Toast.makeText(context, "You need a task to add!", Toast.LENGTH_SHORT).show() }
    }
    Row(
        modifier = Modifier.padding(top = 50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = taskText.value,
            onValueChange = { taskText.value = it },
            label = { Text("Enter your task") },
            placeholder = { Text("Go on a run...") },
            singleLine = true
        )
        IconButton(onClick = addTask) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun TaskBlock(modifier: Modifier, taskContent: String, done: Boolean, onTrans: () -> Unit, onDel: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            if(!done) {
                OutlinedButton(
                    modifier = Modifier.size(40.dp),
                    onClick = onTrans,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                ) { }
            }else{
                OutlinedIconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = onTrans,
                    border = BorderStroke(1.dp, Color.Green)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.Green)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(taskContent)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDel) {
                Icon(Icons.Default.Close, contentDescription = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyToDoAppTheme {
        Screen(modifier = Modifier)
    }
}