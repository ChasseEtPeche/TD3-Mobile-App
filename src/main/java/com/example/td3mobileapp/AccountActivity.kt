package com.example.td3mobileapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import java.io.FileOutputStream
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class AccountActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val refresh = findViewById<Button>(R.id.button2)

        val userID = intent.getStringExtra("USER_ID")

        val account_idT = findViewById<TextView>(R.id.textView2)
        val accountNameT = findViewById<TextView>(R.id.textView4)
        val ammountT = findViewById<TextView>(R.id.textView5)
        val ibanT = findViewById<TextView>(R.id.textView6)
        val currencyT = findViewById<TextView>(R.id.textView7)

        account_idT.text = "ID: " + userID.toString()
        accountNameT.text=intent.getStringExtra("ACCOUNT_NAME")
        ammountT.text=intent.getStringExtra("AMMOUNT")
        ibanT.text=intent.getStringExtra("IBAN")
        currencyT.text=intent.getStringExtra("CURRENCY")

        try {
            FuelManager.instance.socketFactory = TLSSocketFactory()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        refresh.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            Fuel.get("https://60102f166c21e10017050128.mockapi.io/accounts/$userID")
                    .response { request, response, result ->
                        val (bytes, error) = result
                        if (bytes != null) {
                            val info = String(bytes).split(",").toTypedArray()
                            intent.putExtra("USER_ID", userID)
                            intent.putExtra("ACCOUNT_NAME", info[1].replace("\"", " "))
                            intent.putExtra("AMMOUNT", info[2].replace("\"", " "))
                            intent.putExtra("IBAN", info[3].replace("\"", " "))
                            intent.putExtra("CURRENCY", info[4].replace("\"", " "))
                            intent.putExtra("MODE", "ONLINE")
                            val fileName = "off_line.txt"
                            val fileOutputStream: FileOutputStream
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
    }
}