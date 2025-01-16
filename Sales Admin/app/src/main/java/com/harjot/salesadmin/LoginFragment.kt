package com.harjot.salesadmin

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.harjot.salesadmin.databinding.FragmentLoginBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    val binding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }
    lateinit var mainActivity: MainActivity
    private var auth: FirebaseAuth = Firebase.auth
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

//        val formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY")
//        var date = LocalDate.now()
//        val formattedDate = date.format(formatter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.toString().trim().isNullOrEmpty()){
                binding.etEmail.error = "Enter Email"
                binding.etEmail.requestFocus()
            }else if(Pattern.matches(Patterns.EMAIL_ADDRESS.toString(),binding.etEmail.text.toString().trim())==false){
                binding.etEmail.error = "Enter Valid Email"
                binding.etEmail.requestFocus()
            } else if (binding.etPassword.text.toString().trim().isNullOrEmpty())
            {
                binding.etPassword.error = "Enter Password"
                binding.etPassword.requestFocus()
            } else if(binding.etPassword.length()<6){
                binding.etPassword.error="Enter Atleast 6 Characters"
            }else{
                binding.pgBar.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.GONE
                var email = binding.etEmail.text.toString().trim()
                var password = binding.etPassword.text.toString().trim()
                auth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener {
                        binding.pgBar.visibility = View.GONE
                        binding.linearLayout.visibility = View.VISIBLE
                        findNavController().navigate(R.id.addVendorFragment)
                        Toast.makeText(mainActivity, "Login Successfully", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener {
                        binding.linearLayout.visibility = View.VISIBLE
                        binding.pgBar.visibility = View.GONE
                        Toast.makeText(mainActivity, "Password or Email Incorrect", Toast.LENGTH_SHORT).show()
                    }
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}