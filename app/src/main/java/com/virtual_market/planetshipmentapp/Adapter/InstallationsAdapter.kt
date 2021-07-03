package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virtual_market.planetshipmentapp.R

class InstallationsAdapter(private val context: Context,
                            private val responsePost: List<InstallationListModel>
) : RecyclerView.Adapter<InstallationsAdapter.viewholder>() {

    private lateinit var onClickListener: OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate = layoutInflater.inflate(R.layout.installation_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(image: String?, imageId: String?)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

        this.onClickListener=onClickListener

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val installationListModel = responsePost[position]

        Glide.with(context).load(installationListModel.Image).placeholder(R.drawable.ic_logo_brown).error(R.drawable.ic_logo_brown).into(holder.image_icon)

        holder.itemView.setOnClickListener {

            onClickListener.onClick(installationListModel.Image,installationListModel.InstImagesId)

        }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image_icon = itemView.findViewById<ImageView>(R.id.image_icon)

    }

}