package com.harjot.salesperson

import android.app.Dialog
import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.harjot.salesperson.databinding.AddressDialogBinding
import com.harjot.salesperson.databinding.FragmentOutBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutFragment : Fragment() {
    val binding by lazy {
        FragmentOutBinding.inflate(layoutInflater)
    }
    private var auth: FirebaseAuth = Firebase.auth
    lateinit var mainActivity: MainActivity

    val collectionName = "SalesPerson"
    val userList = mutableListOf<Model>()
    var userModel = Model()
    private var database = FirebaseFirestore.getInstance()

    var arrayList = ArrayList<Model>()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val currentTime = LocalTime.now()
    val formattedTime = currentTime.format(formatter)

    var name:String?= null
    var inTime:String?= null
    var inLoc:String?= null
    var inLat:String?= null
    var inLong:String?= null
    var date:String?=null
    var position= -1
    var id = ""
    var productName = ""
    var quantity = ""

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            name = it.getString("name")
            inTime = it.getString("inTime")
            inLoc = it.getString("inLoc")
            inLat = it.getString("inLat")
            inLong = it.getString("inLong")
            position = it.getInt("position")
            id = it.getString("id","")
            date = it.getString("date","")
            productName = it.getString("productName","")
            quantity = it.getString("quantity","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        Toast.makeText(mainActivity, "$position", Toast.LENGTH_SHORT).show()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvOutTime.setText(formattedTime)
//        binding.btnDelete.visibility = View.VISIBLE
        binding.tvName.setText(name)
        binding.tvInTime.setText(inTime)
        binding.tvLatLong.setText(inLat + inLong)
        binding.etProductName.setText(productName)
        binding.etQuantity.setText(quantity)

//        Toast.makeText(mainActivity, "${arrayList.size}", Toast.LENGTH_SHORT).show()

        if (!binding.etProductName.text.toString().isNullOrEmpty() && !binding.etQuantity.text.toString().isNullOrEmpty()){
            binding.btnSave.visibility = View.GONE
        }
        binding.btnSave.setOnClickListener {
            if(binding.etProductName.text.toString().trim().isNullOrEmpty()){
                binding.etProductName.error = "Enter Product Name"
            }else if(binding.etQuantity.text.toString().trim().isNullOrEmpty()){
                binding.etQuantity.error = "Enter Quantity"
            } else{
                binding.btnSave.visibility = View.GONE
                database.collection(collectionName).document(id)
                    .set(
                        Model(
                            id = auth.currentUser?.uid,
                            vendorName = binding.tvName.text.toString(),
                            inTime = binding.tvInTime.text.toString(),
                            outTime = binding.tvOutTime.text.toString(),
                            inLat = inLat,
                            inLong = inLong,
                            inLocation = inLoc,
                            outLocation = inLoc,
                            productName = binding.etProductName.text.toString(),
                            quantity = binding.etQuantity.text.toString(),
                            outLat = inLat,
                            outLong = inLong,
                            date = date,
                        )
                    ).addOnSuccessListener {
                        findNavController().popBackStack()
                        Toast.makeText(mainActivity, "added", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(mainActivity, "failure", Toast.LENGTH_SHORT).show()
                    }
            }
        }
//        binding.btnDelete.setOnClickListener {
//            if (binding.btnSave.isVisible){
//                Toast.makeText(mainActivity, "No data in database", Toast.LENGTH_SHORT).show()
//            }else{
//                database.collection(collectionName)
//                    .document(id)
//                    .delete()
//                    .addOnSuccessListener {
//                        Toast.makeText(mainActivity, "Deleted", Toast.LENGTH_SHORT).show()
//                        binding.btnDelete.visibility = View.GONE
//                    }.addOnFailureListener {
//                        Toast.makeText(mainActivity, "Failed", Toast.LENGTH_SHORT).show()
//                    }
//            }
//        }
        binding.ivInfo.setOnClickListener {
            var dialogBinding = AddressDialogBinding.inflate(layoutInflater)
            var dialog = Dialog(mainActivity).apply {
                setContentView(dialogBinding.root)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                dialogBinding.tvAddress.setText(inLoc)
                show()

            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}