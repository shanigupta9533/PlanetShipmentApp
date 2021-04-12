package com.virtual_market.planetshipmentapp.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.virtual_market.planetshipmentapp.Modal.ScanModel

@Dao
interface ScanDao {

    @Query("SELECT * FROM scan_table ORDER BY id DESC")
    fun getAllScanData(): List<ScanModel?>

    @Insert
    fun insertDiary(folderModel: ScanModel?)

    @Delete
    fun updateDiary(folderModel: ScanModel?)

    @Delete
    fun deleteDiary(folderModel: ScanModel?)

    @Query("DELETE FROM scan_table")
    fun deleteAllDiary()

}