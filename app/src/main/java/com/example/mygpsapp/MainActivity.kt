package com.example.mygpsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mygpsapp.entity.LocationState
import com.example.mygpsapp.ui.theme.MyGPSAppTheme
import dagger.hilt.android.AndroidEntryPoint
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.ui.MapUI
import java.lang.Math.PI
import java.lang.Math.tan
import kotlin.math.ln


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyGPSAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MyGpsPage(hiltViewModel())
                }
            }
        }
    }

}

@Composable
fun MyGpsPage(vm: MainViewModel) {
    val state by vm.state.collectAsState(initial = null)
    val locationState by vm.getMyLocation().collectAsState(initial = LocationState("name", 0f, 0f))

    Log.d("HttpTilesDemo", "longitude = ${locationState?.longitude}, latitude = ${locationState?.latitude}")

    var zoom by remember { mutableStateOf(0.1f) }
    var x = getXCoordinate(locationState.longitude)
    var y = getYCoordinate(locationState.latitude);

    Log.d("HttpTilesDemo", "x = ${x}, y = $y")
    state?.scale = zoom
    state?.addMarker(id = "id", x = x, y = y) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_place_24),
            contentDescription = ""
        )
    }

    LaunchedEffect(zoom) { state?.scrollTo(x, y) }
    LaunchedEffect(x) { state?.scrollTo(x, y) }

    state?.let { MapUI(Modifier, state = it) }

    Row() {
        Button(onClick = {
            zoom -= 0.2f
        }, modifier = Modifier.padding(10.dp)) {
            Text(text = "-")
        }
        Button(onClick = {
            zoom += 0.2f
        }, modifier = Modifier.padding(10.dp)) {
            Text(text = "+")
        }
    }
}

fun getXCoordinate(longitude: Float): Double{
    val mapWidth = 1.0
    return (longitude + 180) * (mapWidth / 360)
}

fun getYCoordinate(latitude: Float): Double{
    val mapHeight = 1.0
    val mapWidth = 1.0
    val latRad = latitude * PI / 180;
    val mercN = ln(tan((PI / 4) + (latRad / 2)));
   return  (mapHeight / 2) - (mapWidth * mercN / (2 * PI))
}