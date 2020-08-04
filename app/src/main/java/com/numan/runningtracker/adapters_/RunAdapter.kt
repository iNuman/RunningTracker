package com.numan.runningtracker.adapters_

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.numan.runningtracker.R
import com.numan.runningtracker.db_.Run
import com.numan.runningtracker.other_.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RunAdapter.RunViewHolder>() {


    val diffCallback = object : DiffUtil.ItemCallback<Run>() {

        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run, parent, false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        when (holder) {
            is RunViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    class RunViewHolder constructor(
        itemView: View,
        private var interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Run?) = with(itemView) {

            item?.let {

                Glide.with(context)
                    .load(item.img)
                    .into(ivRunImage)

             val calendar = Calendar.getInstance().apply {
                 timeInMillis = item.timestamp
             }
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                tvDate.text = dateFormat.format(calendar.time)

                val avgSpeed = "${item.avgSpeedInKMH}km/h"
                tvAvgSpeed.text = avgSpeed
                val distanceInKm = "${item.distanceInMeters /1000f}km"
                tvDistance.text = distanceInKm
                tvTime.text = TrackingUtility.getFormattedStopWatchTime(item.timeInMillis)

                val caloriesBurned = "${item.caloriesBurned}kcal"
                tvCalories.text = caloriesBurned
            }

            parent_view.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Run?)
    }


}