package de.dseelp.oauth2.discord.api.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
open class DurationSerializer(val decode: (Long) -> Duration, val encode: (Duration) -> Long) : KSerializer<Duration> {
    override fun deserialize(decoder: Decoder): Duration = decode.invoke(decoder.decodeLong())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("duration", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Duration) = encoder.encodeLong(encode.invoke(value))

    object Nanoseconds : DurationSerializer(decode = { Duration.nanoseconds(it) }, encode = { it.inWholeNanoseconds })
    object Microseconds : DurationSerializer(decode = { Duration.microseconds(it) }, encode = { it.inWholeMicroseconds })
    object Milliseconds : DurationSerializer(decode = { Duration.milliseconds(it) }, encode = { it.inWholeMilliseconds })
    object Seconds : DurationSerializer(decode = { Duration.seconds(it) }, encode = { it.inWholeSeconds })
    object Minutes : DurationSerializer(decode = { Duration.minutes(it) }, encode = { it.inWholeMinutes })
    object Hours : DurationSerializer(decode = { Duration.hours(it) }, encode = { it.inWholeHours })
    object Days : DurationSerializer(decode = { Duration.days(it) }, encode = { it.inWholeDays })

}


