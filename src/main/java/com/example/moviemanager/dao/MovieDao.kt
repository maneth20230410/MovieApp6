package com.example.moviemanager.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviemanager.model.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE title LIKE :title")
    suspend fun getMovieByTitle(title: String): Movie?

    @Query("SELECT * FROM movies WHERE title = :title AND year = :year")
    suspend fun getMovieByTitleYear(title: String, year: String): Movie?

    @Query("SELECT COUNT(*) FROM movies WHERE title = :title AND year = :year")
    suspend fun countByTitleYear(title: String, year: String): Int

    @Query("SELECT * FROM movies WHERE actors LIKE '%' || :actorName || '%'")
    suspend fun getMoviesByActor(actorName: String): List<Movie>
}