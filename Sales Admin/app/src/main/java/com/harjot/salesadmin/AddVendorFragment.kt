package com.harjot.salesadmin

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.harjot.salesadmin.databinding.FragmentAddVendorBinding
import com.harjot.salesadmin.databinding.VendorAddDialogBinding
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddVendorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVendorFragment : Fragment(),Interfaces {
    val binding by lazy {
        FragmentAddVendorBinding.inflate(layoutInflater)
    }
    lateinit var mainScreenActivity: MainScreenActivity
    var arrayList = ArrayList<VendorModel>()
    lateinit var adapter : VendorAdapter

    var database = Firebase.firestore
    val collectionName = "Admin"
    var auth = Firebase.auth
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenActivity = activity as MainScreenActivity

        adapter = VendorAdapter(arrayList,this)
        binding.rvList.layoutManager = LinearLayoutManager(mainScreenActivity)
        binding.rvList.adapter = adapter

        mainScreenActivity.pgBar?.visibility = View.VISIBLE
        arrayList.clear()
        database.collection(collectionName)
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
                adapter.notifyDataSetChanged()
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivMore.setOnClickListener {
            val popupMenu = PopupMenu(mainScreenActivity,binding.ivMore)
            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.pop_up_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem -> // Toast message on menu item clicked
                when (item.itemId) {
                    R.id.popUpLogout ->{
                        auth.signOut()
                        var intent = Intent(mainScreenActivity,LoginActivity::class.java)
                        startActivity(intent)
                        mainScreenActivity.finish()
                    }
                    else->{}
                }
                true
            }
            popupMenu.show()
        }
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
         * @return A new instance of fragment AddVendorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddVendorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun dialog(){
        var dialogBinding = VendorAddDialogBinding.inflate(layoutInflater)
        var dialog = Dialog(mainScreenActivity).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialogBinding.btnAdd.setOnClickListener {
                if (dialogBinding.etName.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etName.error = "Enter Name"
                }else if(dialogBinding.etEmail.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etEmail.error = "Enter Email"
                }else if(Pattern.matches(Patterns.EMAIL_ADDRESS.toString(),dialogBinding.etEmail.text.toString().trim())==false){
                    dialogBinding.etEmail.error = "Enter Valid Email"
                }else if(dialogBinding.etPassword.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etPassword.error = "Enter Password"
                }else if(dialogBinding.etPassword.length()<6){
                    dialogBinding.etPassword.error = "Password must be of atleast 6 characters"
                }else if(dialogBinding.etCity.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etCity.error = "Enter City"
                }else if(dialogBinding.etContact.text.toString().trim().isNullOrEmpty()){
                    dialogBinding.etContact.error = "Enter Contact"
                }else if(dialogBinding.etContact.length()<10 || dialogBinding.etContact.length()>10){
                    dialogBinding.etContact.error = "Enter valid Contact"
                }else{
                    var email = dialogBinding.etEmail.text.toString().trim()
                    var password = dialogBinding.etPassword.text.toString().trim()
                    dialogBinding.pgBar.visibility = View.VISIBLE
                    dialogBinding.btnAdd.visibility = View.GONE
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnSuccessListener {
                            var model = VendorModel(
                                uid = auth.currentUser?.uid,
                                name = dialogBinding.etName.text.toString().trim(),
                                email = dialogBinding.etEmail.text.toString().trim(),
                                password = dialogBinding.etPassword.text.toString().trim(),
                                city = dialogBinding.etCity.text.toString().trim(),
                                contact = dialogBinding.etContact.text.toString().trim()
                            )
                            database.collection(collectionName).add(model)
                                .addOnSuccessListener {
                                    dialogBinding.pgBar.visibility = View.GONE
                                    dialogBinding.btnAdd.visibility = View.VISIBLE
                                    Toast.makeText(mainScreenActivity, "Added", Toast.LENGTH_SHORT).show()
                                    this.dismiss()
                                }.addOnFailureListener {
                                    dialogBinding.pgBar.visibility = View.GONE
                                    dialogBinding.btnAdd.visibility = View.VISIBLE
                                    Toast.makeText(mainScreenActivity, "Failed", Toast.LENGTH_SHORT).show()
                                }
                        }.addOnFailureListener {
                            dialogBinding.pgBar.visibility = View.GONE
                            dialogBinding.btnAdd.visibility = View.VISIBLE
                            Toast.makeText(mainScreenActivity, "Email Already Exists", Toast.LENGTH_SHORT).show()
                        }

                }
            }
            show()
        }
    }
    fun convertObject(snapshot: QueryDocumentSnapshot) : VendorModel?{
        val userModel: VendorModel =
            snapshot.toObject(VendorModel::class.java)
        userModel.id = snapshot.id ?: ""
        return userModel
    }
    fun getIndex(userModel: VendorModel) : Int{
        var index = -1
        index = arrayList.indexOfFirst { element ->
            element.id?.equals(userModel.id) == true
        }
        return index
    }

    override fun onClick(position: Int) {
        findNavController().navigate(R.id.historyFragment,
            bundleOf(
                "uid" to arrayList[position].uid
            )
        )
    }
}