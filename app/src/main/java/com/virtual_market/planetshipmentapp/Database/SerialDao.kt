package com.virtual_market.planetshipmentapp.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel

@Dao
interface SerialDao {

    @Query("SELECT * FROM serial_table ORDER BY ware_house DESC")
    fun getAllScanData(): List<SerialProductListModel>

    @Insert
    fun insertDiary(folderModel: List<SerialProductListModel>?)

    @Delete
    fun updateDiary(folderModel: SerialProductListModel?)

    @Delete
    fun deleteDiary(folderModel: SerialProductListModel?)

    @Query("DELETE FROM serial_table")
    fun deleteAllDiary()

}