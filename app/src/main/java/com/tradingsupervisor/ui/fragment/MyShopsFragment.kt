package com.tradingsupervisor.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradingsupervisor.R
import com.tradingsupervisor.adapter.ShopsRecyclerViewAdapter
import com.tradingsupervisor.viewmodel.ShopViewModel
import com.tradingsupervisor.webApi.ResponseStatus
import java.util.*

class MyShopsFragment : Fragment() {
    private lateinit var uploadAllShopsBtn: Button
    private lateinit var refreshShopList: Button
    private lateinit var progressPercentage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_shops, container, false)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView = view.findViewById(R.id.all_shops_recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ShopsRecyclerViewAdapter()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, layoutManager.orientation))

        refreshShopList = view.findViewById(R.id.refreshShopsBtn)
        uploadAllShopsBtn = view.findViewById(R.id.uploadAllShopsBtn)
        progressBar = view.findViewById(R.id.visitedShopsProgressBar)
        progressPercentage = view.findViewById(R.id.visitedShopsPercentageTv)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val shopViewModel = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)

        refreshShopList.setOnClickListener { btn ->
            shopViewModel.downloadShopList()
            btn.isEnabled = false
        }

        uploadAllShopsBtn.setOnClickListener {
            shopViewModel.uploadAllPhotos()
        }

        shopViewModel.allShops.observe(viewLifecycleOwner) { shops ->
            (recyclerView.adapter as ShopsRecyclerViewAdapter).submitList(shops)
        }

        shopViewModel.visitPercentage.observe(viewLifecycleOwner) { data ->
            val percent = data ?: 0
            progressPercentage.text = String.format(Locale.US, "%d%%", percent)
            progressBar.progress = percent
        }

        shopViewModel.photoCount.observe(viewLifecycleOwner) { photoCount ->
            uploadAllShopsBtn.isEnabled = photoCount != 0
        }

        shopViewModel.uploadPhotosStatus.observe(viewLifecycleOwner) { status ->
            if (status == null) return@observe
            val builder = AlertDialog.Builder(activity)
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { dialog, which -> dialog.cancel() }
            when (status) {
                ResponseStatus.CLIENT_ERROR -> {
                    builder.setMessage("No Internet connection. Photos were not uploaded")
                    builder.create().show()
                }
                ResponseStatus.SUCCESS -> {
                    builder.setMessage("Photos successfully uploaded")
                    builder.create().show()
                }
                ResponseStatus.UNAUTHORIZED -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                            .add(R.id.start_activity_container,
                                    AuthenticationFragment.newInstance(), AuthenticationFragment.TAG)
                            .commit()
                }
                ResponseStatus.SERVER_ERROR -> {
                    builder.setMessage("Internal server error")
                    builder.create().show()
                }
                ResponseStatus.ERROR -> {
                    builder.setMessage("Unknown error")
                    builder.create().show()
                }

            }
        }
    }

    companion object {
        const val TAG = "MyShopsFragment"
        fun newInstance(): MyShopsFragment {
            return MyShopsFragment()
        }
    }
}