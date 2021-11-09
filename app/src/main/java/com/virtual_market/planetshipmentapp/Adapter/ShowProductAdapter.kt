package com.virtual_market.planetshipmentapp.Adapter;

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Activity.OrderDetailsActivity
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.R
import java.util.*

class ShowProductAdapter(
    private val context: Context,
    private val responsePost: List<ResponseOrders>
) : RecyclerView.Adapter<ShowProductAdapter.viewholder>(),Filterable {

    private var listFilter: List<ResponseOrders>? = null

    init {
        this.listFilter = responsePost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.show_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val responseOrders = listFilter!![position]

        val customers = responseOrders.ordJsonList!!.getCustomers()

        Glide.with(context).load(responseOrders.main_image).placeholder(R.drawable.ic_logo_brown)
            .error(R.drawable.ic_logo_brown)
            .into(holder.imageIcon)

        holder.order_no.text = "Order No. ${responseOrders.OrdCode}"
        holder.date.text = "Delivery Date : " + responseOrders.DeliveryDate
        holder.no_of_items.text = "${responseOrders.ItemCount}"

        holder.customer_name.text = customers!!.customerName

        holder.order_date.text = responseOrders.OrdDate!!

        holder.order_status.text = responseOrders.OrdStatus

        holder.details_button.setOnClickListener {

            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("responseOrders", responseOrders)
            intent.putExtra("ordJson", responseOrders.ordJsonList)
            intent.putExtra("customer_details", responseOrders.ordJsonList!!.getCustomers())
            intent.putParcelableArrayListExtra(
                "productItem",
                responseOrders.ordJsonList!!.getProduct()
            )

            if (responseOrders.Items!=null && responseOrders.Items!!.isNotEmpty())
                intent.putParcelableArrayListExtra("allItems", responseOrders.Items!!)

            context.startActivity(intent)

        }

        holder.itemView.setOnClickListener {

            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("responseOrders", responseOrders)
            intent.putExtra("ordJson", responseOrders.ordJsonList)
            intent.putExtra("customer_details", responseOrders.ordJsonList!!.getCustomers())

            intent.putParcelableArrayListExtra(
                "productItem",
                responseOrders.ordJsonList!!.getProduct()
            )

            if (responseOrders.Items!=null && responseOrders.Items!!.isNotEmpty())
                intent.putParcelableArrayListExtra("allItems", responseOrders.Items)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return listFilter!!.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.image_icon)
        val customer_name: TextView = itemView.findViewById(R.id.customer_name)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val details_button: RelativeLayout = itemView.findViewById(R.id.details_button)
        val order_date: TextView = itemView.findViewById(R.id.order_date)
        val order_status: TextView = itemView.findViewById(R.id.order_status)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listFilter = if (charString.isEmpty()) {
                    responsePost
                } else {
                    val filteredList: ArrayList<ResponseOrders> = ArrayList<ResponseOrders>()
                    for (row in responsePost) {
                        if (row.OrdCode!!.contains(charString.toLowerCase()) || row.OrdStatus!!.toLowerCase().contains(charString.toLowerCase())  || row.ordJsonList!!.getCustomers()!!.customerName!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listFilter
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                listFilter = filterResults.values as List<ResponseOrders>
                notifyDataSetChanged()
            }
        }
    }

}
