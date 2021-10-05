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




}