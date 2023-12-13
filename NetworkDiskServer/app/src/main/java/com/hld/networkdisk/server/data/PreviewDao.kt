package com.hld.networkdisk.server.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Dao
interface PreviewDao {

    @Query("SELECT * from FilePreviewData WHERE fileAbsolutePath = :absolutePath")
    fun query(absolutePath: String): Bean?

    @Query("SELECT * from FilePreviewData WHERE fileAbsolutePath in (:paths)")
    fun query(paths: Array<String>): List<Bean>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bean: Bean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list:List<Bean>)

    @Entity(tableName = "FilePreviewData")
    data class Bean(
        @PrimaryKey val fileAbsolutePath: String, // 绝对路径
        val previewImageBase64: String // 预览图base64
    )
}