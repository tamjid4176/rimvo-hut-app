package com.example

import android.app.Application
import com.example.data.local.RimvoDatabase
import com.example.data.local.UserPreferences
import com.example.data.repository.RimvoRepository

class RimvoHutApp : Application() {
    val database: RimvoDatabase by lazy { RimvoDatabase.getInstance(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }
    val repository: RimvoRepository by lazy {
        RimvoRepository(
            cartDao = database.cartDao(),
            installmentDao = database.installmentDao(),
            kycDao = database.kycDao(),
            userPrefs = userPreferences
        )
    }
}
