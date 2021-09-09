package de.dseelp.oauth2.discord.api.authentication

import de.dseelp.oauth2.discord.api.utils.randomAlphanumeric
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface StateController {
    fun consumeState(state: String): String?
    fun generateState(redirectUri: String, customParameters: Array<String>): String
    fun hasState(state: String, redirectUri: String? = null): Boolean
    fun getParameters(state: String): Array<String>?
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
        val parameterSeparator = ";:§\$/;"
        val idSeparator = ";;"
        override fun consumeState(state: String): String? = states.remove(state)

        override fun generateState(redirectUri: String, customParameters: Array<String>): String {
            val id = randomAlphanumeric(stateLength, random = secureRandom)
            val parameterString = customParameters.joinToString(parameterSeparator)
            val combined = id+idSeparator+parameterString
            val state = Base64.getEncoder().encode(combined.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
            states[state] = redirectUri
            return state
        }

        override fun hasState(state: String, redirectUri: String?): Boolean {
            return if (redirectUri == null) states.containsKey(state)
            else states.entries.firstOrNull { it.key == state && it.value == redirectUri } != null
        }

        override fun getParameters(state: String): Array<String>? {
            if (!states.containsKey(state)) return null
            val s = Base64.getDecoder().decode(state).toString(Charsets.UTF_8)
            val splitted = s.split(idSeparator)
            if (splitted.size != 2) return null
            return splitted[1].split(parameterSeparator).toTypedArray()
        }

    }
}