package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NitroType.Serializer::class)
enum class NitroType(val raw: Int) {
    NONE(0),
    CLASSIC(1),
    NITRO(2);

    object Serializer : KSerializer<NitroType> {
        override fun deserialize(decoder: Decoder): NitroType =
            decoder.decodeInt().let { raw -> values().first { it.raw == raw } }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("nitroType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: NitroType) = encoder.encodeInt(value.raw)
    }
}