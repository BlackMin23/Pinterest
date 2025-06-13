package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pinterest.pinterestfirebase.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {
        val btnMisPubliN = findViewById<Button>(R.id.btnMisPubliN)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.selectedItemId = R.id.nav_profile

        btnMisPubliN.setOnClickListener {
            irAgregar()
        }

        // funcion para ir a agregar desde el nav
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_add -> {
                    startActivity(Intent(this, ElegirTipoActivity ::class.java))
                    true
                }
                else -> false
            }
        }

    }

    //boton para agregar una publicacion normal
    private fun irAgregar() {
        startActivity(Intent(this, PubliNListActivity::class.java))
    }
}