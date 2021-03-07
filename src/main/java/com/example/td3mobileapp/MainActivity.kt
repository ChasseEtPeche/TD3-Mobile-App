package com.example.td3mobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart(){
        super.onStart()

        val off_line_button = findViewById<Button>(R.id.button)
        val mButton = findViewById<Button>(R.id.VALIDATE)
        val et = findViewById<EditText>(R.id.ID)

        FuelManager.instance.socketFactory = TLSSocketFactory()

        mButton.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            Fuel.get("https://60102f166c21e10017050128.mockapi.io/accounts/${et.text}")
                    .response { request, response, result ->
                        val (bytes, error) = result
                        if (bytes != null) {
                            val info = String(bytes).split(",").toTypedArray()
                            val userID = et.text.toString()
                            intent.putExtra("USER_ID", userID)
                            intent.putExtra("ACCOUNT_NAME", info[1].replace("\"", " "))
                            intent.putExtra("AMMOUNT", info[2].replace("\"", " "))
                            intent.putExtra("IBAN", info[3].replace("\"", " "))
                            intent.putExtra("CURRENCY", info[4].replace("\"", " "))
                            intent.putExtra("MODE", "ONLINE")

                            val fileName = "off_line.txt"
                            val fileOutputStream:FileOutputStream
                            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                            fileOutputStream.write(("$userID,").toByteArray())
                            fileOutputStream.write((info[1]+",").replace("\"", " ").toByteArray())
                            fileOutputStream.write((info[2]+",").replace("\"", " ").toByteArray())
                            fileOutputStream.write((info[3]+",").replace("\"", " ").toByteArray())
                            fileOutputStream.write((info[4]+",").replace("\"", " ").toByteArray())
                            startActivity(intent)
                        }
                    }
        }
        off_line_button.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("MODE", "OFFLINE")
            var fileInputStream: FileInputStream? = null
            fileInputStream = openFileInput("off_line.txt")
            val inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            val fileContent = stringBuilder.split(",").toTypedArray()
            intent.putExtra("USER_ID", fileContent[0])
            intent.putExtra("ACCOUNT_NAME", fileContent[1])
            intent.putExtra("AMMOUNT", fileContent[2])
            intent.putExtra("IBAN", fileContent[3])
            intent.putExtra("CURRENCY", fileContent[4])
            intent.putExtra("MODE", "ONLINE")
            startActivity(intent)
        }
    }
}
