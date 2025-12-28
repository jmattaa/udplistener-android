package com.jmattaa.udplistener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jmattaa.udplistener.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Volatile private var listening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stoplisten.isEnabled = false
    }

    external fun listenUDP(port: Int): String

    companion object {
        init {
            System.loadLibrary("udplistener")
        }
    }

    fun listenbtnClick(view: View) {
        binding.textView.text = ""
        val port = binding.portInput.text.toString().toIntOrNull() ?: return

        listening = true
        binding.stoplisten.isEnabled = true

        Thread {
            while (listening) {
                val msg = listenUDP(port)
                runOnUiThread {
                    binding.textView.append(msg + "\n")
                }
            }
            runOnUiThread {
                binding.stoplisten.isEnabled = false
            }
        }.start()
    }


    public fun stoplistenClick(view: View) {
        listening = false
        binding.stoplisten.isEnabled = false
    }
}
