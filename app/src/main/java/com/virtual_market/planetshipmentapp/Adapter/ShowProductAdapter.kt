package com.virtual_market.planetshipmentapp.Adapter;

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Activity.OrderDetailsActivity
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.R

class ShowProductAdapter(
    private val context: Context,
    private val responsePost: List<ResponseOrders>
) : RecyclerView.Adapter<ShowProductAdapter.viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.show_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(isChecked: Boolean, position: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val responseOrders = responsePost[position]

        Glide.with(context).load(responseOrders.main_image).error(R.drawable.after_delete_sofa)
            .into(holder.imageIcon)

        holder.order_no.text = "Order No. ${responseOrders.OrdCode}"
        holder.date.text = responseOrders.DeliveryDate
        holder.no_of_items.text = "${responseOrders.ItemCount}"

        if (!TextUtils.isEmpty(responseOrders.BookingAmount))

            try {
                holder.price.text = numberCalculation(responseOrders.OrdAmount!!.toFloat().toLong())
            } catch (e: NumberFormatException) {
                holder.price.text = responseOrders.OrdAmount!!
            }

        holder.details_button.setOnClickListener {

            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("responseOrders", responseOrders)
            intent.putExtra("ordJson", responseOrders.ordJsonList)
            intent.putParcelableArrayListExtra(
                "productItem",
                responseOrders.ordJsonList!!.getProduct()
            )
            context.startActivity(intent)

        }

        holder.itemView.setOnClickListener {

            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("responseOrders", responseOrders)
            intent.putExtra("ordJson", responseOrders.ordJsonList)
            intent.putParcelableArrayListExtra(
                "productItem",
                responseOrders.ordJsonList!!.getProduct()
            )
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.image_icon)
        val title: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val details_button: RelativeLayout = itemView.findViewById(R.id.details_button)

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
