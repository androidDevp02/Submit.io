package com.yogeshj.autoform.FeedbackFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.FragmentFeedbackBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FeedbackFragment : Fragment() {

    private lateinit var binding:FragmentFeedbackBinding

    private lateinit var dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentFeedbackBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        initLoadingDialog()

        val databaseReference = FirebaseDatabase.getInstance().reference

        binding.submitFeedbackButton.setOnClickListener {
            showLoading()
            val feedbackText = binding.feedbackEditText.text.toString().trim()

            if (feedbackText.isEmpty()) {
                hideLoading()
                Toast.makeText(requireContext(), "Please enter your feedback!", Toast.LENGTH_SHORT).show()
            }
            else{
                val dateFormat = SimpleDateFormat("dd/MM/yy, HH:mm:ss", Locale.getDefault())
                val currentTime = dateFormat.format(System.currentTimeMillis())

                databaseReference.child("Feedback")
                    .child(FirstScreenActivity.auth.currentUser!!.uid)
                    .push()
                    .setValue(FeedbackModel(feedbackText,currentTime,FirstScreenActivity.auth.currentUser!!.uid,FirstScreenActivity.auth.currentUser!!.email.toString()))
                    .addOnSuccessListener {
                        hideLoading()
                        Toast.makeText(requireContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show()
                        binding.feedbackEditText.text?.clear()
                    }
                    .addOnFailureListener {
                        hideLoading()
                        Toast.makeText(requireContext(), "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun initLoadingDialog() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        binding.root.alpha = 0.5f
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
            binding.root.alpha = 1f
        }
    }
}