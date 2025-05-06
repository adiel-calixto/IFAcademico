package com.adielcalixto.ifacademico.data.local

interface CacheService {
    fun get(key: String): String

    fun put(key: String, data: String)

    fun clearCache()
}