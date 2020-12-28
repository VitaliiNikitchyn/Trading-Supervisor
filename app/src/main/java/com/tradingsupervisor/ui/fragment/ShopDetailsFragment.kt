package com.tradingsupervisor.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tradingsupervisor.R
import com.tradingsupervisor.data.entity.Product
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.webApi.HttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopDetailsFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var shopName: TextView
    private lateinit var shopAddress: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_shop_details, container, false)
        shopName = view.findViewById(R.id.shop_name)
        shopAddress = view.findViewById(R.id.shop_address)
        listView = view.findViewById(R.id.assortment_list)
        setupAssortmentList()

        view.findViewById<View>(R.id.fab_camera).setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag(CameraFragment.TAG) ?: CameraFragment.newInstance()
            fragmentManager.beginTransaction()
                    .add(R.id.photo_activity_container, fragment, CameraFragment.TAG)
                    .addToBackStack(null)
                    .commit()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val shop: Shop? = requireActivity().intent.getParcelableExtra(Shop.TAG)
        shopName.text = shop?.name
        shopAddress.text = shop?.address
    }

    private fun setupAssortmentList() {
        val mActivity = requireActivity()
        val sharedPref = mActivity.getSharedPreferences(getString(R.string.appSharedPreferences), Context.MODE_PRIVATE)
        val header = sharedPref.getString(getString(R.string.authToken), null)
        val shopID = requireActivity().intent.getLongExtra(Shop.SHOP_ID, -99)
        HttpClient.getApi().getProducts(header, shopID.toInt()).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()!!
                    val products = Array(productList.size) { i ->
                        productList[i].category + " " + productList[i].name
                    }
                    listView.adapter = ArrayAdapter<Any>(mActivity, R.layout.list_item_product, R.id.product_name, products)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                val products = arrayOf("Could not update product list for this shop")
                listView.adapter = ArrayAdapter<Any>(mActivity, R.layout.list_item_product, R.id.product_name, products)
            }
        })

        /*
        String[] products = { "Super Schocolate", "Plombir 100%", "Gold Key"};
        / *
        final String ASSORTMENT_NAME = "ASSORTMENT";
        List<Map<String, String>> listData = new ArrayList<>();
        for (Product product : shopViewModel.getAssortment(shopIndex)) {
            Map<String, String> item = new HashMap<>();
            item.put(ASSORTMENT_NAME, product.getName());
            listData.add(item);
        }
        String from[] = { ASSORTMENT_NAME };
        int to[] = { R.id.assortment_name };
        //SimpleAdapter adapter = new SimpleAdapter(getActivity(), listData, R.layout.list_item_product, from, to);


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.list_item_product, R.id.assortment_name, products);
        listView.setAdapter(adapter);
        */
    }

    companion object {
        const val TAG = "ShopDetailsFragment"
        fun newInstance(): ShopDetailsFragment {
            return ShopDetailsFragment()
        }
    }
}