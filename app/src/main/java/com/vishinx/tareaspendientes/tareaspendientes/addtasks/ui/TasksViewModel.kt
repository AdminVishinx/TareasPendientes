package com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain.AddTaskUseCase
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain.DeleteTaskUseCase
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain.GetTasksUseCase
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain.UpdateTaskUseCase
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui.TasksUIState.Success
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,

    getTasksUseCase: GetTasksUseCase
):ViewModel() {

    val uiState:StateFlow<TasksUIState> = getTasksUseCase().map (::Success)
        .catch { TasksUIState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUIState.Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog:LiveData<Boolean> = _showDialog

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: String) {
        _showDialog.value = false
        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = task))
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }
    }

    fun onItemRemove(taskModel: TaskModel) {
        viewModelScope.launch{
            deleteTaskUseCase(taskModel)
        }
    }
}