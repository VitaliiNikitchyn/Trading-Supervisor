package com.tradingsupervisor.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import com.tradingsupervisor.R
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tradingsupervisor.data.entity.Shop
import java.util.*

class SimpleShopAdapter(private val context: Context, private var items: List<Shop> = emptyList()) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateData(newList: List<Shop>) {
        items = newList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ShopItemViewHolder
        val view = if (convertView == null) {
            val v = LayoutInflater.from(context).inflate(R.layout.list_item_shop, parent, false)
            viewHolder = ShopItemViewHolder(v)
            v.tag = viewHolder
            v
        } else {
            viewHolder = convertView.tag as ShopItemViewHolder
            convertView
        }
        viewHolder.bind(items[position])
        return view
    }
}