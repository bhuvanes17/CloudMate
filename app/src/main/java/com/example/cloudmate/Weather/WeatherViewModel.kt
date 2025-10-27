package com.example.cloudmate.Weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudmate.Api.Constant
import com.example.cloudmate.Api.NetworkResponse
import com.example.cloudmate.Api.RetrofitInstance
import com.example.cloudmate.Api.WeatherModel
import kotlinx.coroutines.launch


class WeatherViewModel: ViewModel() {

    private  val weatherApi = RetrofitInstance.weatherApi
    private  val weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult1 : LiveData<NetworkResponse<WeatherModel>> = weatherResult

    fun getData(city : String)
    {
        Log.i("City name :",city)

        weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apikey,city)
                if (response.isSuccessful){

                    Log.i("Response :",response.body().toString())

                    response.body()?.let {
                        weatherResult.value = NetworkResponse.Success(it)
                    }
                }
                else
                {
                    Log.i("Error :",response.message())
                    weatherResult.value = NetworkResponse.Error("Failed to load the data")

                }

            }
            catch (e : Exception){
                weatherResult.value = NetworkResponse.Error("Failed to load the data")

            }

        }
    }
}