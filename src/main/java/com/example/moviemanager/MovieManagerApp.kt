package com.example.moviemanager

import android.app.Application
import com.example.moviemanager.database.MovieDatabase

class MovieManagerApp : Application() {
    // Create a database instance that can be used throughout the app
    val database by lazy { MovieDatabase.getDatabase(this) }
    val movieDao by lazy { database.movieDao() }

    override fun onCreate() {
        super.onCreate()
        // Any application-wide initialization can go here
    }
}