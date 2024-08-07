import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sk.tripsync.R
import org.osmdroid.views.overlay.Marker

data class Trip(
    val tripId: String,
    val driverName: String,
    val driverPhoneNumber: String,
    val cabNumber: String,
    val isOngoing: Boolean
)

class TripAdapter(private val trips: List<Trip>) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.tripIdTextView.text = "Trip ID: ${trip.tripId}"
        holder.driverNameTextView.text = "Driver: ${trip.driverName}"
        holder.driverPhoneNumberTextView.text = "Phone: ${trip.driverPhoneNumber}"
        holder.cabNumberTextView.text = "Cab No: ${trip.cabNumber}"

        if (trip.isOngoing) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E0F2F1")) // Light green background for ongoing trips
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE) // Default background
        }
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tripIdTextView: TextView = itemView.findViewById(R.id.text_trip_id)
        val driverNameTextView: TextView = itemView.findViewById(R.id.text_driver_name)
        val driverPhoneNumberTextView: TextView = itemView.findViewById(R.id.text_driver_phone_number)
        val cabNumberTextView: TextView = itemView.findViewById(R.id.text_cab_number)
    }
}
