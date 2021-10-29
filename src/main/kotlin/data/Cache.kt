package data

import PropertiesProvider.configOf
import java.net.URI
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class Cache {

    private var jedisPool: JedisPool? = null
    private val jedis: Jedis?
        get() = jedisPool?.resource

    init {
        jedisPool = JedisPool(toJedisPoolConfig(), URI.create(configOf("REDIS_URL")), 600)
    }

    fun set(id: String, value: String) {
        jedis?.set(id, value)
    }

    fun get(id: String) = jedis?.get(id)

    private fun toJedisPoolConfig(): JedisPoolConfig {
        return JedisPoolConfig().apply {
            maxWaitMillis = 500
            maxTotal = 10
            maxIdle = 5
            minIdle = 1
            testOnBorrow = true
            testOnReturn = true
            testWhileIdle = true
        }
    }
}
