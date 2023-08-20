package com.github.mraghurao92.javadocgenerator.util

import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe


class SecretManager {

    /**
     * Retrieve the secret based on a given key.
     *
     * @param key the key for the secret
     * @return the secret as a String or null if not found
     */
    fun getSecret(key: String): String? {
        val credentials: Credentials? = PasswordSafe.instance.get(getCredentialAttribute())
        if (credentials != null) {
            return credentials.getPasswordAsString()
        }
        return null;
    }

    /**
     * Stores a secret in the password safe using the provided key and secret.
     *
     * @param key the key to associate with the secret
     * @param secret the secret to be stored
     */
    fun storeSecret(key: String, secret: String) {
        val credentialAttributes = getCredentialAttribute()

        val credentials = Credentials(key, secret)
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }


    companion object {

        /**
         * This method returns the CredentialAttributes object with specified attributes.
         *
         * @return CredentialAttributes - The generated CredentialAttributes object.
         */
        fun getCredentialAttribute(): CredentialAttributes {
            return CredentialAttributes(API_KEY, null, null, true)
        }
    }
}
