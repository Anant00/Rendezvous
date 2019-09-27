package com.nasa.rendezvous.model.localdatabasemodel


import androidx.room.*
import com.nasa.rendezvous.model.NasaImages
import io.reactivex.Flowable


@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<NasaImages>)

    @Update
    fun update(nasaImages: NasaImages)

    @Query("DELETE FROM images")
    fun deleteAll()

    @Query("SELECT * FROM images ORDER BY date DESC")
    fun getAllImagesFromDatabase(): Flowable<List<NasaImages>>

    @Query("SELECT * FROM images ORDER BY date DESC LIMIT 1")
    fun checkIfDataBaseUpdate(): List<NasaImages>

}