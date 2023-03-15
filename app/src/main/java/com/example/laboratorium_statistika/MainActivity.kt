package com.example.laboratorium_statistika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Xml
import com.example.laboratorium_statistika.databinding.ActivityMainBinding
import com.example.laboratorium_statistika.model.Module
import org.xmlpull.v1.XmlPullParser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}