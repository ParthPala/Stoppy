package com.doublep.stoppy.home.others

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doublep.stoppy.R

class LapAdapter(private val lapTimes: List<String>,val context: Context) : RecyclerView.Adapter<LapAdapter.LapViewHolder>() {

    inner class LapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lapTimeText: TextView = itemView.findViewById(R.id.tv_rc_lap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lap, parent, false)
        return LapViewHolder(view)
    }

    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        val lapNum = lapTimes.size - position
        holder.lapTimeText.text = context.getString(R.string.item_lap_number,lapNum.toString(),lapTimes[position])
    }

    override fun getItemCount(): Int = lapTimes.size
}
