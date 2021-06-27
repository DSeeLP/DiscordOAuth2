package de.dseelp.oauth2.discord.api.authentication

import de.dseelp.oauth2.discord.api.utils.randomAlphanumeric
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

interface StateController {
    fun consumeState(state: String): String?
    fun generateState(redirectUri: String): String
    fun hasState(state: String, redirectUri: String? = null): Boolean
    fun consumeAndCheckState(state: String, redirectUri: String): Boolean {
        return if (hasState(state, redirectUri)) {
            consumeState(state)
            true
        } else {
            false
        }
    }

    class Default(val stateLength: Int = 8): StateController {
        val secureRandom = SecureRandom()
        val states = ConcurrentHashMap<String, String>()
        override fun consumeState(state: String): String? = states.remove(state)

        override fun generateState(redirectUri: String): String {
            val state = randomAlphanumeric(stateLength, random = secureRandom)
            states[state] = redirectUri
            return state
        }

        override fun hasState(state: String, redirectUri: String?): Boolean {
            return if (redirectUri == null) states.containsKey(state)
            else states.entries.firstOrNull { it.key == state && it.value == redirectUri } != null
        }

    }
}