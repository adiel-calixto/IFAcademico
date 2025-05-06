package com.adielcalixto.ifacademico.data.local

import androidx.collection.LruCache

class LruCacheService : CacheService {
    private val store = object : LruCache<String, String>(1024 * 256 /* 256 KiB*/) {
        override fun sizeOf(key: String, value: String): Int {
            return value.toByteArray(Charsets.UTF_8).size
        }
    }

    override fun get(key: String): String {
        return store[key] ?: ""
    }

    override fun put(key: String, data: String) {
        store.put(key, data)
    }

    override fun clearCache() {
        store.evictAll()
    }
}