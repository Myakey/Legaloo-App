package org.ambatujudge.legaloo.data

object AuthRepository {
    private val accounts = DummyData.accounts.toMutableList()
    fun findByEmail(email: String): UserAccount? = accounts.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
    fun register(account: UserAccount) { accounts += account }
    fun login(email: String, password: String): UserAccount? = findByEmail(email)?.takeIf { it.password == password }
}
