package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ConnectionVisibility.Serializer::class)
enum class ConnectionVisibility(val raw: Int) {
    NONE(0),
    EVERYONE(1);

    object Serializer: KSerializer<ConnectionVisibility> {
        override fun deserialize(decoder: Decoder): ConnectionVisibility = decoder.decodeInt().let { raw -> values().first { it.raw == raw } }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("visibility", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: ConnectionVisibility) {
            encoder.encodeInt(value.raw)
        }
    }
}