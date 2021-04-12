package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Activity.QrCodeScannerActivity
import com.virtual_market.planetshipmentapp.Modal.ResponsePartModel
import com.virtual_market.planetshipmentapp.Modal.ResponsePartsModel
import com.virtual_market.planetshipmentapp.Modal.SerialDetailsModal
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R

class ShowPartitianAdapter (
    private val context: Context,
    private val responsePost: List<SerialDetailsModal>
) : RecyclerView.Adapter<ShowPartitianAdapter.viewholder>() {

    private var productIdString: String?=null
    private var stringModel:MutableList<String> = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.show_partitian_adapter_layout, parent, false)
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
        holder.sapCode.text=responseOrders.SapOrderCode
        holder.no_of_items.text=responseOrders.AllocQty
        holder.warehouse.text=responseOrders.Warehouse
        holder.item_code.text=responseOrders.ItemCode
        holder.order_no.text= "Sub Product ${position+1}"
        holder.detail_name.text=responseOrders.DetailName
        holder.date.text=responseOrders.DeliveryDate!!.substring(0,10)

        if((position+1)%2==0)
            holder.divider.visibility=View.GONE
        else
            holder.divider.visibility=View.VISIBLE

        stringModel.forEach {

            if(responseOrders.SerialNumber.equals(it,true)){
                holder.parent_of_card.setCardBackgroundColor(Color.parseColor("#00ff00"))

            }

        }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    fun productId(productId: java.util.ArrayList<String>) {

        stringModel.clear()
        stringModel.addAll(productId)

    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.image_icon)
        val foreign_name: TextView = itemView.findViewById(R.id.foreign_name)
        val sapCode: TextView = itemView.findViewById(R.id.sapCode)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val warehouse: TextView = itemView.findViewById(R.id.warehouse)
        val item_code: TextView = itemView.findViewById(R.id.item_code)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val divider: View = itemView.findViewById(R.id.divider)
        val parent_of_card: CardView = itemView.findViewById(R.id.parent_of_card)
        val detail_name: TextView = itemView.findViewById(R.id.detail_name)

    }

}