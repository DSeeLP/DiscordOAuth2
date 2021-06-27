package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class GuildPermission(val offset: Int) {
    CREATE_INSTANT_INVITE(0),
    KICK_MEMBERS(1),
    BAN_MEMBERS(2),
    ADMINISTRATOR(3),
    MANAGE_CHANNELS(4),
    MANAGE_GUILD(5),
    ADD_REACTIONS(6),
    VIEW_AUDIT_LOG(7),
    PRIORITY_SPEAKER(8),
    STREAM(9),
    VIEW_CHANNEL(10),
    SEND_MESSAGES(11),
    SEND_TTS_MESSAGES(12),
    MANAGE_MESSAGES(13),
    EMBED_LINKS(14),
    ATTACH_FILES(15),
    READ_MESSAGE_HISTORY(16),
    MENTION_EVERYONE(17),
    USE_EXTERNAL_EMOJIS(18),
    VIEW_GUILD_INSIGHTS(19),
    CONNECT(20),
    SPEAK(21),
    MUTE_MEMBERS(22),
    DEAFEN_MEMBERS(23),
    MOVE_MEMBERS(24),
    USE_VAD(25),
    CHANGE_NICKNAME(26),
    MANAGE_NICKNAMES(27),
    MANAGE_ROLES(28),
    MANAGE_WEBHOOKS(29),
    MANAGE_EMOJIS(30),
    USE_SLASH_COMMANDS(31),
    REQUEST_TO_SPEAK(32),
    MANAGE_THREADS(34),
    USE_PUBLIC_THREADS(35),
    USE_PRIVATE_THREADS(36),
    UNKNOWN(-1);

    val raw = 1L shl offset

    companion object {
        fun isApplied(effectivePermissions: Long, vararg permissions: GuildPermission) =
            isApplied(effectivePermissions, permissions.raw)

        fun isApplied(effectivePermissions: Long, rawPerms: Long) = (effectivePermissions and rawPerms) == rawPerms

        fun getRaw(vararg permissions: GuildPermission): Long {
            var raw = 0L
            for (permission in permissions) {
                if (permission != UNKNOWN) raw = raw or permission.raw
            }
            return raw
        }

        fun getAllApplied(effectiveFlags: Long): Array<GuildPermission> {
            val applied = mutableListOf<GuildPermission>()
            for (value in values()) {
                if (isApplied(effectiveFlags, value.raw)) applied.add(value)
            }
            return applied.toTypedArray()
        }
    }
}

val Array<out GuildPermission>.raw
    get() = GuildPermission.getRaw(*this)

val Array<out GuildPermission>.bitwise: Long
    get() {
        val mapped = filter { it != GuildPermission.UNKNOWN }.map { it.raw }
        var result = 0L
        for (l in mapped) {
            result = result or l
        }
        return result
    }

object GuildPermissionArraySerializer: KSerializer<Array<out GuildPermission>> {
    override fun deserialize(decoder: Decoder): Array<out GuildPermission> {
        return GuildPermission.getAllApplied(decoder.decodeLong())
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("permisisons", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Array<out GuildPermission>) {
        encoder.encodeLong(value.bitwise)
    }
}