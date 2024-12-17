package com.yogeshj.autoform.user.userSubscriptionFragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.FragmentUserSubscriptionBinding
import java.util.Calendar


class UserSubscriptionFragment : Fragment() {

    private lateinit var binding:FragmentUserSubscriptionBinding


    private lateinit var dialog:Dialog

    private var selectedOption: String = ""
    private var fees=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentUserSubscriptionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoadingDialog()

//        Toast.makeText(context,"${Calendar.getInstance().time}", Toast.LENGTH_LONG).show()

        showLoading()

        val dbRef = FirebaseDatabase.getInstance().getReference("SubscriptionPayment")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val subscriptionDetails = snap.getValue(SubscriptionPaymentModel::class.java)
                        if (subscriptionDetails?.userId == FirstScreenActivity.auth.currentUser?.uid) {
                            val endDate = subscriptionDetails?.endDate

                            if (endDate != null && System.currentTimeMillis() > endDate) {
                                // Subscription expired
                                binding.subscribedLayout.visibility=View.GONE
                                binding.notSubscribedLayout.visibility=View.VISIBLE
                                snap.ref.removeValue()
                            }
                            else {
                                binding.subscribedLayout.visibility = View.VISIBLE
                                binding.notSubscribedLayout.visibility=View.GONE
                            }
                            break
                        }
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })

        binding.weeklyOption.setOnClickListener {
            selectOption("Weekly",binding.weeklyOption)
            fees=10
        }
        binding.monthlyOption.setOnClickListener {
            selectOption("Monthly",binding.monthlyOption)
            fees=30
        }
        binding.yearlyOption.setOnClickListener {
            selectOption("Yearly",binding.yearlyOption)
            fees=250
        }

        binding.confirmButton.setOnClickListener {
            val intent=Intent(context, SubscriptionActivity::class.java)
            intent.putExtra("selectedOption", selectedOption)
            intent.putExtra("fees",fees)
            startActivity(intent)
        }

    }

//

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



    private fun selectOption(option: String, selectedLayout: LinearLayout) {
        resetSelections()
        selectedLayout.setBackgroundColor(Color.LTGRAY)
        selectedOption = option
    }

    private fun resetSelections() {
        binding.weeklyOption.setBackgroundColor(Color.WHITE)
        binding.monthlyOption.setBackgroundColor(Color.WHITE)
        binding.yearlyOption.setBackgroundColor(Color.WHITE)
    }
}