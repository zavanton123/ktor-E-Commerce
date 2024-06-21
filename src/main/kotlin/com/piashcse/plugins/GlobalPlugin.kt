package com.piashcse.plugins

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlin.reflect.KType

fun Application.configureBasic() {
    /* install(Compression)
     install(CORS) {
         anyHost()
     }*/
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            // serializeNulls()
        }
    }
    // Open api configuration
    install(OpenAPIGen) {
        // basic info
        info {
            version = "1.0.0"
            title = "Ktor E-Commerce"
            description = "API Documentation for Ktor Ecommerce App"
            contact {
                name = "PLabs Corporation"
                email = "piash599@gmail.com"
            }
        }
        // describe the server, add as many as you want
        server("http://localhost:8080/") {
            description = "Base URL"
        }
        //optional custom schema object name
        replaceModule(DefaultSchemaNamer, object : SchemaNamer {
            val regex = Regex("[A-Za-z0-9_.]+")
            override fun get(type: KType): String {
                return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
            }
        })
    }
}