package com.euromix.esupervisor.sources.visits

import com.euromix.esupervisor.app.model.visits.VisitsSource
import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.visits.entities.VisitsChangeTypeRequestEntity
import com.euromix.esupervisor.sources.visits.entities.VisitsRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitVisitsSource @Inject constructor(private val config: RetrofitConfig) :
    BaseRetrofitSource(config), VisitsSource {

    private val visitsApi = retrofit.create(VisitsApi::class.java)
    override suspend fun getVisits(request: VisitsRequestEntity?): List<Visit> {
        return wrapRetrofitException {

            val jsonAdapter = config.moshi.adapter(VisitsRequestEntity::class.java)

            val response = visitsApi.getVisits(jsonAdapter.toJson(request))
            response.map {
                it.toVisit()
            }
        }
    }

    override suspend fun getChangeTypeVisitReasons(): List<ChangeVisitTypeReason> {
        return wrapRetrofitException {
            val response = visitsApi.getChangeTypeVisitReasons()
            response.map {
                it.toChangeVisitTypeReason()
            }
        }
    }

    override suspend fun changeVisitsType(visitsChangeType: VisitsChangeTypeRequestEntity) =
        wrapRetrofitException { visitsApi.changeVisitsType(visitsChangeType).string() }

}