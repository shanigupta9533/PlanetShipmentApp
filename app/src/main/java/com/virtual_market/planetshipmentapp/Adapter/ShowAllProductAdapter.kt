package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Activity.QrCodeWithProductActivity
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.R

class ShowAllProductAdapter(private val context: Context,
                            private val responsePost: List<SerialProductListModel>
) : RecyclerView.Adapter<ShowAllProductAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.all_product_adapter, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(isChecked: Boolean, position: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val responseOrders = responsePost[position]

        holder.foreign_name.text=responseOrders.ForeignName
        holder.no_of_items.text=responseOrders.AllocQty
        holder.warehouse.text=responseOrders.Warehouse
        holder.item_code.text=responseOrders.ItemCode
        holder.order_no.text="Order No : "+responseOrders.OrdCode
        holder.detail_name.text=responseOrders.DetailName
        holder.date.text="Delivery date : "+responseOrders.DeliveryDate!!.substring(0,10)

        Glide.with(context).load("responseOrders.image").placeholder(R.drawable.ic_logo_brown).error(R.drawable.ic_logo_brown).into(holder.imageIcon)

        holder.scan_now.setOnClickListener {

            val intent = Intent(context, QrCodeWithProductActivity::class.java)
            intent.putExtra("orderCode", responseOrders.OrdCode)
            intent.putExtra("spoCode", responseOrders.SapOrderCode)
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.image_icon)
        val foreign_name: TextView = itemView.findViewById(R.id.foreign_name)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val warehouse: TextView = itemView.findViewById(R.id.warehouse)
        val item_code: TextView = itemView.findViewById(R.id.item_code)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val detail_name: TextView = itemView.findViewById(R.id.detail_name)
        val scan_now: RelativeLayout = itemView.findViewById(R.id.scan_now)

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