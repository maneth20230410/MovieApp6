package com.example.moviemanager.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moviemanager.dao.MovieDao
import com.example.moviemanager.model.Movie
import com.example.moviemanager.service.MovieService
import kotlinx.coroutines.launch

@Composable
fun SearchActorScreen(
    onBackPressed: () -> Unit,
    movieDao: MovieDao
) {
    val movieService = remember { MovieService() }
    var searchQuery by remember { mutableStateOf("") }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    var lastSavedMovie by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Search for Actors",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter actor name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            // First try to search the local database
                            val dbMovies = movieDao.getMoviesByActor(searchQuery)

                            if (dbMovies.isNotEmpty()) {
                                movies = dbMovies
                            } else {
                                // If no results in database, search the API
                                val apiMovies = movieService.searchMoviesByActor(searchQuery)
                                movies = apiMovies
                            }
                        } catch (e: Exception) {
                            Log.e("SearchActorScreen", "Error searching actors", e)
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (showSavedMessage && lastSavedMovie != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "$lastSavedMovie saved to database successfully!",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Hide the message after a delay
            LaunchedEffect(showSavedMessage) {
                if (showSavedMessage) {
                    kotlinx.coroutines.delay(3000)
                    showSavedMessage = false
                }
            }
        }

        // Results
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(movies) { movie ->
                MovieListItem(
                    movie = movie,
                    onSaveMovie = {
                        coroutineScope.launch {
                            try {
                                movieDao.insertMovie(movie)
                                lastSavedMovie = movie.title
                                showSavedMessage = true
                            } catch (e: Exception) {
                                Log.e("SearchActorScreen", "Error saving movie", e)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    onSaveMovie: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Year: ${movie.year}")

            if (!movie.actors.isNullOrBlank()) {
                Text("Actors: ${movie.actors}")
            }

            if (!movie.director.isNullOrBlank()) {
                Text("Director: ${movie.director}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onSaveMovie
                ) {
                    Text("Save to Database")
                }
            }
        }
    }
}