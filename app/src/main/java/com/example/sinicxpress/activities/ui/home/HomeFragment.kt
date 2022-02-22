package com.example.sinicxpress.activities.ui.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sinicxpress.R
//import com.example.sinicxpress.activities.R
//import com.example.sinicxpress.activities.databinding.FragmentHomeBinding
import com.example.sinicxpress.databinding.FragmentHomeBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.location.LocationRequest as LocationRequest

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
    //Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

//    online system
    private lateinit var onlineRef:DatabaseReference
    private lateinit var currentUserRef:DatabaseReference
    private lateinit var ridersLocationRef:DatabaseReference
    private lateinit var geoFire:GeoFire

    private val onlineValueEventListener = object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists())
                currentUserRef.onDisconnect().removeValue()
        }

        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(mapFragment.requireView(),error.message,Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        geoFire.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
        onlineRef.removeEventListener(onlineValueEventListener)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        registerOnlineSystem()
    }

    private fun registerOnlineSystem() {
       onlineRef.addValueEventListener(onlineValueEventListener)
    }

//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()


        mapFragment = childFragmentManager
            .findFragmentById( R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        return root
    }

    private fun init() {


        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected")
        ridersLocationRef = FirebaseDatabase.getInstance().getReference(Common.RIDERS_LOCATION_REFERENCE)
        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.RIDERS_LOCATION_REFERENCE).child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        geoFire = GeoFire(ridersLocationRef)

        registerOnlineSystem()

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val newPos= LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos,18f))

//                Update Location
                geoFire.setLocation(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    GeoLocation(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude)
                ){ key:String?, error:DatabaseError? ->
                    if(error !=null)
                        Snackbar.make(mapFragment.requireView(),error.message,Snackbar.LENGTH_LONG).show()
                    else
                        Snackbar.make(mapFragment.requireView(),"You're online!",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled=true

        setUpMap()

        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.uber_maps_style))
            if (!success)
                Log.e("SX_ERROR", "Style parsing error")
        }catch (e:Resources.NotFoundException)
        {
            Log.e("SX_ERROR", e.message!!)
        }

//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)

            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->

            if (location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude,location.longitude)
                placeMarkerOnMap(currentLatLong)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))



            }

        }
    }




    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("currentLatLong")
        mMap.addMarker(markerOptions)

    }

    override fun onMarkerClick(marker: Marker) = false
    override fun onMapClick(currentLatLong: LatLng) {
        mMap.setOnMyLocationClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnMyLocationClickListener
            }
            fusedLocationProviderClient.lastLocation
                .addOnFailureListener { e->
                    Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { location ->
                    val userLatLng = LatLng(location.latitude,location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,18f))
                }
            true
        }
    }


}


