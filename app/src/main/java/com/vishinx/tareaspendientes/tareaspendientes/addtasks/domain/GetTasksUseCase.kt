package com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain

import com.vishinx.tareaspendientes.tareaspendientes.addtasks.data.TaskRepository
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val taskRepository: TaskRepository){

    operator fun invoke(): Flow<List<TaskModel>>{
        return taskRepository.tasks
    }
}