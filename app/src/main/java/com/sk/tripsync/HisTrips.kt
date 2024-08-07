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
    val isOngoing: Boolean,
    val price: Double // New property for price
)

class TripAdapter(private val trips: List<Trip>) :
    RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ride_item, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int = trips.size

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_name)
        private val textStartEnd: TextView = itemView.findViewById(R.id.text_start_end)
        private val textFare: TextView = itemView.findViewById(R.id.text_fare)
        private val textDate: TextView = itemView.findViewById(R.id.text_date)

        fun bind(trip: Trip) {
            textName.text = "Driver: ${trip.driverName}"
            textStartEnd.text = "Cab No: ${trip.cabNumber}"
            textFare.text = "Price: ₹${trip.price}" // Updated to show price
            textDate.text = "Status: ${if (trip.isOngoing) "Ongoing" else "Completed"}"
        }
    }
}
