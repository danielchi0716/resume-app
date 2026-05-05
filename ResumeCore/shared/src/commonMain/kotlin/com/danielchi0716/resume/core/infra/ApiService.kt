package com.danielchi0716.resume.core.infra

import com.danielchi0716.resume.core.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class ApiService(
    private val config: NetworkConfig,
    engine: HttpClientEngine? = null,
) {
    val client: HttpClient

    private val block: HttpClientConfig<*>.() -> Unit = {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = config.host
            }
            contentType(ContentType.Application.Json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    init {
        client = if (engine != null) HttpClient(engine, block) else HttpClient(block)
    }
}
