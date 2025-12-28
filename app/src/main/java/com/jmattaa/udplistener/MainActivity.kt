package com.jmattaa.udplistener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jmattaa.udplistener.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    external fun stringFromJNI(port: Int): Int

    companion object {
        init {
            System.loadLibrary("udplistener")
        }
    }

    public fun listenbtnClick(view: View) {
        binding.textView.text = 
            stringFromJNI(binding.portInput.text.toString().toIntOrNull() ?: 0)
            .toString()
    }
}
