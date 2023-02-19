package com.vishinx.tareaspendientes.tareaspendientes.addtasks.domain

import com.vishinx.tareaspendientes.tareaspendientes.addtasks.data.TaskRepository
import com.vishinx.tareaspendientes.tareaspendientes.addtasks.ui.model.TaskModel
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(taskModel: TaskModel){
        taskRepository.add(taskModel)
    }
}