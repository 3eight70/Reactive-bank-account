package com.learning.reactive.wallet.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Component
class PBKDF2Encoder(
    @Value("\${jwt.password.encoder.secret}")
    private val secret: String,
    @Value("\${jwt.password.encoder.iteration}")
    private val iteration: Int,
    @Value("\${jwt.password.encoder.keylength}")
    private val keyLength: Int
) : PasswordEncoder {
    private val SECRET_KEY_INSTANCE: String = "PBKDF2WithHmacSHA512"

    override fun encode(rawPassword: CharSequence?): String {
        try {
            val result: ByteArray = SecretKeyFactory.getInstance(SECRET_KEY_INSTANCE)
                .generateSecret(
                    PBEKeySpec(
                        rawPassword.toString().toCharArray(),
                        secret.toByteArray(),
                        iteration,
                        keyLength
                    )
                ).encoded
            return Base64.getEncoder().encodeToString(result)
        } catch (ex: NoSuchAlgorithmException) {
            throw RuntimeException(ex)
        } catch (ex: InvalidKeySpecException) {
            throw RuntimeException(ex)
        }
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return encode(rawPassword) == encodedPassword
    }
}