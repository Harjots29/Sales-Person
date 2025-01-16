package com.harjot.salesperson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.harjot.salesperson.databinding.FragmentHistoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }
    var auth = Firebase.auth
    private lateinit var recyclerView: RecyclerView
    private val database = FirebaseFirestore.getInstance()
    private val collectionName = "SalesPerson"
    lateinit var mainActivity: MainActivity
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = activity as MainActivity

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
                val list = mutableListOf<TableModel>()
                for (document in documents) {
                    val salesId = document.getString("id") ?: ""
                    if (salesId == auth.currentUser?.uid) {
                        val id = document.id
                        val vendorName = document.getString("vendorName") ?: ""
                        val inTime = document.getString("inTime") ?: ""
                        val outTime = document.getString("outTime") ?: ""
                        val productName = document.getString("productName") ?: ""
                        val quantity = document.getString("quantity") ?: ""

                        if (productName != null) {
                            list.add(
                                TableModel(
                                    id,
                                    vendorName,
                                    inTime,
                                    outTime,
                                    productName,
                                    quantity
                                )
                            )
                        }
                    }else{ }
                }
                recyclerView.adapter = TableAdapter(list)
            }
            .addOnFailureListener { e ->
                Toast.makeText(mainActivity, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}