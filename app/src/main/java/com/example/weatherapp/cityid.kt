package com.example.weatherapp

import com.google.gson.annotations.SerializedName



class cid {

    @SerializedName("cd")
    var cd:List<city>?=null

    class city {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("country")
        var country: String? = null

        @SerializedName("state")
        var state: String? = null

        @SerializedName("coord")
        var coord: cooord? = null

        class cooord() {
            @SerializedName("lon")
            var lon: Float? = null

            @SerializedName("lat")
            var lat: Float? = null
        }
    }


}