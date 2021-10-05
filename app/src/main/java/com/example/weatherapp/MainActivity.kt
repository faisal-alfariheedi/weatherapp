package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val APIkey = "##"
    lateinit var sel: Spinner
    var cities= ArrayList<String>()
    var countries=ArrayList<String>()
    var ctid=ArrayList<Int>()
    var sell:Int = 0
    var adrli =arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sel=findViewById(R.id.spncity)

        var myJson = this.jsonToClass<cid>(R.raw.citylist)

         for (i in myJson.cd!!){
             cities.add(i.name!!)
             countries.add(i.country!!)
             ctid.add(i.id!!)
             adrli.add("${i.name},${i.country}")
         }
        sel.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, adrli)
        sel.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                sell= position
                APIrequest()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                null
            }
        }

    }

    fun APIrequest() {

        CoroutineScope(Dispatchers.IO).launch {
            var jasonread: GsonBuilder = GsonBuilder()
            jasonread.setLenient()
            var a = jasonread.create()
            updateStatus(-1)
            val data = async {
                a.fromJson(
                    URL("https://api.openweathermap.org/data/2.5/weather?id=${ctid[sell]}&units=metric&appid=$APIkey").readText(
                        Charsets.UTF_8
                    ), tcity::class.java
                )
            }.await()
            if (data.name?.isNotEmpty() == true) {
                updateWeatherData(data)
                updateStatus(0)
            } else {
                updateStatus(1)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    suspend fun updateWeatherData(result: tcity){
        withContext(Dispatchers.Main){
/*
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

 */
            findViewById<TextView>(R.id.tvupdate).text =  "Updated at: "+SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.ENGLISH).format(Date(
                result.dt?.toLong()?.times(1000)!!))
            findViewById<TextView>(R.id.tvStatus).text = result.weather?.get(0)?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            findViewById<TextView>(R.id.tvTemperature).text = (result.main?.temp.toString()).substring(0,result.main?.temp.toString().indexOf('.'))+"°C"
            findViewById<TextView>(R.id.tvTempMin).text = (result.main?.temp_min.toString()).substring(0,result.main?.temp_min.toString().indexOf('.'))+"°C"
            findViewById<TextView>(R.id.tvTempMax).text = (result.main?.temp_max.toString()).substring(0,result.main?.temp_max.toString().indexOf('.'))+"°C"
            findViewById<TextView>(R.id.tvSunrise).text = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(result.sys?.sunrise?.times(1000)!!.toLong()))
            findViewById<TextView>(R.id.tvSunset).text = SimpleDateFormat("hh:mm a",
                Locale.ENGLISH).format(Date(result.sys?.sunset?.times(1000)!!.toLong()))
            findViewById<TextView>(R.id.tvWind).text = result.wind?.speed.toString()
            findViewById<TextView>(R.id.tvPressure).text = result.main?.pressure.toString()
            findViewById<TextView>(R.id.tvHumidity).text = result.main?.humidity.toString()
            findViewById<LinearLayout>(R.id.llRefresh).setOnClickListener { APIrequest() }
        }
    }



    private suspend fun updateStatus(i: Int) {
//        i: -1 = loading, 0 = loaded, 1 = error
        withContext(Dispatchers.Main){
            when{
                i < 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.mscreen).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.llErrorContainer).visibility = View.GONE
                }
                i == 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.GONE
                    findViewById<ConstraintLayout>(R.id.mscreen).visibility = View.VISIBLE
                }
                i > 0 -> {
                    findViewById<ProgressBar>(R.id.pbProgress).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.llErrorContainer).visibility = View.VISIBLE
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
