package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.Activity.ShowClientAddressActivity

class DeliveredProductAdapterr(
    private val context: Context,
    private val responsePost: List<String>
) : RecyclerView.Adapter<DeliveredProductAdapterr.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.delivered_product_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(isChecked: Boolean, position: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        holder.itemView.setOnClickListener {

            context.startActivity(Intent(context, ShowClientAddressActivity::class.java))

        }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }
}