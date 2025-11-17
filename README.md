## Practical-7
AIM: Develop an Android application that retrieves person data in JSON format from an internet API and stores the retrieved data in an SQLite database.
Study: JSON Format, ListView, RecyclerView, HttpURLConnection, CoroutineScope

1. Create an application to create JSON URL for Contact which have field(id, Name(First Name, Last Name), Phone No, Address)

2. To Generate JSON Data, Refer: https://app.json-generator.com/

3. Create MainActivity according to below UI design.

4. Use link generated from website for JSON Data

5. Create Class Person with member Variables like id, Name, Phone No, Email Id, Address, Latitude, Longitude. This class should be inherited from Serializable class.

6. Generate JSON data format according to below image.

7. Use RecyclerView or ListView Adapter

8. Add Internet Permission in  Manifest file

9. Create Class HttpRequest for communicating with Web URL

📌 Overview

This Android application retrieves Person data in JSON format from an online API, parses it, stores it inside an SQLite database, and displays the stored data using RecyclerView.
The project demonstrates JSON handling, HTTP networking, database storage, and modern Android UI components.

🎯 Aim

Develop an Android application that:

Fetches JSON data from a URL generated using https://app.json-generator.com/

Parses JSON into a Person model

Stores the data into an SQLite (or Room) database

Displays the stored data using RecyclerView/ListView

Uses HttpURLConnection + Kotlin Coroutines for API calls

🧩 Features

✔ Internet API call
✔ JSON parsing
✔ SQLite data storage
✔ RecyclerView display
✔ Custom model class using Serializable
✔ HttpURLConnection networking
✔ CoroutineScope for background tasks

📐 JSON Structure

Create JSON from the website with this structure:

[
  {
    "id": 1,
    "name": {
      "first": "John",
      "last": "Doe"
    },
    "phone": "9876543210",
    "email": "john@example.com",
    "address": "New York",
    "latitude": 40.7128,
    "longitude": -74.0060
  }
]


Generate the URL and use it inside your app.

🧱 Person Model Class
data class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Serializable

🌐 Permissions

Add Internet permission in AndroidManifest.xml:

<uses-permission android:name="android.permission.INTERNET" />

🌍 HttpRequest Class

Handles communication with the web URL:

class HttpRequest {

    fun getData(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val result = reader.readText()

        reader.close()
        connection.disconnect()

        return result
    }
}

🔄 Fetching & Saving Data with Coroutines
CoroutineScope(Dispatchers.IO).launch {
    val jsonData = HttpRequest().getData(JSON_URL)
    val persons = parseJson(jsonData)

    database.personDao().insertAll(persons)

    withContext(Dispatchers.Main) {
        adapter.updateList(persons)
    }
}

🗃 SQLite (Room) Setup
DAO
@Dao
interface PersonDao {
    @Insert
    suspend fun insertAll(persons: List<Person>)

    @Query("SELECT * FROM person")
    suspend fun getAll(): List<Person>
}

📋 Displaying Data (RecyclerView)

Create person_item_view.xml

Bind name, phone, and address in onBindViewHolder()

Update recycler on data received

🖼 MainActivity UI Layout

Your layout must contain:

Toolbar

RecyclerView

FloatingActionButton for refresh/update

Example components:

AppBarLayout

MaterialToolbar

RecyclerView

FloatingActionButton

📁 Project Structure
app/
 ├── java/
 │   ├── MainActivity.kt
 │   ├── Person.kt
 │   ├── HttpRequest.kt
 │   ├── db/
 │   │   ├── AppDatabase.kt
 │   │   └── PersonDao.kt
 │   ├── adapter/
 │   │   └── PersonAdapter.kt
 │
 └── res/
     ├── layout/activity_main.xml
     ├── layout/person_item_view.xml
     ├── drawable/
     └── values/

▶️ How It Works

App loads → Toolbar & RecyclerView visible

Coroutine fetches JSON from the given URL

JSON parsed into Person objects

Objects saved into SQLite database

RecyclerView updates and shows the list

🧪 Testing Checklist

✔ JSON URL opens in browser
✔ Valid JSON format
✔ Logs show API response
✔ SQLite stores all records
✔ RecyclerView displays all persons

📌 Conclusion

This project demonstrates:

Networking using HttpURLConnection

Kotlin Coroutines for async tasks

JSON parsing

SQLite/Room Database

RecyclerView usage

Data modeling with Serializable
