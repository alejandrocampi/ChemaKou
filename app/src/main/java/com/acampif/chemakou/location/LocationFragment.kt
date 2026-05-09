package com.acampif.chemakou

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.acampif.chemakou.databinding.FragmentLocationBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class LocationFragment :
    Fragment(R.layout.fragment_location),
    LectorPantalla,
    OnMapReadyCallback {

    private lateinit var binding: FragmentLocationBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLocationBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("voz_settings", 0)

        textToSpeech = TextToSpeech(requireContext()) {

            val idioma = prefs.getString("idioma", "Español")

            val locale = if (idioma == "Inglés") {
                Locale.US
            } else {
                Locale("es", "ES")
            }

            textToSpeech.language = locale

            val velocidad = prefs.getFloat("speed", 1.0f)
            textToSpeech.setSpeechRate(velocidad)
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        obtenerUbicacion()
    }

    private fun obtenerUbicacion() {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            binding.txtNoLocation.text =
                "Permiso de ubicación no concedido"

            return
        }

        googleMap.isMyLocationEnabled = true

        googleMap.uiSettings.isMyLocationButtonEnabled = true

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {

                val latLng = LatLng(
                    location.latitude,
                    location.longitude
                )

                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Tu ubicación")
                )

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                )

                val geocoder =
                    Geocoder(requireContext(), Locale.getDefault())

                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                if (!addresses.isNullOrEmpty()) {

                    val address = addresses[0]

                    val direccion =
                        address.getAddressLine(0)

                    val ciudad =
                        address.locality ?: "Ciudad desconocida"

                    binding.txtNoLocation.visibility = View.GONE
                    binding.locationInfoContent.visibility = View.VISIBLE

                    binding.txtAddress.text = direccion
                    binding.txtCity.text = ciudad

                    binding.txtNearbyPlaces.text =
                        "Ubicación detectada correctamente"

                    hablar("Te encuentras en $direccion")
                }

            } else {

                binding.txtNoLocation.text =
                    "No se pudo detectar la ubicación"
            }
        }
    }

    override fun leerPantalla() {

        val direccion =
            binding.txtAddress.text.toString()

        val mensaje = if (direccion.isEmpty()) {
            "Obteniendo ubicación"
        } else {
            "Tu ubicación actual es $direccion"
        }

        hablar(mensaje)
    }

    private fun hablar(texto: String) {

        textToSpeech.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.shutdown()
    }
}