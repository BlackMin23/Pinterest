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

class ElegirTipoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_tipo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }
    private fun initUI() {
        val btnSubirN = findViewById<Button>(R.id.btnPNormal)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnSubirN.setOnClickListener {
            irAgregar()
        }

        bottomNavigation.selectedItemId = R.id.nav_add

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    // Ya estamos en ElegirTipo, no hacer nada más
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun irAgregar() {
        startActivity(Intent(this, PubliNAddEditActivity::class.java))
    }

}