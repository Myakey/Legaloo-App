package org.ambatujudge.legaloo

import org.ambatujudge.legaloo.viewmodel.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun name_rules() {
        assertEquals("Nama wajib diisi", AuthValidator.name("  "))
        assertEquals("Nama minimal 3 karakter", AuthValidator.name("Al"))
        assertEquals("Nama hanya boleh berisi huruf dan spasi", AuthValidator.name("Budi123"))
        assertNull(AuthValidator.name("Budi Santoso"))
    }

    @Test
    fun email_rules() {
        assertEquals("Email wajib diisi", AuthValidator.email(""))
        assertEquals("Format email tidak valid", AuthValidator.email("budi@legaloo"))
        assertEquals("Format email tidak valid", AuthValidator.email("budi.legaloo.id"))
        assertNull(AuthValidator.email(" budi@legaloo.id "))
    }

    @Test
    fun password_rules() {
        assertEquals("Kata sandi wajib diisi", AuthValidator.newPassword(""))
        assertEquals("Kata sandi minimal 8 karakter", AuthValidator.newPassword("abc12"))
        assertEquals("Kata sandi harus berisi huruf dan angka", AuthValidator.newPassword("abcdefgh"))
        assertNull(AuthValidator.newPassword("legaloo123"))
        assertNull(AuthValidator.password("x"))
    }
}
