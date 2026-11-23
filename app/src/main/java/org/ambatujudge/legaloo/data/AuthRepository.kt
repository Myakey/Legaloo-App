package org.ambatujudge.legaloo.data

import org.ambatujudge.legaloo.data.remote.ApiClient

object AuthRepository {
    /** Throws [org.ambatujudge.legaloo.data.remote.ApiException] on failure. */
    suspend fun register(name: String, email: String, password: String): User =
        store(ApiClient.post("/auth/register", RegisterRequest(name, email, password), auth = false))

    /** Throws [org.ambatujudge.legaloo.data.remote.ApiException] on failure. */
    suspend fun login(email: String, password: String): User =
        store(ApiClient.post("/auth/login", LoginRequest(email, password), auth = false))

    private fun store(response: AuthResponse): User {
        LegalooRepository.clear()
        SessionStore.save(response)
        return response.user
    }
}
