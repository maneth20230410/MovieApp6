package com.example.moviemanager.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moviemanager.dao.MovieDao
import com.example.moviemanager.model.Movie
import com.example.moviemanager.service.MovieService
import kotlinx.coroutines.launch

@Composable
fun SearchMovieScreen(
    onBackPressed: () -> Unit,
    movieDao: MovieDao
) {
    val movieService = remember { MovieService() }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentMovie by remember { mutableStateOf<Movie?>(null) }
    var showMovieDetails by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

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
                text = "Search for Movies",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter movie title") },
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
                            val result = movieService.fetchMovie(searchQuery)
                            if (result != null) {
                                currentMovie = result
                                showMovieDetails = true
                            } else {
                                // Handle no results
                                showMovieDetails = false
                            }
                        } catch (e: Exception) {
                            Log.e("SearchMovieScreen", "Error searching movies", e)
                            showMovieDetails = false
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search for Movie")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (showSavedMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Movie saved to database successfully!",
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

        if (showMovieDetails && currentMovie != null) {
            MovieDetails(
                movie = currentMovie!!,
                onSaveMovie = {
                    coroutineScope.launch {
                        try {
                            movieDao.insertMovie(currentMovie!!)
                            showSavedMessage = true
                        } catch (e: Exception) {
                            Log.e("SearchMovieScreen", "Error saving movie", e)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun MovieDetails(
    movie: Movie,
    onSaveMovie: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Year: ${movie.year}")
            Text("Rated: ${movie.rated ?: "N/A"}")
            Text("Released: ${movie.released ?: "N/A"}")
            Text("Runtime: ${movie.runtime ?: "N/A"}")
            Text("Genre: ${movie.genre ?: "N/A"}")
            Text("Director: ${movie.director ?: "N/A"}")
            Text("Writer: ${movie.writer ?: "N/A"}")
            Text("Actors: ${movie.actors ?: "N/A"}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Plot:",
                fontWeight = FontWeight.Bold
            )
            Text(movie.plot ?: "N/A")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSaveMovie,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Movie to Database")
            }
        }
    }
}