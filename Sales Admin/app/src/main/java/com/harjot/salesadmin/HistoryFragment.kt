package com.harjot.salesadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.harjot.salesadmin.databinding.FragmentHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment(),HistoryInterface {
    val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }
    var arrayList = ArrayList<HistoryModel>()
    val list = mutableListOf<HistoryModel>()
    lateinit var mainActivity: MainActivity
    private lateinit var recyclerView: RecyclerView
    private val database = FirebaseFirestore.getInstance()
    private val collectionName = "SalesPerson"

    var uid = ""
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            uid = it.getString("uid","")
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
        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        fetchData()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun fetchData() {
        database.collection(collectionName).get()
            .addOnSuccessListener { documents ->
                list.clear()
                for (document in documents) {
                    val salesId = document.getString("id")?: ""
                    if (uid == salesId) {
                        val id = document.id
                        val vendorName = document.getString("vendorName") ?: ""
                        val inTime = document.getString("inTime") ?: ""
                        val outTime = document.getString("outTime") ?: ""
                        val productName = document.getString("productName") ?: ""
                        val quantity = document.getString("quantity") ?: ""
                        val date = document.getString("date") ?: ""
                        if (productName != null) {
                            list.add(HistoryModel(
                                id,
                                vendorName,
                                date))
                        }
                    }else{ }
                }
                recyclerView.adapter = HistoryAdapter(list,this)
                if(list.isEmpty()){
                    Toast.makeText(mainActivity, "Nothing to Show", Toast.LENGTH_SHORT).show()
                }else{

                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(mainActivity, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun details(position: Int) {
        findNavController().navigate(R.id.historyDetails,
            bundleOf("id" to list[position].id))
    }
}