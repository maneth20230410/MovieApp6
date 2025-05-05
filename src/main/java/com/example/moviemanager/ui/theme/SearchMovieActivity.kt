package com.example.moviemanager.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.moviemanager.database.MovieDatabase
import com.example.moviemanager.ui.theme.theme.MoviemanagerTheme

class SearchMovieActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get database instance
        val database = MovieDatabase.getDatabase(applicationContext)
        val movieDao = database.movieDao()

        setContent {
            MoviemanagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchMovieScreen(
                        onBackPressed = { finish() },
                        movieDao = movieDao
                    )
                }
            }
        }
    }
}