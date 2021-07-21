package de.dseelp.oauth2.discord.api

import de.dseelp.oauth2.discord.api.authentication.AuthorizeDiscordOauth2Request
import de.dseelp.oauth2.discord.api.authentication.DiscordOauth2Response
import de.dseelp.oauth2.discord.api.authentication.StateController
import de.dseelp.oauth2.discord.api.authentication.makeDiscordOAuth2Request
import de.dseelp.oauth2.discord.api.entities.GuildPermission
import de.dseelp.oauth2.discord.api.entities.bitwise
import de.dseelp.oauth2.discord.api.utils.Scope
import de.dseelp.oauth2.discord.api.utils.join
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

data class DiscordClient(
    val httpClient: HttpClient,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val endpoint: Url = Url("https://discord.com/api/"),
    val botToken: String? = null,
    val stateController: StateController = StateController.Default()
) {

    fun generateAuthorizationUrl(vararg scopes: Scope) = generateAuthorizationUrl(scopes.toList().toTypedArray())

    fun generateAuthorizationUrl(scopes: Array<Scope>, permissions: Array<GuildPermission> = arrayOf(), botInvite: Boolean = false): String {
        val scopeString = scopes.join()
        return url {
            takeFrom(Url("https://discord.com/"))
            pathComponents("oauth2", "authorize")
            parameters.apply {
                append("client_id", clientId)
                append("permissions", permissions.bitwise.toString())
                if (!botInvite) append("redirect_uri", redirectUri)
                append("scope", scopeString)
                append("response_type", "code")
                append("state", stateController.generateState(redirectUri))
            }
        }.replace(scopeString.replace(' ', '+'), scopes.join(false))
    }

    fun createBotInvite(vararg permissions: GuildPermission) = generateAuthorizationUrl(arrayOf(Scope.BOT), permissions.toList().toTypedArray(), true)

    fun configureKtorRoute(
        route: Route,
        routeName: String,
        noCodeHandler: suspend PipelineContext<Unit, ApplicationCall>.(String?) -> Unit,
        responseHandler: suspend PipelineContext<Unit, ApplicationCall>.(DiscordOauth2Response) -> Unit
    ) {
        route.apply {
            get(routeName) {
                val code = call.parameters["code"]
                val received = call.receiveOrNull<String>()
                if (code == null) {
                    noCodeHandler.invoke(this, received)
                    return@get
                }
                val request = AuthorizeDiscordOauth2Request(clientId, clientSecret, redirectUri, code)
                responseHandler.invoke(this, makeDiscordOAuth2Request(this@DiscordClient, request))
            }
        }
    }
}