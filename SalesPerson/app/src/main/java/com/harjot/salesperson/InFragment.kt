package com.harjot.salesperson

import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harjot.salesperson.databinding.DialogLayoutBinding
import com.harjot.salesperson.databinding.FragmentInBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InFragment : Fragment(),Interfaces {
    val binding by lazy {
        FragmentInBinding.inflate(layoutInflater)
    }
    private var auth: FirebaseAuth = Firebase.auth
    lateinit var mainScreenActivity: MainScreenActivity
    var arrayList = ArrayList<Model>()
    lateinit var inAdapter: InAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val permissionRequestCode = 1000
    var pgBar: ProgressBar?=null

    private var collectionName = "SalesPerson"
    private var database = FirebaseFirestore.getInstance()
    var userModel = Model()
    val userList = mutableListOf<Model>()


    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val currentTime = LocalTime.now()
    val formattedTime = currentTime.format(formatter)
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-YYYY")
    var date = LocalDate.now()
    val formattedDate = date.format(dateFormatter)

    var currentLoc:String?= null
    var currentLat:String?= null
    var currentLong:String?= null

//    val sdf = SimpleDateFormat("hh:mm a")
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenActivity = activity as MainScreenActivity
        mainScreenActivity.pgBar?.visibility = View.VISIBLE

        inAdapter = InAdapter(arrayList,this)
        binding.rvList.layoutManager = LinearLayoutManager(mainScreenActivity)
        binding.rvList.adapter = inAdapter

        arrayList.clear()
        database.collection(collectionName)
            .whereEqualTo("id",auth.currentUser?.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (snapshot in snapshots!!.documentChanges) {
                    val userModel = convertObject(snapshot.document)

                    when (snapshot.type) {
                        DocumentChange.Type.ADDED -> {
                            mainScreenActivity.pgBar?.visibility = View.GONE
                            userModel?.let { arrayList.add(it) }
                            Log.e(ContentValues.TAG, "userModelList ${arrayList.size}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            mainScreenActivity.pgBar?.visibility = View.GONE
                            userModel?.let {
                                var index = getIndex(userModel)
                                if (index > -1) {
                                    arrayList.set(index, it)
                                }
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            mainScreenActivity.pgBar?.visibility = View.GONE
                            userModel?.let {
                                var index = getIndex(userModel)
                                if (index > -1) {
                                    arrayList.removeAt(index)
                                }
                            }
                        }
                    }
                }
                inAdapter.notifyDataSetChanged()
            }
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainScreenActivity)

        if (checkPermission()){
            getLastLocation()
        }else{
            requestPermission()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAdd.setOnClickListener {
            dialog()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            permissionRequestCode->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLastLocation()
                }else{
                    Toast.makeText(mainScreenActivity, "Location Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun checkPermission():Boolean{
        return ActivityCompat.checkSelfPermission(mainScreenActivity,
            android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }
    fun requestPermission(){
        ActivityCompat.requestPermissions(
            mainScreenActivity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),
            permissionRequestCode
        )
    }
    fun getLastLocation(){
        pgBar?.visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(mainScreenActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainScreenActivity,android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                pgBar?.visibility = View.GONE
                if (location!=null){
                    var userLong = location.longitude
                    var userLat = location.latitude

                    Log.e("TAG", "address: $userLat $userLong", )
                    var address = getCompleteAddressString(userLat,userLong)
                    currentLoc = address
                    currentLat = userLat.toString()
                    currentLong = userLong.toString()
//                    binding.tvLocation.text=address.toString()
//                    binding.tvLatitude.text=userLat.toString()
//                    binding.tvLongitude.text=userLong.toString()

                }
            }
    }
    fun getCompleteAddressString(latitude:Double,longitde:Double):String{
        val geocoder = Geocoder(mainScreenActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude,longitde,1)
            if (addresses!=null && addresses.isNotEmpty()){
                val address = addresses[0]

                val addressString = address.getAddressLine(0)

                val placeIdIndex = addressString.indexOf(" ")
                if (placeIdIndex!=-1){
                    return addressString.substring(placeIdIndex+1)
                }else{
                    return addressString
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
        return "No Address Found"
    }
    fun convertObject(snapshot: QueryDocumentSnapshot) : Model?{
        val userModel: Model =
            snapshot.toObject(Model::class.java)
        userModel.id = snapshot.id ?: ""
        return userModel
    }
    fun getIndex(userModel: Model) : Int{
        var index = -1
        index = arrayList.indexOfFirst { element ->
            element.id?.equals(userModel.id) == true
        }
        return index
    }
    fun dialog(){
        var dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
        var dialog = Dialog(mainScreenActivity).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogBinding.tvTime.setText(formattedTime)
            dialogBinding.btnAdd.setText("Add")

            dialogBinding.btnAdd.setOnClickListener {
                if (dialogBinding.etName.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etName.error = "Enter Name"
                }else{

                    dialogBinding.pgBar.visibility = View.VISIBLE
                    dialogBinding.btnAdd.visibility = View.GONE
//                        Toast.makeText(this@MainActivity, "$position", Toast.LENGTH_SHORT).show()
                    database.collection(collectionName).add(
                        Model(
                            id = auth.currentUser?.uid,
                            vendorName = dialogBinding.etName.text.toString(),
                            inTime = dialogBinding.tvTime.text.toString(),
                            inLocation = currentLoc,
                            inLat = currentLat,
                            inLong = currentLong,
                            date = formattedDate,
                        )
                    ).addOnSuccessListener {
                        dialogBinding.pgBar.visibility = View.GONE
                        dialogBinding.btnAdd.visibility = View.VISIBLE
                        this.dismiss()
                        Toast.makeText(mainScreenActivity, "success", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener {
                            dialogBinding.pgBar.visibility = View.GONE
                            dialogBinding.btnAdd.visibility = View.VISIBLE
                            Toast.makeText(mainScreenActivity, "failure", Toast.LENGTH_SHORT).show()

                        }
                    inAdapter.notifyDataSetChanged()


                }
            }
            show()
        }
    }

    override fun onClick(position: Int) {
        findNavController().navigate(R.id.outFragment,
            bundleOf("name" to arrayList[position].vendorName,
                "inTime" to arrayList[position].inTime,
                "inLoc" to arrayList[position].inLocation,
                "inLat" to arrayList[position].inLat,
                "inLong" to arrayList[position].inLong,
                "id" to arrayList[position].id,
                "position" to position,
                "date" to arrayList[position].date,
                "productName" to arrayList[position].productName,
                "quantity" to arrayList[position].quantity,
            ))
    }
}