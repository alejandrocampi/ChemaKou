package com.acampif.chemakou

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.acampif.chemakou.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("voz_settings", 0)

        configurarSpinner(prefs)
        configurarVelocidad(prefs)
    }

    private fun configurarSpinner(prefs: android.content.SharedPreferences) {

        val idiomas = listOf("Español", "Inglés")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            idiomas
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val idiomaGuardado = prefs.getString("idioma", "Español")
        val posicion = idiomas.indexOf(idiomaGuardado)
        binding.spinnerLanguage.setSelection(posicion)

        binding.spinnerLanguage.setOnItemSelectedListener(object :
            android.widget.AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val idioma = idiomas[position]
                prefs.edit().putString("idioma", idioma).apply()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun configurarVelocidad(prefs: android.content.SharedPreferences) {

        val velocidadGuardada = prefs.getFloat("speed", 1.0f)

        binding.seekbarSpeed.progress = when {
            velocidadGuardada < 0.8f -> 0
            velocidadGuardada < 1.0f -> 1
            velocidadGuardada < 1.3f -> 2
            velocidadGuardada < 1.6f -> 3
            else -> 4
        }

        binding.seekbarSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val velocidad = when (progress) {
                    0 -> 0.5f
                    1 -> 0.8f
                    2 -> 1.0f
                    3 -> 1.3f
                    else -> 1.6f
                }

                prefs.edit().putFloat("speed", velocidad).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}