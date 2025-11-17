package com.example.mad_practical_7

import org.json.JSONObject
import java.io.Serializable

class Person(
    var id: String,
    var name: String,var email: String,
    var phone: String,
    var address: String
) : Serializable {

    constructor(jsonObject: JSONObject) : this("", "", "", "", "") {
        id = jsonObject.optString("id")
        email = jsonObject.optString("email")
        phone = jsonObject.optString("phone")

        val profileJson = jsonObject.optJSONObject("profile")
        if (profileJson != null) {
            name = profileJson.optString("name")
            address = profileJson.optString("address")
        }
    }

    override fun toString(): String {
        return "$name\n$email\n$phone\n$address"
    }
}
