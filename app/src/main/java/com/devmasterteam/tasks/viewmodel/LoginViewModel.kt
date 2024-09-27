package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.remote.RetrofityClient
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.local.PriorityRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val personRepository = PersonRepository(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)
    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login
    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser
    private val _failure = MutableLiveData<String>()
    val failure: LiveData<String> = _failure
    
    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSuccess(result: PersonModel) {
                securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, result.token)
                securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, result.personKey)
                securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, result.name)
                _login.value = ValidationModel()
            }
            
            override fun onFailure(message: String) {
                _login.value = ValidationModel(message)
            }
        })
    }
    
    fun verifyLoggedUser() {
        val token = securityPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val personKey = securityPreferences.get(TaskConstants.SHARED.PERSON_KEY)
        RetrofityClient.addHeaders(token, personKey)
        
        val logged = (token != "" && personKey != "")
        _loggedUser.value = logged
        if (!logged) {
            priorityRepository.list(object : APIListener<List<PriorityModel>> {
                override fun onSuccess(result: List<PriorityModel>) {
                    priorityRepository.save(result)
                }
                override fun onFailure(message: String) {
                }
                
            })
        }
    }
    
}