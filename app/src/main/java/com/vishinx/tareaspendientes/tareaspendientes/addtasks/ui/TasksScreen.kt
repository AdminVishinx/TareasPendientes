package com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui.model.TaskModel
import com.vishinx.tareaspendientes.ui.theme.ExtraColor
import com.vishinx.tareaspendientes.ui.theme.PrimaryColor
import com.vishinx.tareaspendientes.ui.theme.PrimaryColorLight

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel) {

    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<TasksUIState>(
        initialValue = TasksUIState.Loading,
        key1 = lifecycle,
        key2 = tasksViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            tasksViewModel.uiState.collect { value = it }
        }
    }

    when (uiState) {
        is TasksUIState.Error -> {}
        TasksUIState.Loading -> {
            CircularProgressIndicator()
        }
        is TasksUIState.Success -> {
            Column {
                TestTopAppBar()
                Box(modifier = Modifier.fillMaxSize()) {
                    AddTasksDialog(
                        showDialog,
                        onDismiss = { tasksViewModel.onDialogClose() },
                        onTaskAdded = { tasksViewModel.onTaskCreated(it) })
                    FabDialog(Modifier.align(Alignment.BottomEnd), tasksViewModel)
                    TasksList((uiState as TasksUIState.Success).tasks, tasksViewModel)
                }
            }
        }
    }

}

@Composable
fun TestTopAppBar() {
    TopAppBar(
        title = { Text(text = "Tareas Pendientes") },
        backgroundColor = PrimaryColorLight,
        contentColor = Color.White, elevation = 4.dp, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu Icon")
            }
        },
    actions = {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = "Share Icon")
        }
    })
}

@Composable
fun TasksList(tasks: List<TaskModel>, tasksViewModel: TasksViewModel) {

    LazyColumn {
        //Con el key asignado al id optimizamos el rendimiento del RecyclerView
        items(tasks, key = { it.id }) { task ->
            ItemTask(task, tasksViewModel)
        }
    }
}

@Composable
fun ItemTask(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    tasksViewModel.onItemRemove(taskModel)
                })
            },
        border = BorderStroke(1.dp, Color.LightGray), elevation = 8.dp
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = taskModel.task, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            )
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { tasksViewModel.onCheckBoxSelected(taskModel) })
        }
    }
}

@Composable
fun FabDialog(modifier: Modifier, tasksViewModel: TasksViewModel) {
    FloatingActionButton(contentColor = Color.White, onClick = {
        tasksViewModel.onShowDialogClick()
    }, modifier = modifier.padding(16.dp)) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }
}

@Composable
fun AddTasksDialog(show: Boolean, onDismiss: () -> Unit, onTaskAdded: (String) -> Unit) {
    var myTask by remember {
        mutableStateOf("")
    }
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = 8.dp
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Añadir tarea",
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    OutlinedTextField(
                        value = myTask,
                        onValueChange = { myTask = it },
                        label = { Text(text = "Tarea")},
                        singleLine = true,
                        maxLines = 1,
                        placeholder = { Text(text = "Tarea") },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            backgroundColor = MaterialTheme.colors.background,
                            focusedIndicatorColor = ExtraColor,
                            unfocusedIndicatorColor = PrimaryColorLight
                        )
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = {
                        onTaskAdded(myTask)
                        myTask = ""
                    }, shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Añadir")
                    }
                }
            }
        }
    }
}