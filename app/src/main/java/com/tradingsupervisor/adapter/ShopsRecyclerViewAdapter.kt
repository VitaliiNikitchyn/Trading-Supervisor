package com.tradingsupervisor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradingsupervisor.R
import com.tradingsupervisor.data.entity.Shop

class ShopsRecyclerViewAdapter : RecyclerView.Adapter<ShopItemViewHolder>() {
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ShopItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_shop, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        viewHolder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Shop>) {
        differ.submitList(list)
    }

    companion object {
        private val diffCallback: DiffUtil.ItemCallback<Shop> = object : DiffUtil.ItemCallback<Shop>() {
            override fun areItemsTheSame(sh1: Shop, sh2: Shop): Boolean {
                return sh1.id.toLong() == sh2.id.toLong() //compare by ID
            }

            override fun areContentsTheSame(shop1: Shop, shop2: Shop): Boolean {
                return shop1.name == shop2.name &&
                        shop1.address == shop2.address &&
                        shop1.lastVisitedDate.time == shop2.lastVisitedDate.time
            }
        }
    }
}