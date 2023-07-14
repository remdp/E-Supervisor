package com.euromix.esupervisor.sources.tasks.taskDetail

import com.euromix.esupervisor.app.model.taskDetail.TaskDetailSource
import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitTaskDetailSource @Inject constructor(config: RetrofitConfig) :
    BaseRetrofitSource(config),
    TaskDetailSource {

    private val taskDetailApi = retrofit.create(TaskDetailApi::class.java)

    override suspend fun getTask(id: String): TaskDetail {
        return wrapRetrofitException {
            taskDetailApi.getTaskDetail(id).toTaskDetail()
        }
    }
}