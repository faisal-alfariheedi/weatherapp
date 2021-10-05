package com.example.weatherapp

import com.google.gson.annotations.SerializedName

class tcity {
    @SerializedName("name")
    var name: String?=null
    @SerializedName("main")
    var main: Main?=null

    class Main{
        @SerializedName("temp")
        var temp:Float?=null
        @SerializedName("temp_min")
        var temp_min:Float?=null
        @SerializedName("temp_max")
        var temp_max:Float?=null
        @SerializedName("pressure")
        var pressure:Float?=null
        @SerializedName("humidity")
        var humidity:Float?=null
    }
    @SerializedName("wind")
    var wind:Wind?=null
    class Wind{
        @SerializedName("speed")
        var speed: Float?=null
    }
    @SerializedName("weather")
    var weather: Array<rand>? =null
    class rand{
        @SerializedName("description")
        var description: String?=null
    }
    @SerializedName("sys")
    var sys: SYS?=null
    class SYS{
        @SerializedName("sunrise")
        var sunrise:Int?=null
        @SerializedName("sunset")
        var sunset:Int?=null
    }
    @SerializedName("dt")
    var dt:Int?=null


}