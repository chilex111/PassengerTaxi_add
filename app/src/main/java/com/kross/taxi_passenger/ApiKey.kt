package com.kross.taxi_passenger

import android.content.Context
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

class ApiKey(context: Context) {

    private val passagerApi = context.getString(R.string.passagerApiKey)

    fun getPassagerApiKey(): String {
        val sm = SimpleDateFormat("dd/MM/yyyy")
        val date = Date()
        val strDate = sm.format(date) + passagerApi
        return getStringInMd5(strDate)
    }

    fun getStringInMd5(word: String): String {
        var m: MessageDigest? = null
        try {
            m = MessageDigest.getInstance("MD5")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        m?.reset()
        m?.update(word.toByteArray())
        val digest = m?.digest()
        val bigInt = BigInteger(1, digest)
        var hashtext = bigInt.toString(16)
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext
    }
}