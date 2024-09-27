package com.devmasterteam.tasks.service.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity (tableName = "Priority")
class PriorityModel {
    @SerializedName("id")
    @ColumnInfo(name= "id")
    @PrimaryKey
    var id: Int = 0
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String = ""
}