package de.dseelp.oauth2.discord.api

import de.dseelp.oauth2.discord.api.authentication.RefreshDiscordOauth2Request
import de.dseelp.oauth2.discord.api.authentication.makeDiscordOAuth2Request
import de.dseelp.oauth2.discord.api.entities.DiscordGuild
import de.dseelp.oauth2.discord.api.entities.DiscordUser
import de.dseelp.oauth2.discord.api.entities.DiscordUserConnection
import de.dseelp.oauth2.discord.api.utils.Scope
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class DiscordSession(val client: DiscordClient, val accessToken: String, val refreshToken: String, val scopes: Array<Scope>, val tokenType: String, val expirationTime: Instant) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscordSession) return false

        if (accessToken != other.accessToken) return false
        if (refreshToken != other.refreshToken) return false
        if (!scopes.contentEquals(other.scopes)) return false
        if (tokenType != other.tokenType) return false
        if (expirationTime != other.expirationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accessToken.hashCode()
        result = 31 * result + refreshToken.hashCode()
        result = 31 * result + scopes.contentHashCode()
        result = 31 * result + tokenType.hashCode()
        result = 31 * result + expirationTime.hashCode()
        return result
    }

    suspend fun refresh(): DiscordSession {
        return makeDiscordOAuth2Request(client, RefreshDiscordOauth2Request(client.clientId, client.clientSecret, client.redirectUri, refreshToken)).generateSession(client)
    }

    private fun HttpRequestBuilder.authenticate() {
        header(HttpHeaders.Authorization, "Bearer $accessToken")
    }

    suspend fun getUser(): DiscordUser {
        val response = client.httpClient.get<HttpResponse> {
            url {
                takeFrom(client.endpoint)
                pathComponents("users", "@me")
            }
            authenticate()
        }
        return response.receive()
    }

    suspend fun getGuilds(): Array<DiscordGuild> {
        val response = client.httpClient.get<HttpResponse>() {
            url {
                takeFrom(client.endpoint)
                pathComponents("users", "@me", "guilds")
            }
            authenticate()
        }
        return response.receive()
    }

    suspend fun getConnections(): Array<DiscordUserConnection> {
        val response = client.httpClient.get<HttpResponse>() {
            url {
                takeFrom(client.endpoint)
                pathComponents("users", "@me", "connections")
            }
            authenticate()
        }
        return response.receive()
    }

    @Serializable
    data class DiscordOauth2GuildJoinRequest(@SerialName("access_token") val accessToken: String)
}
