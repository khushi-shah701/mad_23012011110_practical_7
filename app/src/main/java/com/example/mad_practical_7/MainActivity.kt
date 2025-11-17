package com.example.mad_practical_7

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_practical_7.db.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val personList = ArrayList<Person>()
    lateinit var personsRecycleAdapter: PersonAdapter
    lateinit var db: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
        personsRecycleAdapter = PersonAdapter(this, personList)
        recyclerView.adapter = personsRecycleAdapter
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            networkDb()
        }
        db = DatabaseHelper(applicationContext)
        getPersonDetailsFromSqliteDb()
    }

    private fun getPersonDetailsFromSqliteDb() {
        val size = personList.size
        personList.clear()
        personsRecycleAdapter.notifyItemRangeRemoved(0,size)
        try {
            personList.addAll(db.allPersons)
            personsRecycleAdapter.notifyItemRangeInserted(0,personList.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Toast.makeText(this@MainActivity,"Fetched details from DB",Toast.LENGTH_SHORT).show()
    }

    fun deletePerson(position: Int){
        val deletedPerson = personList[position]
        db.deletePerson(deletedPerson)
        personList.removeAt(position)
        personsRecycleAdapter.notifyItemRemoved(position)
        Toast.makeText(this@MainActivity,"At $position, ${deletedPerson.name} Deleted",Toast.LENGTH_SHORT).show()
    }
    private fun networkDb(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpRequest().makeServiceCall(
                    reqUrl = "https://api.json-generator.com/templates/fXurFOiiq23j/data",
                    token = "easp1qqg6mn87spu8gx4n0bvspxo88oxo14x6gpw"
                )

                withContext(Dispatchers.Main) {
                    try {
                        if (data != null) {
                            runOnUiThread {
                                getPersonDetailsFromJson(data)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPersonDetailsFromJson(sJson: String) {
        val size = personList.size
        personList.clear()
        personsRecycleAdapter.notifyItemRangeRemoved(0,size)
        try {
            val jsonArray = JSONArray(sJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray[i] as JSONObject
                val person = Person(jsonObject)
                personList.add(person)
                try {
                    if (db.getPerson(person.id) != null) {
                        db.updatePerson(person)
                    } else {
                        db.insertPerson(person)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            personsRecycleAdapter.notifyItemRangeInserted(0,personList.size)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Toast.makeText(this@MainActivity,"Fetched details from JSON",Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_db -> {
                getPersonDetailsFromSqliteDb()
                true
            }

            R.id.action_newdb -> {
                networkDb()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}