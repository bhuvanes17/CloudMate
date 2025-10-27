package com.example.cloudmate

import android.R.attr.data
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudmate.ui.theme.CloudMateTheme
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloudmate.Api.NetworkResponse
import com.example.cloudmate.Api.WeatherModel
import com.example.cloudmate.Weather.WeatherViewModel
import com.example.cloudmate.ui.theme.DarkBlue
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.jetbrains.annotations.Async
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.ColorFilter
import coil.compose.AsyncImage



val fontmedium = FontFamily(Font(R.font.poppins_medium))
val fontlight = FontFamily(Font(R.font.poppins_light))


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            CloudMateTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding())
                { innerPadding ->
                    Modifier.padding(innerPadding)
                    WeatherScreen(weatherViewModel)
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel)
{
    val systemUiController = rememberSystemUiController()
    var  city by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    val weatherResult = viewModel.weatherResult1.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    SideEffect {
        systemUiController.setStatusBarColor(
            color = DarkBlue,
            darkIcons = false
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(android.graphics.Color.parseColor("#0d044a")),
                    Color(android.graphics.Color.parseColor("#160717"))
                )
            )
        )
        .clickable(
            indication = null, // remove ripple effect
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }

    )

    {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = {
                        Text(
                            "Search My Location",
                            color = Color.Black,
                            fontFamily = fontlight
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.getData(city)
                            keyboardController?.hide()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.map),
                                contentDescription = "search",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                    },

                    modifier = Modifier
                        .width(380.dp)
                        .padding(top = 18.dp)
                )

            }

            Spacer(modifier = Modifier.height(10.dp))


            when(val result = weatherResult.value){
                is NetworkResponse.Error -> {
                    Text(text = result.message,color = Color.White)
                }
                NetworkResponse.Loading -> {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.height(44.dp).width(28.dp))
                }
                is NetworkResponse.Success-> {
                    //Text(text = result.data.toString())
                    WeatherDetail(result.data)
                }
                null -> {}
            }




        }
    }
}


@Composable
fun WeatherDetail(data : WeatherModel)
{
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom){
            Icon(
                painter = painterResource(R.drawable.map),
                contentDescription ="Location",
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp).padding(start = 20.dp)
            )
            Text(text = data.location.name, fontSize = 28.sp, fontFamily = fontmedium,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp))

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = data.location.country, fontSize = 20.sp,
                fontFamily = fontlight,
                color = Color.Gray)

        }
        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "${data.current.temp_c} ° C",
            fontSize = 30.sp,
            fontFamily = fontlight,
            color = Color.White)

        Spacer(modifier = Modifier.height(10.dp))


        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier.size(120.dp),
                model = "https:${data.current.condition.icon}"
                    .replace("64x64", "128x128"),
                contentDescription = "Condition icon",
                contentScale = ContentScale.Fit
            )
        }


        Spacer(modifier = Modifier.height(18.dp))


        Text(text = data.current.condition.text,
            fontSize = 25.sp,
            fontFamily = fontlight,
            color = Color.White)

        Spacer(modifier = Modifier.height(15.dp))

        Card {
            Column (modifier = Modifier.fillMaxWidth().
            background(colorResource(id = R.color.purple_700))
            ){
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround)
                {
                WeatherKeyvalue("Humidity",data.current.humidity)
                    WeatherKeyvalue("Wind Speed",data.current.wind_kph+" km/h")
                }
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround)
                {
                    WeatherKeyvalue("Uv",data.current.uv)
                    WeatherKeyvalue("Participation",data.current.precip_in+" mm")
                }
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround)
                {
                    val parts = data.location.localtime.split(" ")
                    WeatherKeyvalue("Local Date", parts[0])
                    WeatherKeyvalue("Local Time", parts[1])

                }
            }
        }
    }
}

@Composable
fun WeatherKeyvalue(key : String,value: String)
{
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = value, fontFamily = fontmedium,
            fontSize = 24.sp,
            color = Color.White)
        Text(text = key, fontFamily = fontlight,
            fontSize = 20.sp,
            color = Color.White)
    }
}




