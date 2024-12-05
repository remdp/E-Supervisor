package com.euromix.esupervisor.sources.visitsSupervisors

import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsSource
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitVisitsSupervisorsSource @Inject constructor(private val config: RetrofitConfig) :
    BaseRetrofitSource(config), VisitsSupervisorsSource {

    private val visitsSupervisorsApi = retrofit.create(VisitsSupervisorsApi::class.java)

    override suspend fun getVisitsSupervisors(): List<VisitSupervisor> {
        return wrapRetrofitException {
            visitsSupervisorsApi.getVisitsSupervisor().map {
                it.toVisitSupervisor()
            }
        }
    }

    override suspend fun getVisitSupervisorDetail(id: String): VisitSupervisorDetail {
        return wrapRetrofitException {
            visitsSupervisorsApi.getVisitSupervisorDetail(id).toVisitSupervisorDetail()
        }
    }

    override suspend fun checkIn(id: String, request: CheckInRequestEntity) {
        return wrapRetrofitException {
            visitsSupervisorsApi.checkIn(id, request)
        }
    }
}