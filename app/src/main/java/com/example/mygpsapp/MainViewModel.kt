package com.example.mygpsapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mygpsapp.entity.LocationState
import com.example.mygpsapp.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.api.shouldLoopScale
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.state.MapState
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationService: LocationService
): ViewModel() {

    private val tileStreamProvider = makeTileStreamProvider()

    val level = 17
    val width = 256 * 2.0.pow(level).toInt()
    val height = 256 * 2.0.pow(level).toInt()

    var state= MutableStateFlow(
        MapState(level, width, height, tileStreamProvider, workerCount = 16).apply {
        //    scale = 0.1f
            shouldLoopScale = false
        }
    )

    fun getMyLocation(): Flow<LocationState> {
        return locationService.getLocation()
    }

private fun makeTileStreamProvider() =
    TileStreamProvider { row, col, zoomLvl ->
        try {
            Log.d("makeTileStreamProvider", " row = $row, col= $col, zoomLvl= $zoomLvl")
            val url = URL("https://a.tile.openstreetmap.de/${zoomLvl+1}/${col}/${row}.png")
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            BufferedInputStream(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}