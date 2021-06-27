package de.dseelp.oauth2.discord.api.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable()
enum class Scope(val text: String) {
    BOT("bot"),
    CONNECTIONS("connections"),
    EMAIL("email"),
    IDENTIFY("identify"),
    GUILDS("guilds"),
    GUILDS_JOIN("guilds.join"),
    GDM_JOIN("gdm.join"),
    MESSAGES_READ("messages.read"),
    RPC("rpc"),
    RPC_API("rpc.api"),
    RPC_NOTIFICATION_READ("rpc.notifications.read"),
    WEBHOOK_INCOMING("webhook.incoming"),
    UNKNOWN("");

    companion object {
        fun parse(scopes: String): Array<Scope> = scopes.split(' ').map { get(it) }.toTypedArray()

        operator fun get(text: String) = values().first { it.text.equals(text, ignoreCase = true) }
    }
}

object ScopeArraySerializer: KSerializer<Array<Scope>> {
    override fun deserialize(decoder: Decoder): Array<Scope> = Scope.parse(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("scopes", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Array<Scope>) = encoder.encodeString(value.join())

}

fun Array<out Scope>.join(bySpace: Boolean = true): String = joinToString(if (bySpace) " " else "%20") { it.text }