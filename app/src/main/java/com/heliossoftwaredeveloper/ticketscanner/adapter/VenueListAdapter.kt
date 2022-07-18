/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.heliossoftwaredeveloper.ticketscanner.R
import com.heliossoftwaredeveloper.ticketscanner.databinding.VenueItemBinding
import com.heliossoftwaredeveloper.ticketscanner.model.VenueResult

class VenueListAdapter(private val trackListAdapterListener : VenueListAdapterListener) :
    RecyclerView.Adapter<VenueListAdapter.VenueViewHolder>() {

    private var list: List<VenueResult> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val binding: VenueItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.venue_item, parent, false)
        return VenueViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.binding.item = list[position]
        holder.binding.executePendingBindings()
        holder.binding.trackCardView.setOnClickListener {
            trackListAdapterListener.onVenueSelect(list[position])
        }
    }

    fun updateDataSet(list: List<VenueResult>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class VenueViewHolder(val binding: VenueItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface VenueListAdapterListener {
        fun onVenueSelect(item: VenueResult)
    }
}