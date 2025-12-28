package com.jmattaa.udplistener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.text.Html
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
        binding.textView.movementMethod = ScrollingMovementMethod()
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
                    // Parse the message and add HTML colors
                    val coloredMsg = formatMessageWithColors(msg)
                    binding.textView.append(Html.fromHtml(coloredMsg, Html.FROM_HTML_MODE_COMPACT))
                    binding.textView.append("\n")
                }
            }
            runOnUiThread {
                binding.stoplisten.isEnabled = false
            }
        }.start()
    }


    private fun formatMessageWithColors(msg: String): String {
    // Use regex to parse the formatted message and add HTML colors
    val timestampPattern = "\\[(.*?)\\]".toRegex()
    val receivedFromPattern = "Received from".toRegex()
    val ipPortPattern = "(.*?):(\\d+)".toRegex()
    
    var result = msg
    
    // Color timestamp cyan
    result = result.replace(timestampPattern) { match ->
        "<font color='#00FFFF'><b>[${match.groupValues[1]}]</b></font>"
    }
    
    // Color "Received from" green
    result = result.replace(receivedFromPattern) { 
        "<font color='#00FF00'><b>Received from</b></font>"
    }
    
    // Color IP yellow and port blue
    result = result.replace(ipPortPattern) { match ->
        "<font color='#FFFF00'><b>${match.groupValues[1]}</b></font>:<font color='#0000FF'><b>${match.groupValues[2]}</b></font>"
    }
    
    // Color the actual message data magenta (everything after the IP:port line)
    val lines = result.split("\n")
    if (lines.size > 1) {
        val metadataLine = lines[0]
        val dataLine = lines.subList(1, lines.size).joinToString("\n")
        result = metadataLine + "\n<font color='#FF00FF'><b>$dataLine</b></font>"
    }
    
    return result
}

    public fun stoplistenClick(view: View) {
        listening = false
        binding.stoplisten.isEnabled = false
    }
}
