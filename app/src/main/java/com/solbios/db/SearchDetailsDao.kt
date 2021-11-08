package com.solbios.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.solbios.db.entities.SearchData
import com.solbios.db.entities.SearchEntityRoot

@Dao
interface SearchDetailsDao {

    @Insert
    suspend fun addDetails(searchData: List<SearchData>)
    @Insert
    suspend fun searchAddDetails(searchData: SearchData)

    @Query("SELECT * FROM searchdata")
   suspend   fun getAllRecentSearch(): List<SearchData?>?

   @Query("DELETE FROM searchdata")
     suspend  fun deleteAll()

}