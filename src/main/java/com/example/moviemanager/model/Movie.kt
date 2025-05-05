package com.example.moviemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val year: String,
    val rated: String?,
    val released: String?,
    val runtime: String?,
    val genre: String?,
    val director: String?,
    val writer: String?,
    val actors: String?,
    val plot: String?,
    val language: String?,
    val country: String?,
    val awards: String?,
    val imdbID: String?,
    val imdbRating: String?
) {
    companion object {
        fun fromJson(json: JSONObject): Movie? {
            return try {
                Movie(
                    title = json.getString("Title"),
                    year = json.getString("Year"),
                    rated = json.optString("Rated", "N/A"),
                    released = json.optString("Released", "N/A"),
                    runtime = json.optString("Runtime", "N/A"),
                    genre = json.optString("Genre", "N/A"),
                    director = json.optString("Director", "N/A"),
                    writer = json.optString("Writer", "N/A"),
                    actors = json.optString("Actors", "N/A"),
                    plot = json.optString("Plot", "N/A"),
                    language = json.optString("Language", "N/A"),
                    country = json.optString("Country", "N/A"),
                    awards = json.optString("Awards", "N/A"),
                    imdbID = json.optString("imdbID", ""),
                    imdbRating = json.optString("imdbRating", "N/A")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun toString(): String {
        return "Title: $title\n" +
                "Year: $year\n" +
                "Rated: $rated\n" +
                "Released: $released\n" +
                "Runtime: $runtime\n" +
                "Genre: $genre\n" +
                "Director: $director\n" +
                "Writer: $writer\n" +
                "Actors: $actors\n" +
                "Plot: $plot"
    }
}