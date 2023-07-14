package com.euromix.esupervisor.sources.tasks.list

import com.euromix.esupervisor.app.model.tasks.TasksSource
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitTasksSource @Inject constructor(
    private val config: RetrofitConfig
) : BaseRetrofitSource(config), TasksSource {

    private val tasksApi = retrofit.create(TasksApi::class.java)

    override suspend fun getTasks(request: TasksRequestEntity) =
        wrapRetrofitException {

            val jsonAdapter = config.moshi.adapter(TasksRequestEntity::class.java)

            tasksApi.getTasks(jsonAdapter.toJson(request)).map {
                it.toTask()
            }
        }

    override suspend fun createTasks(body: TasksCreateRequestEntity) =
        wrapRetrofitException {
            tasksApi.createTasks(body)
        }


//    override suspend fun getOutlets(idPartner: String)=
//        wrapRetrofitException {
//            tasksApi.getOutlets(idPartner).map { it.toOutlet() }
//        }

//    override suspend fun getTaskTypes(): List<ServerPair> =
//        wrapRetrofitException { tasksApi.getTaskTypes() }

}