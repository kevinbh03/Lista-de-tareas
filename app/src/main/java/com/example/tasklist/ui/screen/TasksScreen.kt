package com.example.tasklist.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tasklist.domain.TaskModel
import com.example.tasklist.domain.TasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun TasksScreen(tasksViewModel: TasksViewModel) {

    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)

    Box(modifier = Modifier.fillMaxSize()) {
        AddTasksDialog(
            showDialog,
            onDismiss = { tasksViewModel.onDialogClose() },
            onTaskAdded = { task, name, lastname, mail, date ->
                tasksViewModel.onTasksCreated(task, name, lastname, mail, date)
            }
        )
        FabDialog(Modifier.align(Alignment.BottomEnd), tasksViewModel)
        TasksList(tasksViewModel)
    }

}

@Composable
fun TasksList(tasksViewModel: TasksViewModel) {
    val myTasks: List<TaskModel> = tasksViewModel.task

    LazyColumn {
        items(myTasks, key = { it.id }) { task ->
            ItemTask(task, tasksViewModel)
        }
    }
}

@Composable
fun ItemTask(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    tasksViewModel.onItemRemove(taskModel)
                })
            },
        elevation = 8.dp
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            ) {
                Row() {
                    CustomText(texts = "pedido: ")
                    CustomText(texts = taskModel.task)
                }
                Row() {
                    CustomText(texts = "Fecha de registro: ")
                    CustomText(texts = taskModel.id.toString())
                }
                Row() {
                    Text(text = "Nombre: ", fontWeight = FontWeight.Bold)
                    Text(text = taskModel.name)
                }
                Row() {
                    Text(text = "Apellido: ", fontWeight = FontWeight.Bold)
                    Text(text = taskModel.lastname)
                }
                Row() {
                    Text(text = "Correo: ", fontWeight = FontWeight.Bold)
                    Text(text = taskModel.mail)
                }
                Row() {
                    Text(text = "Descripcion: ", fontWeight = FontWeight.Bold)
                    Text(text = taskModel.date)
                }
            }
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { tasksViewModel.onCheckBoxSelected(taskModel) })
        }

    }
}

@Composable
fun FabDialog(modifier: Modifier, tasksViewModel: TasksViewModel) {
    FloatingActionButton(onClick = {
        tasksViewModel.onShowDialogClick()
    }, modifier = modifier) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }
}

@Composable
fun AddTasksDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onTaskAdded: (task: String, name: String, lastname: String, mail: String, date: String) -> Unit
) {
    val Context = LocalContext.current.applicationContext
    var myTask by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {

            Card(elevation = 8.dp) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pedido Nuevo",
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    TextField(
                        value = myTask,
                        onValueChange = { myTask = it },
                        label = { Text(text = "Pedido") },
                        singleLine = true,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        label = { Text(text = "Nombre") },
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        singleLine = true,
                        label = { Text(text = "Apellido") },
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextField(
                        value = mail,
                        onValueChange = { mail = it },
                        singleLine = true,
                        label = { Text(text = "Correo Electronico") },
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextField(
                        value = date,
                        onValueChange = { date = it },
                        singleLine = true,
                        label = { Text(text = "Descriccion del pedido") },
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = {
                            if (validateMaxCharacterImput(myTask) &&
                                validateMaxCharacterImput(name) &&
                                validateMaxCharacterImput(lastname) &&
                                validateMail(mail) &&
                                validateMaxCharacterImput(date)
                            ) {
                                Toast.makeText(Context, "Tarea creada", Toast.LENGTH_LONG).show()
                                onTaskAdded(myTask, name, lastname, mail, date)
                                myTask = ""
                                name = ""
                                lastname = ""
                                mail = ""
                                date = ""
                            } else {
                                Toast.makeText(Context, "Datos invalidos", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Añadir Pedido")
                    }
                }
            }
        }
    }
}

private fun validateMaxCharacter(max: Int, value: Int): Boolean {
    return value <= max
}

private fun validateMaxCharacterImput(textImput: String): Boolean {
    return validateMaxCharacter(20, textImput.length) && textImput.isNotEmpty()
}

private fun validateMail(mail: String): Boolean {
    return mail.contains("@")
}

private fun fullName(name: String, lastname: String): String {
    return "$name $lastname"
}

fun isEven(number: Int): Boolean {
    return number % 2 == 0
}

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun getGreetingMessage(hourOfDay: Int): String {
    return when {
        hourOfDay < 12 -> "¡Buenos días!"
        hourOfDay < 18 -> "¡Buenas tardes!"
        else -> "¡Buenas noches!"
    }
}

fun countVowels(input: String): Int {
    val vowels = "AEIOUaeiou"
    return input.count { it in vowels }
}

fun stringLength(input: String): Int {
    return input.length
}

fun toUpperCase(input: String): String {
    return input.toUpperCase()
}

fun firstChar(input: String): Char {
    return input.first()
}


@Composable
private fun CustomText(texts: String) {
    Text(
        text = texts,
        fontWeight = FontWeight.Bold
    )
}



