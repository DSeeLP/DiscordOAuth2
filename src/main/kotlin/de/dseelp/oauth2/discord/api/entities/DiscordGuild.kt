package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class DiscordGuild(
    val id: ULong,
    val name: String,
    val icon: String?,
    val owner: Boolean,
    val features: Array<String>,
    @Serializable(with = GuildPermissionArraySerializer::class) val permissions: Array<GuildPermission>,
    @SerialName("permissions_new") @Serializable(with = GuildPermissionArraySerializer::class) val permissionsNew: Array<GuildPermission>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscordGuild) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (owner != other.owner) return false
        if (!permissions.contentEquals(other.permissions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + permissions.contentHashCode()
        return result
    }
}