package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.data.model.Channel
import com.example.databinding.ItemChannelBinding

class ChannelAdapter(private val onChannelClick: (Channel) -> Unit) : RecyclerView.Adapter<ChannelAdapter.ViewHolder>() {

    private var channels: List<Channel> = emptyList()

    fun submitList(newChannels: List<Channel>) {
        channels = newChannels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]
        holder.binding.tvName.text = channel.name
        holder.binding.tvGroup.text = channel.groupName
        
        if (channel.isOnline) {
            holder.binding.badgeLive.visibility = View.VISIBLE
            holder.binding.badgeOff.visibility = View.GONE
        } else {
            holder.binding.badgeLive.visibility = View.GONE
            holder.binding.badgeOff.visibility = View.VISIBLE
        }
        
        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.binding.ivLogo.visibility = View.VISIBLE
            holder.binding.tvInitial.visibility = View.GONE
            holder.binding.ivLogo.load(channel.logoUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        } else {
            holder.binding.ivLogo.visibility = View.GONE
            holder.binding.tvInitial.visibility = View.VISIBLE
            holder.binding.tvInitial.text = channel.name.take(1).uppercase()
        }
        
        holder.itemView.setOnClickListener {
            onChannelClick(channel)
        }
    }

    override fun getItemCount() = channels.size

    class ViewHolder(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root)
}
