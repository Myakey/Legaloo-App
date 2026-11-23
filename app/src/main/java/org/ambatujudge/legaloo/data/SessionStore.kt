package org.ambatujudge.legaloo.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Keeps the bearer token across app restarts. Tokens last 30 days; there is no refresh. */
object SessionStore {
    private const val PREFS = "legaloo_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"

    private var prefs: SharedPreferences? = null

    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    /** Emits when the server rejected the token (401). The app should go to the login screen. */
    val expired: SharedFlow<Unit> = _expired.asSharedFlow()

    fun init(context: Context) {
        if (prefs == null) prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    val token: String? get() = prefs?.getString(KEY_TOKEN, null)
    val isLoggedIn: Boolean get() = token != null
    val userName: String? get() = prefs?.getString(KEY_NAME, null)
    val userEmail: String? get() = prefs?.getString(KEY_EMAIL, null)

    fun save(auth: AuthResponse) {
        prefs?.edit()
            ?.putString(KEY_TOKEN, auth.token)
            ?.putString(KEY_NAME, auth.user.name)
            ?.putString(KEY_EMAIL, auth.user.email)
            ?.apply()
    }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
        LegalooRepository.clear()
    }

    fun expire() {
        if (!isLoggedIn) return
        clear()
        _expired.tryEmit(Unit)
    }
}
