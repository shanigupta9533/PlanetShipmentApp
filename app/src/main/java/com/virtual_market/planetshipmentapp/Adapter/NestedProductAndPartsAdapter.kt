package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.Modal.ProductWithSubProduct
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.Modal.SerialDetailsModal
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R

class NestedProductAndPartsAdapter(
    private val context: Context,
    private val responsePost: List<SerialProductListModel>
) : RecyclerView.Adapter<NestedProductAndPartsAdapter.viewholder>() {

    private var scanningCode: String = ""
    private val fragmentActivity = context as FragmentActivity
    private var isScanning: Boolean = false
    private var isOrderDetails: Boolean = false
    private val productId: ArrayList<String> = ArrayList()
    private val checkedCheckList: ArrayList<ProductWithSubProduct> = ArrayList()
    private val responseUserLogin: ResponseUserLogin =
        (context.applicationContext as PlanetShippingApplication).responseUserLogin
    private val serialIds: ArrayList<String> = ArrayList()
    private var onClickListener: NestedProductAndPartsAdapter.OnClickListener?=null

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

        this.onClickListener = onClickListener

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val responseOrders = responsePost[position]

        holder.foreign_name.text = responseOrders.ForeignName
        holder.warehouse.text = responseOrders.Warehouse
        holder.item_code.text = responseOrders.ItemCode
        holder.order_no.text = "Order No : " + responseOrders.OrdCode
        holder.detail_name.text = responseOrders.DetailName
        holder.serial_number.text = responseOrders.SerialNumber

        try {
            holder.date.text = responseOrders.DeliveryDate!!.substring(0, 10)
        } catch (e: StringIndexOutOfBoundsException) {
            holder.date.text = responseOrders.DeliveryDate!!
        }

        try {
            holder.no_of_items.text = responseOrders.AllocQty!!.trim().toFloat().toInt().toString()
        } catch (e: NumberFormatException) {
            holder.no_of_items.text = responseOrders.AllocQty!!
        }

        Glide.with(context).load(responseOrders.MainImage).placeholder(R.drawable.ic_logo_brown)
            .error(R.drawable.ic_logo_brown).into(holder.image_icon)

        holder.bindView(position, responseOrders.Details, responseOrders, holder.parent_of_parent)

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var recyclerView: RecyclerView? = null
        val foreign_name: TextView = itemView.findViewById(R.id.foreign_name)
        val no_of_items: TextView = itemView.findViewById(R.id.no_of_items)
        val warehouse: TextView = itemView.findViewById(R.id.warehouse)
        val item_code: TextView = itemView.findViewById(R.id.item_code)
        val order_no: TextView = itemView.findViewById(R.id.order_no)
        val date: TextView = itemView.findViewById(R.id.date)
        val detail_name: TextView = itemView.findViewById(R.id.detail_name)
        val serial_number: TextView = itemView.findViewById(R.id.serial_number)
        val parent_of_parent: LinearLayout = itemView.findViewById(R.id.parent_of_parent)
        val image_icon: ImageView = itemView.findViewById(R.id.image_icon)

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

        fun bindView(
            position: Int,
            parts: ArrayList<SerialDetailsModal>?,
            responseOrdersParent: SerialProductListModel,
            itemView: View
        ) {

            val showPartitianAdapter = ShowPartitianAdapter(context, parts!!)
            recyclerView!!.adapter = showPartitianAdapter
            showPartitianAdapter.productId(productId)
            showPartitianAdapter.setScanning(isScanning, scanningCode)

            // fitter jiska delivery status delivered ho usko red red kar do
            if ((responseUserLogin.Role.equals(
                    "Fitter",
                    true
                ) || responseUserLogin.Role.equals("Helper")) && responseOrdersParent.DeliveryStatus.equals(
                    "Delivered"
                )
            ) {

                parent_of_parent.setBackgroundColor(Color.parseColor("#ffcccb"))
                showPartitianAdapter.setOnClickListener(object :
                    ShowPartitianAdapter.OnClickListener {
                    override fun onClick(serialNumber: SerialDetailsModal) {
                    }

                    override fun deleteOnClick(serialNumber: SerialDetailsModal) {
                    }

                    override fun greenAllProduct(boolean: Boolean) {
                    }

                    override fun onScanning(responseOrders: SerialDetailsModal) {

                        android.os.Handler(Looper.myLooper()!!).postDelayed({

                            showPartitianAdapter.setScanning(false, "")
                            isScanning = false
                            notifyDataSetChanged()

                            Log.i("TAG", "onScanning: yes")

                            if (onClickListener != null)
                                onClickListener!!.onClick(true, position)

                        }, 500)

                    }


                })

                // Fitter jiska Shipped status Shipped ho usko red red kar do
            } else if (responseUserLogin.Role.equals("Stores") && responseOrdersParent.ShipStatus.equals(
                    "Shipped"
                )
            ) {
                parent_of_parent.setBackgroundColor(Color.parseColor("#ffcccb"))
                showPartitianAdapter.setOnClickListener(object :
                    ShowPartitianAdapter.OnClickListener {
                    override fun onClick(serialNumber: SerialDetailsModal) {
                    }

                    override fun deleteOnClick(serialNumber: SerialDetailsModal) {
                    }

                    override fun greenAllProduct(boolean: Boolean) {
                    }

                    override fun onScanning(responseOrders: SerialDetailsModal) {

                        android.os.Handler(Looper.myLooper()!!).postDelayed({

                            showPartitianAdapter.setScanning(false, "")
                            isScanning = false
                            notifyDataSetChanged()

                            if (onClickListener != null)
                                onClickListener!!.onClick(true, position)

                        }, 500)

                    }


                })

            } else {

                // in last sub product click par add kardo and green color change kar do

                showPartitianAdapter.setOnClickListener(object :
                    ShowPartitianAdapter.OnClickListener {

                    var productWithSubProduct = ProductWithSubProduct()

                    override fun onClick(responseOrders: SerialDetailsModal) {

                        Log.i("TAG", "onScanningClick: $position")

                        showPartitianAdapter.setScanning(false, "")
                        isScanning = false

                        if (!isOrderDetails) {

                            productId.add(responseOrders.SerialNumber!!)
                            serialIds.add(responseOrders.SerialId!!)
                            showPartitianAdapter.productId(productId)
                            notifyDataSetChanged()

                            productWithSubProduct.isProduct = false
                            productWithSubProduct.isSubProduct = true
                            productWithSubProduct.serialId = responseOrdersParent.SerialId!!
                            checkedCheckList.add(productWithSubProduct)

                        }

                    }

                    // in last sub product click par add kardo and delete karke color change kar do

                    override fun deleteOnClick(responseOrders: SerialDetailsModal) {

                        showPartitianAdapter.setScanning(false, "")
                        isScanning = false

                        if (!isOrderDetails) {

                            productId.remove(responseOrders.SerialNumber!!)
                            serialIds.remove(responseOrders.SerialId!!)
                            showPartitianAdapter.productId(productId)
                            notifyDataSetChanged()

                            productWithSubProduct.serialId = responseOrdersParent.SerialId!!
                            checkedCheckList.remove(productWithSubProduct)

                        }
                    }

                    // in last sub product total green ho toh product bhi green kar do

                    override fun greenAllProduct(boolean: Boolean) {

                        if (!isOrderDetails) {

                            if (boolean) {

                                productId.add(responseOrdersParent.SerialNumber!!)
                                serialIds.add(responseOrdersParent.SerialId!!)
                                parent_of_parent.setBackgroundColor(Color.parseColor("#00ff00"))
                                showPartitianAdapter.productId(productId)

                                checkedCheckList.forEach {
                                    if (responseOrdersParent.SerialId == it.serialId) {
                                        it.isProduct = true
                                    }
                                }

                            } else {

                                productId.remove(responseOrdersParent.SerialNumber!!)
                                serialIds.remove(responseOrdersParent.SerialId!!)
                                parent_of_parent.setBackgroundColor(Color.parseColor("#ffffff"))
                                showPartitianAdapter.productId(productId)

                                checkedCheckList.forEach {
                                    if (responseOrdersParent.SerialId == it.serialId) {
                                        it.isProduct = false
                                    }
                                }

                            }

                        }
                    }

                    override fun onScanning(responseOrders: SerialDetailsModal) {

                        if (!isOrderDetails) {

                            serialIds.add(responseOrders.SerialId!!)
                            showPartitianAdapter.productId(productId)

                            fragmentActivity.runOnUiThread {

                                android.os.Handler(Looper.myLooper()!!).postDelayed({

                                    showPartitianAdapter.setScanning(false, "")
                                    isScanning = false
                                    notifyDataSetChanged()

                                    if (onClickListener != null)
                                        onClickListener!!.onClick(true, position)

                                }, 500)

                            }

                            productWithSubProduct.isProduct = false
                            productWithSubProduct.isSubProduct = true
                            productWithSubProduct.serialId = responseOrdersParent.SerialId!!
                            checkedCheckList.add(productWithSubProduct)

                        }

                        showPartitianAdapter.setScanning(false, "")
                        isScanning = false

                    }

                })
            }

        }


    }

    fun setProductId(productId: ArrayList<String>) {

        this.productId.clear()
        this.productId.addAll(productId)

    }

    fun getArrayList(): ArrayList<String> {

        return productId

    }

    fun getCheckedArrayList(): ArrayList<ProductWithSubProduct> {

        return checkedCheckList

    }


    fun getSerialId(): ArrayList<String> {

        return serialIds

    }

    fun isOrderdetails(b: Boolean) {

        isOrderDetails = b

    }

    fun setScanningStart(b: Boolean, text: String) {

        isScanning = b
        scanningCode = text

    }

}