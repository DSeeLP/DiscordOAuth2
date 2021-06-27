package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    val id: String,
    val username: String,
    val discriminator: String,
    val avatar: String,
    val locale: String,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean,
    @Serializable(with = UserFlagArraySerializer::class) val flags: Array<UserFlag>,
    @Serializable(with = UserFlagArraySerializer::class) @SerialName("public_flags") val publicFlags: Array<UserFlag>,
    @SerialName("premium_type") val nitroType: NitroType? = null,
    val email: String? = null,
    val verified: Boolean? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscordUser) return false

        if (id != other.id) return false
        if (username != other.username) return false
        if (discriminator != other.discriminator) return false
        if (avatar != other.avatar) return false
        if (locale != other.locale) return false
        if (mfaEnabled != other.mfaEnabled) return false
        if (!flags.contentEquals(other.flags)) return false
        if (!publicFlags.contentEquals(other.publicFlags)) return false
        if (nitroType != other.nitroType) return false
        if (email != other.email) return false
        if (verified != other.verified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + discriminator.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + locale.hashCode()
        result = 31 * result + mfaEnabled.hashCode()
        result = 31 * result + flags.contentHashCode()
        result = 31 * result + publicFlags.contentHashCode()
        result = 31 * result + nitroType.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (verified?.hashCode() ?: 0)
        return result
    }
}
