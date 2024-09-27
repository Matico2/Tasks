package com.devmasterteam.tasks.service.repository.local

import android.content.Context
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.remote.PriorityService
import com.devmasterteam.tasks.service.remote.RetrofityClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(val context: Context) {
    
    val remote = RetrofityClient.getService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()
    fun list(listener: APIListener<List<PriorityModel>>) {
        val call = remote.list()
        call.enqueue(object : Callback<List<PriorityModel>> {
            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                if (response.code() == TaskConstants.HTTP.SUCCESS) {
                    response.body()?.let { listener.onSuccess(it) }
                } else {
                    listener.onFailure(failResponse(response.errorBody()!!.string()))
                }
                
            }
            
            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {
                listener.onFailure("Um erro inesperado ocorreu. Tente novamente mais tarde.")
            }
            
        })
        
        
    }
    fun save(list: List<PriorityModel>){
        database.save(list)
        database.clear()
    }
    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)
    }
}