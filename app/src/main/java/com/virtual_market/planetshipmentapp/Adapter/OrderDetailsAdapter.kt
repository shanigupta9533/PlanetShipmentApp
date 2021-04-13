package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Modal.ProductItem
import com.virtual_market.planetshipmentapp.R
import java.lang.NumberFormatException

class OrderDetailsAdapter (private val context: Context,
private val responsePost: List<ProductItem>
) : RecyclerView.Adapter<OrderDetailsAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.orders_details_adapter, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(isChecked: Boolean, position: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val productItem = responsePost[position]

        Glide.with(context).load(productItem.mainImage).placeholder(R.drawable.ic_logo_brown).error(R.drawable.ic_logo_brown).into(holder.image_icon)
        holder.no_of_items.text = productItem.qty.toString()
        holder.price.text = productItem.mRP
        holder.title.text = productItem.detailName

        if (!TextUtils.isEmpty(productItem.mRP))

            try {
                holder.price.text = numberCalculation(productItem.mRP!!.toFloat().toLong())
            } catch (e: NumberFormatException){
                holder.price.text = productItem.mRP!!
            }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image_icon=itemView.findViewById<ImageView>(R.id.image_icon)
        val no_of_items=itemView.findViewById<TextView>(R.id.no_of_items)
        val price=itemView.findViewById<TextView>(R.id.price)
        val title=itemView.findViewById<TextView>(R.id.title)

    }


    fun numberCalculation(number: Long): String? {
        if (number < 1000) return "" + number
        val exp = (Math.log(number.toDouble()) / Math.log(1000.0)).toInt()
        return String.format(
            "%.1f %c", number / Math.pow(1000.0, exp.toDouble()),
            "kMGTPE"[exp - 1]
        )

    }

}
