package com.example

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TvBrowseFragment : BrowseSupportFragment() {

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = "M4DiTV"
        
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
        
        val database = AppDatabase.getDatabase(requireContext())
        
        lifecycleScope.launch {
            val groups = database.channelDao().getGroups().first()
            groups.forEachIndexed { index, groupName ->
                // Basic setup for leanback rows
                val header = HeaderItem(index.toLong(), groupName)
                val listRowAdapter = ArrayObjectAdapter() // Needs a custom Presenter for TV Cards
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
        }
    }
}
