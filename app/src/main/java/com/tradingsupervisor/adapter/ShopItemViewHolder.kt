package com.tradingsupervisor.adapter

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradingsupervisor.R
import com.tradingsupervisor.data.entity.Shop
import java.util.*

class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.shop_name)
    private val address: TextView = itemView.findViewById(R.id.shop_address)
    private val visitedStatus: TextView = itemView.findViewById(R.id.colorVisitedTextView)

    fun bind(shop: Shop) {
        name.text = shop.name
        address.text = shop.address
        visitedStatus.setBackgroundColor(Color.parseColor(
                if (Date().time - shop.lastVisitedDate.time < 86400000) "#FF5CF566" else "#FFFC3535"))
        //86400000 = 60 * 60 * 1000 * 24 = 24 hours
    }
}