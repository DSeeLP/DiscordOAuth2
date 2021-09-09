package de.dseelp.oauth2.discord.api.authentication

import de.dseelp.oauth2.discord.api.DiscordClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.time.ExperimentalTime

abstract class DiscordOauth2Request {
    abstract val clientId: String
    abstract val clientSecret: String
    abstract val grantType: GrantType
    abstract val redirectUri: String?
    abstract val urlencoded: FormDataContent

    protected fun applyDefaultParameters(builder: ParametersBuilder) {
        builder.apply {
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("grant_type", grantType.typeName)
            if (redirectUri != null) append("redirect_uri", redirectUri!!)
        }
    }

    enum class GrantType(val typeName: String) {
        AUTHORIZATION_CODE("authorization_code"),
        REFRESH_TOKEN("refresh_token")
    }
}

data class RefreshDiscordOauth2Request(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String?,
    val refreshToken: String
): DiscordOauth2Request() {

    override val grantType: GrantType = GrantType.REFRESH_TOKEN

    override val urlencoded: FormDataContent = FormDataContent(Parameters.build {
        append("refresh_token", refreshToken)
        applyDefaultParameters(this)
    })
}

data class AuthorizeDiscordOauth2Request(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String?,
    val code: String,
    val state: String,
    val stateParameters: Array<String>?
): DiscordOauth2Request() {
    override val grantType: GrantType = GrantType.AUTHORIZATION_CODE

    override val urlencoded: FormDataContent = FormDataContent(Parameters.build {
        append("code", code)
        applyDefaultParameters(this)
    })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthorizeDiscordOauth2Request) return false

        if (clientId != other.clientId) return false
        if (clientSecret != other.clientSecret) return false
        if (redirectUri != other.redirectUri) return false
        if (code != other.code) return false
        if (state != other.state) return false
        if (stateParameters != null) {
            if (other.stateParameters == null) return false
            if (!stateParameters.contentEquals(other.stateParameters)) return false
        } else if (other.stateParameters != null) return false
        if (grantType != other.grantType) return false
        if (urlencoded != other.urlencoded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientId.hashCode()
        result = 31 * result + clientSecret.hashCode()
        result = 31 * result + (redirectUri?.hashCode() ?: 0)
        result = 31 * result + code.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (stateParameters?.contentHashCode() ?: 0)
        result = 31 * result + grantType.hashCode()
        result = 31 * result + urlencoded.hashCode()
        return result
    }
}

@OptIn(ExperimentalTime::class)
suspend fun makeDiscordOAuth2Request(
    client: DiscordClient,
    request: DiscordOauth2Request
): DiscordOauth2Response {
    val response = client.httpClient.post<HttpResponse>() {
        url {
            takeFrom(client.endpoint)
            pathComponents("oauth2", "token")
        }
        accept(ContentType.Application.Json)
        body = request.urlencoded
    }
    return when (response.status) {
        HttpStatusCode.BadRequest -> response.receive<DiscordOauth2Response.FailedDiscordOauth2Response>()
        HttpStatusCode.OK -> response.receive<DiscordOauth2Response.SuccessfulDiscordOauth2Response>()
        else -> throw IllegalStateException("Unexpected response from DiscordAPI! status: ${response.status} body: ${runCatching { response.receive<String>() }.getOrNull()}")
    }
}