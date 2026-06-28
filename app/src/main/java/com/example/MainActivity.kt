package com.example

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.local.AppDatabase
import com.example.data.remote.M3UParser
import com.example.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    
    // Default playlist for the requirement
    private val DEFAULT_PLAYLIST_URL = "https://rizkyevory.github.io/merged_iptv_simple.m3u"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Auto route to TV if leanback is supported
        if (packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK)) {
            startActivity(Intent(this, TvMainActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)
        
        setupRecyclerViews()
        checkAndLoadDefaultPlaylist()
        observeData()
    }
    
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var channelAdapter: ChannelAdapter

    private fun setupRecyclerViews() {
        binding.rvGroups.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter { group ->
            loadChannelsByGroup(group)
        }
        binding.rvGroups.adapter = groupAdapter
        
        binding.rvChannels.layoutManager = GridLayoutManager(this, 2)
        channelAdapter = ChannelAdapter { channel ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("STREAM_URL", channel.streamUrl)
            }
            startActivity(intent)
        }
        binding.rvChannels.adapter = channelAdapter
    }

    private fun loadChannelsByGroup(group: String) {
        lifecycleScope.launch {
            database.channelDao().getChannelsByGroup(group).collectLatest { channels ->
                channelAdapter.submitList(channels)
            }
        }
    }

    private fun checkAndLoadDefaultPlaylist() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
            }
            
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(DEFAULT_PLAYLIST_URL).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful && response.body != null) {
                    val channels = M3UParser.parse(response.body!!.byteStream())
                    database.channelDao().insertChannels(channels)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            database.channelDao().getGroups().collectLatest { groups ->
                groupAdapter.submitList(groups)
                if (groups.isNotEmpty()) {
                    loadChannelsByGroup(groups[0])
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Implement search and other options
        return true
    }
}
