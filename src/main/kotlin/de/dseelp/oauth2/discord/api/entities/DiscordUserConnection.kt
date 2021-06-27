package de.dseelp.oauth2.discord.api.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordUserConnection(
    val type: String,
    val id: String,
    val name: String,
    val visibility: ConnectionVisibility,
    @SerialName("friend_sync") val friendSync: Boolean,
    @SerialName("show_activity") val showActivity: Boolean,
    val verified: Boolean
)