package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val APIkey = "87afbfc5e78daa067047fa4f5ee18ba2"
    lateinit var sel: Spinner
    var cities= ArrayList<String>()
    var countries=ArrayList<String>()
    var ctid=ArrayList<Int>()
    var sell:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sel=findViewById(R.id.spncity)
        var myJson = this.jsonToClass<cid>(R.raw.citylist)

         for (i in myJson.cd!!){
             cities.add(i.name!!)
             countries.add(i.country!!)
             ctid.add(i.id!!)
         }
        var jasonread: GsonBuilder =GsonBuilder()
        jasonread.setLenient()
        var a=jasonread.create()
        var json=a.fromJson(URL("https://api.openweathermap.org/data/2.5/weather?zip=${ctid[sell]}&units=metric&appid=$APIkey").readText(Charsets.UTF_8), tcity::class.java)
        json.



         fun requestAPI(){
            println("CITY: ${cid.city.name}")
            CoroutineScope(Dispatchers.IO).launch {
                updateStatus(-1)
                val data = async {
                    fetchWeatherData()
                }.await()
                if(data.isNotEmpty()){
                    updateWeatherData(data)
                    updateStatus(0)
                }else{
                    updateStatus(1)
                }
            }
        }



    }

    private suspend fun updateWeatherData(result: String){
        withContext(Dispatchers.Main){
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

            val lastUpdate:Long = jsonObj.getLong("dt")
            val lastUpdateText = "Updated at: " + SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH).format(Date(lastUpdate*1000))
            val currentTemperature = main.getString("temp")
            val temp = try{
                currentTemperature.substring(0, currentTemperature.indexOf(".")) + "°C"
            }catch(e: Exception){
                currentTemperature + "°C"
            }
            val minTemperature = main.getString("temp_min")
            val tempMin = "Low: " + minTemperature.substring(0, minTemperature.indexOf("."))+"°C"
            val maxTemperature = main.getString("temp_max")
            val tempMax = "High: " + maxTemperature.substring(0, maxTemperature.indexOf("."))+"°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise:Long = sys.getLong("sunrise")
            val sunset:Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")

            val address = jsonObj.getString("name")+", "+sys.getString("country")

            findViewById<TextView>(R.id.tvAddress).text = address
            findViewById<TextView>(R.id.tvAddress).setOnClickListener {
                rlZip.isVisible = true
            }
            findViewById<TextView>(R.id.tvupdate).text =  lastUpdateText
            findViewById<TextView>(R.id.tvStatus).text = weatherDescription.capitalize(Locale.getDefault())
            findViewById<TextView>(R.id.tvTemperature).text = temp
            findViewById<TextView>(R.id.tvTempMin).text = tempMin
            findViewById<TextView>(R.id.tvTempMax).text = tempMax
            findViewById<TextView>(R.id.tvSunrise).text = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(sunrise*1000))
            findViewById<TextView>(R.id.tvSunset).text = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(sunset*1000))
            findViewById<TextView>(R.id.tvWind).text = windSpeed
            findViewById<TextView>(R.id.tvPressure).text = pressure
            findViewById<TextView>(R.id.tvHumidity).text = humidity
            findViewById<LinearLayout>(R.id.llRefresh).setOnClickListener { requestAPI() }
        }
    }



    private fun fetchWeatherData(): String{
        var response = ""
        try {
            response = URL("https://api.openweathermap.org/data/2.5/weather?zip=${cid.city}&units=metric&appid=$APIkey")
                .readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("Error: $e")
        }
        return response
    }


    private suspend fun updateStatus(state: Int){
//        states: -1 = loading, 0 = loaded, 1 = error
        withContext(Dispatchers.Main){
            when{
                state < 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.mscreen).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.llErrorContainer).visibility = View.GONE
                }
                state == 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.mscreen).visibility = View.VISIBLE
                }
                state > 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.llErrorContainer).visibility = View.VISIBLE
                }
            }
        }
    }
}

}


//this function for easy reuse later to read json and convert it to class
//invoked with context.jsonToClass<MyObject>(R.raw.my_object)
//inline fun <reified T> Context.jsonToClass(@RawRes resourceId: Int): T =
//    Gson().fromJson(resources.openRawResource(resourceId).bufferedReader().use { it.readText() }, T::class.java)
inline fun <reified T> Context.jsonToClass(@RawRes resourceId: Int): T =
    Gson().fromJson(resources.openRawResource(resourceId).bufferedReader().use { it.readText() }, T::class.java)