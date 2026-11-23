package org.ambatujudge.legaloo.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.ambatujudge.legaloo.data.SessionStore
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Error thrown for any failed request. [detail] is already in Indonesian and ready to show. */
class ApiException(val status: Int, val detail: String, val field: String? = null) : Exception(detail)

@Serializable
private data class ApiError(val detail: String? = null, val field: String? = null)

object ApiClient {
    const val BASE_URL = "https://legaloo-backend.vercel.app"
    private const val NETWORK_ERROR = "Tidak dapat terhubung ke server. Periksa koneksi internetmu."

    val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    // Vercel cold starts can take a few seconds, so be generous with the read timeout.
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend inline fun <reified T> get(path: String, auth: Boolean = true): T =
        json.decodeFromString(execute("GET", path, null, auth))

    suspend inline fun <reified B, reified T> post(path: String, body: B, auth: Boolean = true): T =
        json.decodeFromString(execute("POST", path, json.encodeToString(body), auth))

    /**
     * Runs the request and returns the response body on 2xx. Otherwise throws [ApiException].
     * A 401 on an authenticated request ends the session (see [SessionStore.expire]).
     */
    @PublishedApi
    internal suspend fun execute(method: String, path: String, body: String?, auth: Boolean): String = withContext(Dispatchers.IO) {
        val builder = Request.Builder()
            .url(BASE_URL + path)
            .method(method, body?.toRequestBody(jsonMedia))
        if (auth) SessionStore.token?.let { builder.header("Authorization", "Bearer $it") }

        val (code, text) = try {
            http.newCall(builder.build()).execute().use { it.code to it.body?.string().orEmpty() }
        } catch (e: IOException) {
            throw ApiException(0, NETWORK_ERROR)
        }
        if (code in 200..299) return@withContext text

        if (code == 401 && auth) SessionStore.expire()
        val error = runCatching { json.decodeFromString<ApiError>(text) }.getOrNull()
        throw ApiException(code, error?.detail ?: "Terjadi kesalahan ($code). Coba lagi nanti.", error?.field)
    }
}
