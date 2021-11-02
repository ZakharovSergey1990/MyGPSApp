package com.example.mygpsapp

import android.app.Application
import android.location.LocationListener
import com.example.mygpsapp.service.LocationService
import com.example.mygpsapp.service.LocationServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MyModule {

    @Singleton
    @Binds
    abstract fun bindLocationService(myLocationServiceImpl: LocationServiceImpl): LocationService

}