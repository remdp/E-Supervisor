package com.euromix.esupervisor.sources.visitsSupervisors

import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsSource
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckIn
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckOut
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorsRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitVisitsSupervisorsSource @Inject constructor(private val config: RetrofitConfig) :
    BaseRetrofitSource(config), VisitsSupervisorsSource {

    private val visitsSupervisorsApi = retrofit.create(VisitsSupervisorsApi::class.java)

    override suspend fun getVisitsSupervisors(request: VisitsSupervisorsRequestEntity?): List<VisitSupervisor> {
        return wrapRetrofitException {

            val jsonAdapter = config.moshi.adapter(VisitsSupervisorsRequestEntity::class.java)

            visitsSupervisorsApi.getVisitsSupervisor(jsonAdapter.toJson(request)).map {
                it.toVisitSupervisor()
            }
        }
    }

    override suspend fun getVisitSupervisorDetail(id: String): VisitSupervisorDetail {
        return wrapRetrofitException {
            visitsSupervisorsApi.getVisitSupervisorDetail(id).toVisitSupervisorDetail()
        }
    }

    override suspend fun checkInPost(id: String, request: CheckInRequestEntity): CheckIn {
        return wrapRetrofitException {
            visitsSupervisorsApi.checkInPost(id, request).toCheckIn()
        }
    }

    override suspend fun checkInGet(id: String): CheckIn {
        return wrapRetrofitException {
            visitsSupervisorsApi.checkInGet(id).toCheckIn()
        }
    }

    override suspend fun checkOutPost(id: String, request: CheckOutRequestEntity): CheckOut {
        return wrapRetrofitException {
            visitsSupervisorsApi.checkOutPost(id, request).toCheckOut()
        }
    }

    override suspend fun checkOutGet(id: String): CheckOut {
        return wrapRetrofitException {
            visitsSupervisorsApi.checkOutGet(id).toCheckOut()
        }
    }

    override suspend fun createRepeatStoreCheck(request: RepeatStoreCheckRequestEntity) =
        wrapRetrofitException { visitsSupervisorsApi.createRepeatStoreCheck(request) }

}