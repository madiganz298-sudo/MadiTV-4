package com.example

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemGroupBinding

class GroupAdapter(private val onGroupSelected: (String) -> Unit) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    private var groups: List<String> = emptyList()
    private var selectedIndex = 0

    fun submitList(newGroups: List<String>) {
        groups = newGroups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.binding.tvGroupName.text = group
        
        holder.binding.tvGroupName.isSelected = position == selectedIndex
        
        holder.itemView.setOnClickListener {
            val oldIndex = selectedIndex
            selectedIndex = position
            notifyItemChanged(oldIndex)
            notifyItemChanged(selectedIndex)
            onGroupSelected(group)
        }
    }

    override fun getItemCount() = groups.size

    class ViewHolder(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root)
}
