package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class UserFlag(val offset: Int) {
    NONE(0),
    DISCORD_EMPLOYEE(0),
    PARTNERED_SERVER_OWNER(1),
    HYPESQUAD_EVENTS(2),
    BUG_HUNTER_LEVEL_1(3),
    HOUSE_BRAVERY(6),
    HOUSE_BRILLIANCE(7),
    HOUSE_BALANCE(8),
    EARLY_SUPPORTER(9),
    TEAM_USER(10),
    BUG_HUNTER_LEVEL_2(14),
    VERIFIED_BOT(16),
    EARLY_VERIFIED_BOT_DEVELOPER(17),
    DISCORD_CERTIFIED_MODERATOR(18);

    val raw = 1L shl offset

    companion object {
        fun isApplied(effectiveFlags: Long, vararg flags: UserFlag) = isApplied(effectiveFlags, flags.raw)

        fun isApplied(effectiveFlags: Long, rawFlags: Long) = (effectiveFlags and rawFlags) == rawFlags

        fun getRaw(vararg flags: UserFlag): Long {
            var raw = 0L
            for (flag in flags) {
                if (flag != NONE) raw = raw or flag.raw
            }
            return raw
        }

        fun getAllApplied(effectiveFlags: Long): Array<UserFlag> {
            val applied = mutableListOf<UserFlag>()
            for (value in values()) {
                if (isApplied(effectiveFlags, value.raw)) applied.add(value)
            }
            return applied.toTypedArray()
        }
    }
}


val Array<out UserFlag>.raw
    get() = UserFlag.getRaw(*this)

val Array<out UserFlag>.bitwise: Long
    get() {
        val mapped = filter { it != UserFlag.NONE }.map { it.raw }
        var result = 0L
        for (l in mapped) {
            result = result or l
        }
        return result
    }

object UserFlagArraySerializer: KSerializer<Array<out UserFlag>> {
    override fun deserialize(decoder: Decoder): Array<out UserFlag> {
        return UserFlag.getAllApplied(decoder.decodeLong())
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("permisisons", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Array<out UserFlag>) {
        encoder.encodeLong(value.bitwise)
    }
}