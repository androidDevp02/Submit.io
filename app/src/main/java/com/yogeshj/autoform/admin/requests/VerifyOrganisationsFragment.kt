package com.yogeshj.autoform.admin.requests

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.splashScreenAndIntroScreen.IntroActivity
import com.yogeshj.autoform.databinding.FragmentVerifyOrganisationsBinding

class VerifyOrganisationsFragment : Fragment() {

    private lateinit var binding:FragmentVerifyOrganisationsBinding

    lateinit var myAdapter: UploadFormSignUpAdapter
    lateinit var dataList: ArrayList<UploadFormSignUpModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentVerifyOrganisationsBinding.inflate(inflater,container,false)
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener {
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(
                    Intent(context, FirstScreenActivity::class.java))
                .addNextIntent(Intent(context, IntroActivity::class.java))
                .startActivities()

        }

        dataList =ArrayList()
        myAdapter = UploadFormSignUpAdapter(dataList,requireContext())
        binding.rvRegistrationList.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvRegistrationList.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("UploadFormRegistrationUsers")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val curr=snap.getValue(UploadFormSignUpModel::class.java)
                        if (curr!=null) {
                            dataList.add(UploadFormSignUpModel(curr.websitelink,curr.instituteName,curr.headName,curr.instituteAddress,curr.instituteContact,curr.instituteMailId,curr.loginMailId,curr.key))

                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}