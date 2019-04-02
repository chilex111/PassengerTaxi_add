package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.LiveData
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.ExampleEntity

class ExampleViewModel(application: Application, private val mainRepository: Repository) : BaseViewModel(application) {

    fun getAll(): LiveData<List<ExampleEntity>> = mainRepository.getAll()

    fun addNew(mainEntity: ExampleEntity) = mainRepository.addNew(mainEntity)

}