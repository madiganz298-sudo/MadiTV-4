package com.example.data.remote

import com.example.data.model.Channel
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object M3UParser {
    fun parse(inputStream: InputStream, playlistId: Int = 1): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        
        var currentName = ""
        var currentLogo = ""
        var currentGroup = "Uncategorized"
        
        // Regex patterns
        val nameRegex = Regex(",(.*)")
        val logoRegex = Regex("tvg-logo=\"([^\"]+)\"")
        val groupRegex = Regex("group-title=\"([^\"]+)\"")
        
        reader.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                // Parse metadata
                val nameMatch = nameRegex.find(trimmed)
                if (nameMatch != null) {
                    currentName = nameMatch.groupValues[1].trim()
                }
                
                val logoMatch = logoRegex.find(trimmed)
                if (logoMatch != null) {
                    currentLogo = logoMatch.groupValues[1]
                }
                
                val groupMatch = groupRegex.find(trimmed)
                if (groupMatch != null) {
                    currentGroup = groupMatch.groupValues[1]
                }
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                // It's a URL
                if (currentName.isNotEmpty()) {
                    channels.add(
                        Channel(
                            name = currentName,
                            logoUrl = currentLogo.ifEmpty { null },
                            groupName = currentGroup,
                            streamUrl = trimmed,
                            playlistId = playlistId
                        )
                    )
                }
                // Reset for next
                currentName = ""
                currentLogo = ""
                currentGroup = "Uncategorized"
            }
        }
        
        return channels
    }
}
