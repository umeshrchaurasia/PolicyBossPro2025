package com.policyboss.policybosspro.view.qrScanner.model

object QRParser {

    fun parse(raw: String): QRLoginData? {

        return try {

            val parts = raw.split("|")

            // Expected:
            // 0 -> token section
            // 1 -> ip
            // 2 -> browser/device

            if (parts.size < 3) {
                return null
            }

            val token = parts[0]
                .substringAfter("token=")
                .trim()

            val ipAddress = parts[1]
                .trim()

            val webDevice = parts[2]
                .trim()

            QRLoginData(

                token = token,

                ipAddress = ipAddress,

                webDevice = webDevice
            )

        } catch (e: Exception) {

            null
        }
    }
}