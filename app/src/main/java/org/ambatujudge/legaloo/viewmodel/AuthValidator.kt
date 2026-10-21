package org.ambatujudge.legaloo.viewmodel

object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val nameRegex = Regex("^[A-Za-z .'-]+$")

    fun name(value: String): String? = when {
        value.isBlank() -> "Nama wajib diisi"
        value.trim().length < 3 -> "Nama minimal 3 karakter"
        !nameRegex.matches(value.trim()) -> "Nama hanya boleh berisi huruf dan spasi"
        else -> null
    }

    fun email(value: String): String? = when {
        value.isBlank() -> "Email wajib diisi"
        !emailRegex.matches(value.trim()) -> "Format email tidak valid"
        else -> null
    }

    fun newPassword(value: String): String? = when {
        value.isEmpty() -> "Kata sandi wajib diisi"
        value.length < 8 -> "Kata sandi minimal 8 karakter"
        value.none { it.isLetter() } || value.none { it.isDigit() } -> "Kata sandi harus berisi huruf dan angka"
        else -> null
    }

    fun password(value: String): String? = if (value.isEmpty()) "Kata sandi wajib diisi" else null
}
