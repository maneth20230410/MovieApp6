package com.example.moviemanager.ui.theme

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Movie Manager",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Add Movies to DB Button
        Button(
            onClick = {
                navigateToSearchMovie(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Movies to DB")
        }

        // Search for Movies Button
        Button(
            onClick = {
                navigateToSearchMovie(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Search for Movies")
        }

        // Search for Actors Button
        Button(
            onClick = {
                navigateToSearchActor(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Search for Actors")
        }
    }
}

private fun navigateToSearchMovie(context: Context) {
    val intent = Intent(context, SearchMovieActivity::class.java)
    context.startActivity(intent)
}

private fun navigateToSearchActor(context: Context) {
    val intent = Intent(context, SearchActorActivity::class.java)
    context.startActivity(intent)
}