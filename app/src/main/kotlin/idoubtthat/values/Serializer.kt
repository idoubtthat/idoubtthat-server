package idoubtthat.values

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Serializer {
    private val module = KotlinModule.Builder().build()

    val mapper: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .registerModule(module)
        .registerModule(
            JavaTimeModule().also {
                it.addSerializer(LocalDateSerializer(DateTimeFormatter.ISO_DATE))
                it.addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_DATE))
            }
        )
}