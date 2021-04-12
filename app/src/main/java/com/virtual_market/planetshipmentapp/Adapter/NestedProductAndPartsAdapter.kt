package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Modal.SerialDetailsModal
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.R

class NestedProductAndPartsAdapter(
    private val context: Context,
    private val responsePost: List<SerialProductListModel>
) : RecyclerView.Adapter<NestedProductAndPartsAdapter.viewholder>() {

    private val productId: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate =
            layoutInflater.inflate(R.layout.show_nested_product_parts_adapter, parent, false)
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
        holder.order_no.text="Order No : "+responseOrders.OrdCode
        holder.detail_name.text=responseOrders.DetailName
        holder.serial_number.text=responseOrders.SerialNumber
        holder.date.text=responseOrders.DeliveryDate!!.substring(0,10)

        holder.bindView(responseOrders.Details)

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var recyclerView: RecyclerView? = null
        val imageIcon: ImageView = itemView.findViewById(R.id.image_icon)
        val foreign_name: TextView = itemView.findViewById(R.id.foreign_name)
        val sapCode: TextView = itemView.findViewById(R.id.sapCode)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val warehouse: TextView = itemView.findViewById(R.id.warehouse)
        val item_code: TextView = itemView.findViewById(R.id.item_code)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val detail_name: TextView = itemView.findViewById(R.id.detail_name)
        val serial_number: TextView = itemView.findViewById(R.id.serial_number)

        init {

            recyclerView = itemView.findViewById(R.id.recyclerView)
            val gridLayoutManager = GridLayoutManager(context, 2)
            recyclerView!!.layoutManager = gridLayoutManager

            val spanCount = 2 // colums
            val spacing = 10 // spaces
            val includeEdge = true
            recyclerView!!.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    spacing,
                    includeEdge
                )
            )


        }

        fun bindView(parts: ArrayList<SerialDetailsModal>?) {

            val showPartitianAdapter = ShowPartitianAdapter(context, parts!!)
            recyclerView!!.adapter = showPartitianAdapter

            showPartitianAdapter.productId(productId)

        }


    }

    fun numberCalculation(number: Long): String? {
        if (number < 1000) return "" + number
        val exp = (Math.log(number.toDouble()) / Math.log(1000.0)).toInt()
        return String.format(
            "%.1f %c", number / Math.pow(1000.0, exp.toDouble()),
            "kMGTPE"[exp - 1]
        )


    }

    fun setProductId(productId: ArrayList<String>) {

        this.productId.clear()
        this.productId.addAll(productId)

    }

}