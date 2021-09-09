package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    val id: ULong,
    val username: String,
    val discriminator: Short,
    val avatar: String?,
    val locale: String,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean,
    @Serializable(with = UserFlagArraySerializer::class) val flags: Array<UserFlag>,
    @Serializable(with = UserFlagArraySerializer::class) @SerialName("public_flags") val publicFlags: Array<UserFlag>,
    @SerialName("premium_type") val nitroType: NitroType? = null,
    val banner: String? = null,
    @SerialName("banner_color") val bannerColor: String? = null,
    @SerialName("accent_color") val accentColor: Int? = null,
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
        if (banner != other.banner) return false
        if (accentColor != other.accentColor) return false
        if (email != other.email) return false
        if (verified != other.verified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + discriminator
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + locale.hashCode()
        result = 31 * result + mfaEnabled.hashCode()
        result = 31 * result + flags.contentHashCode()
        result = 31 * result + publicFlags.contentHashCode()
        result = 31 * result + (nitroType?.hashCode() ?: 0)
        result = 31 * result + (banner?.hashCode() ?: 0)
        result = 31 * result + (accentColor ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (verified?.hashCode() ?: 0)
        return result
    }

}
