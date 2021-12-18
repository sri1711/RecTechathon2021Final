package com.asyncdevs.devforum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class competitiondetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitiondetails)

        findViewById<Button>(R.id.btn_createTeam).setOnClickListener {
            val intent = Intent(this,CreateTeam::class.java)
            startActivity(intent)
        }
    }
}