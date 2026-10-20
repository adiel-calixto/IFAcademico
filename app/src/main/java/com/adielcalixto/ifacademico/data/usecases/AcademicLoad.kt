package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import com.adielcalixto.ifacademico.data.session.SessionFailure
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.Result
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import javax.inject.Inject

class AcademicLoad @Inject constructor(
    @PublishedApi internal val cache: CacheService,
    @PublishedApi internal val session: SessionCoordinator,
) {
    suspend inline fun <reified T, D> cached(
        key: String?, crossinline request: suspend () -> Response<T>, crossinline map: (T) -> D,
    ): Result<D, Error.DataError> = try {
        session.owned { owner ->
            val revision = session.freshness.revision.value
            val ownedKey = key?.let { "$owner/$revision/$it" }
            val cached = ownedKey?.let(cache::get).orEmpty()
            if (cached.isNotEmpty()) {
                Result.Success(map(Json.decodeFromString<T>(cached)))
            } else {
                val response = request()
                if (!response.isSuccessful) throw SessionFailure(NetworkError.Http(response.code()))
                val body = response.body() ?: throw SessionFailure(NetworkError.Protocol)
                if (ownedKey != null && session.isCurrent(owner) && revision == session.freshness.revision.value) {
                    session.publish(owner) { cache.put(ownedKey, Json.encodeToString(body)) }
                }
                Result.Success(map(body))
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(SessionCoordinator.errorOf(e))
    }
}
