package de.dseelp.oauth2.discord.api.authentication

import de.dseelp.oauth2.discord.api.DiscordSession
import de.dseelp.oauth2.discord.api.DiscordClient
import de.dseelp.oauth2.discord.api.utils.DurationSerializer
import de.dseelp.oauth2.discord.api.utils.Scope
import de.dseelp.oauth2.discord.api.utils.ScopeArraySerializer
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

sealed interface DiscordOauth2Response {
    val isError: Boolean
    val error: String?
    val errorDescription: String?

    @OptIn(ExperimentalTime::class)
    fun generateSession(client: DiscordClient): DiscordSession {
        if (this !is SuccessfulDiscordOauth2Response) throw IllegalArgumentException("A session can only be generated for a successful response!")
        return DiscordSession(client, accessToken, refreshToken, scopes, tokenType, Clock.System.now()+expiresIn)
    }

    @ExperimentalTime
    @Serializable
    data class SuccessfulDiscordOauth2Response(
        @SerialName("access_token") val accessToken: String,
        @SerialName("expires_in") @Serializable(with = DurationSerializer.Seconds::class) val expiresIn: Duration,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("scope") @Serializable(with = ScopeArraySerializer::class) val scopes: Array<Scope>,
        @SerialName("token_type") val tokenType: String
    ) : DiscordOauth2Response {
        @Transient
        override val isError: Boolean = false

        @Transient
        override val error: String? = null

        @Transient
        override val errorDescription: String? = null
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SuccessfulDiscordOauth2Response) return false

            if (accessToken != other.accessToken) return false
            if (expiresIn != other.expiresIn) return false
            if (refreshToken != other.refreshToken) return false
            if (!scopes.contentEquals(other.scopes)) return false
            if (tokenType != other.tokenType) return false
            if (isError != other.isError) return false
            if (error != other.error) return false
            if (errorDescription != other.errorDescription) return false

            return true
        }

        override fun hashCode(): Int {
            Clock.System.now()
            Clock.System.now() + Duration.seconds(10)
            var result = accessToken.hashCode()
            result = 31 * result + expiresIn.hashCode()
            result = 31 * result + refreshToken.hashCode()
            result = 31 * result + scopes.contentHashCode()
            result = 31 * result + tokenType.hashCode()
            result = 31 * result + isError.hashCode()
            result = 31 * result + (error?.hashCode() ?: 0)
            result = 31 * result + (errorDescription?.hashCode() ?: 0)
            return result
        }
    }

    @Serializable
    data class FailedDiscordOauth2Response(
        override val error: String,
        @SerialName("error_description") override val errorDescription: String
    ) : DiscordOauth2Response {
        override val isError: Boolean = true
    }
}