package com.sk.tripsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RideAdapter(private val rides: List<Ride>) :
    RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ride_item, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride)
    }

    override fun getItemCount(): Int = rides.size

    class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_name)
        private val textStartEnd: TextView = itemView.findViewById(R.id.text_start_end)
        private val textFare: TextView = itemView.findViewById(R.id.text_fare)
        private val textDate: TextView = itemView.findViewById(R.id.text_date)

        fun bind(ride: Ride) {
            textName.text = "Name: ${ride.name}"
            textStartEnd.text = "Start: ${ride.startLocation} - End: ${ride.endLocation}"
            textFare.text = "Fare: ${ride.fare}"
            textDate.text = "Date: ${ride.date}"
        }
    }
}
