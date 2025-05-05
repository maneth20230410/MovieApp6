package com.example.moviemanager.service

import android.util.Log
import com.example.moviemanager.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MovieService {
    private val apiKey = "a5955c7"
    private val baseUrl = "https://www.omdbapi.com/"

    suspend fun fetchMovie(title: String): Movie? {
        Log.i(tag, "Fetching movie with title: $title")
        val urlString = "${baseUrl}?t=${title}&apikey=${apiKey}"
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val strb = StringBuilder()

        withContext(Dispatchers.IO) {
            val br = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String? = br.readLine()

            while (line != null) {
                strb.append(line + "\\n")
                line = br.readLine()
            }
        }

        return parseJSON(strb.toString())
    }

    suspend fun searchMoviesByTitle(title: String): List<Movie> {
        Log.i(tag, "Searching movies with title: $title")
        val urlString = "${baseUrl}?s=${title}&apikey=${apiKey}"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        val strb = StringBuilder()

        withContext(Dispatchers.IO) {
            val br = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String? = br.readLine()

            while (line != null) {
                strb.append(line + "\\n")
                line = br.readLine()
            }
        }

        return parseSearchResults(strb.toString())
    }

    suspend fun searchMoviesByActor(actorName: String): List<Movie> {
        // For the OMDb API, we need to perform a general search first
        // and then filter for movies containing the actor
        val urlString = "${baseUrl}?s=${actorName}&apikey=${apiKey}"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        val strb = StringBuilder()

        withContext(Dispatchers.IO) {
            val br = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String? = br.readLine()

            while (line != null) {
                strb.append(line + "\\n")
                line = br.readLine()
            }
        }

        val searchResults = parseSearchResults(strb.toString())
        val moviesWithActor = mutableListOf<Movie>()

        // For each movie in search results, fetch complete details and check actors
        for (movie in searchResults) {
            val detailedMovie = fetchMovie(movie.title)
            if (detailedMovie != null && detailedMovie.actors?.contains(actorName, ignoreCase = true) == true) {
                moviesWithActor.add(detailedMovie)
            }
        }

        return moviesWithActor
    }

    private fun parseJSON(jsonString: String): Movie? {
        try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.optString("Response") == "True") {
                return Movie.fromJson(jsonObject)
            }
        } catch (e: Exception) {
            Log.e(tag, "parseJSON", e)
        }
        return null
    }

    private fun parseSearchResults(jsonString: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.optString("Response") == "True") {
                val searchArray = jsonObject.getJSONArray("Search")
                for (i in 0 until searchArray.length()) {
                    val movieObject = searchArray.getJSONObject(i)
                    val movie = Movie(
                        title = movieObject.getString("Title"),
                        year = movieObject.getString("Year"),
                        rated = null,
                        released = null,
                        runtime = null,
                        genre = null,
                        director = null,
                        writer = null,
                        actors = null,
                        plot = null,
                        language = null,
                        country = null,
                        awards = null,
                        imdbID = movieObject.optString("imdbID", ""),
                        imdbRating = null
                    )
                    movies.add(movie)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "parseSearchResults", e)
        }
        return movies
    }

    companion object {
        private const val tag = "MovieService"
    }
}