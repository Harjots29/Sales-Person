package com.harjot.salesadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.harjot.salesadmin.databinding.FragmentHistoryDetailsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryDetails : Fragment() {
    val binding by lazy {
        FragmentHistoryDetailsBinding.inflate(layoutInflater)
    }
    val database=FirebaseFirestore.getInstance()

    lateinit var mainActivity: MainActivity
    var id = ""
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            id = it.getString("id","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database.collection("SalesPerson").document(id).get()
            .addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()){
                    val vendorName = documentSnapshot.getString("vendorName")
                    val inTime = documentSnapshot.getString("inTime")
                    val outTime = documentSnapshot.getString("outTime")
                    val productName = documentSnapshot.getString("productName")
                    val quantity = documentSnapshot.getString("quantity")
                    val inLoc = documentSnapshot.getString("inLocation")
                    val outLoc = documentSnapshot.getString("outLocation")
                    val inLat = documentSnapshot.getString("inLat")
                    val outLat = documentSnapshot.getString("outLat")
                    val inLong = documentSnapshot.getString("inLong")
                    val outLong = documentSnapshot.getString("outLong")
                    val date = documentSnapshot.getString("date")

                    binding.tvVendor.setText(vendorName)
                    binding.tvInTime.setText(inTime)
                    binding.tvOutTime.setText(outTime)
                    binding.tvInLoc.setText(inLoc)
                    binding.tvOutLoc.setText(outLoc)
                    binding.tvProduct.setText(productName)
                    binding.tvQuantity.setText(quantity)
                    binding.tvInLat.setText(inLat)
                    binding.tvOutLat.setText(outLat)
                    binding.tvInLon.setText(inLong)
                    binding.tvOutLon.setText(outLong)
                    binding.tvDate.setText(date)
                }else{

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
         * @return A new instance of fragment HistoryDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}