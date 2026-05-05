package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.infra.ApiService
import com.danielchi0716.resume.core.infra.ResumeInfraService
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

object ResumeCore : KoinComponent
object ResumeEntry {
    fun init(config: NetworkConfig) {
        startKoin {
            modules(
                module {
                    single { config }
                    single { ApiService(get()) }
                    single { get<ApiService>().client }
                    single { ResumeInfraService(get()) }
                    single<ResumeService> { ResumeServiceImpl(get()) }
                }
            )
        }
    }
}
