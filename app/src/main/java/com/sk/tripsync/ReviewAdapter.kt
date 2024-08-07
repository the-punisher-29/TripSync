package com.sk.tripsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUserName: TextView = itemView.findViewById(R.id.text_user_name)
        private val textReview: TextView = itemView.findViewById(R.id.text_review)
        private val textRating: TextView = itemView.findViewById(R.id.text_rating)
        private val textDateReview: TextView = itemView.findViewById(R.id.text_date_review)

        fun bind(review: Review) {
            textUserName.text = "User: ${review.userName}"
            textReview.text = "Review: ${review.reviewText}"
            textRating.text = "Rating: ${review.rating}"
            textDateReview.text = "Date: ${review.date}"
        }
    }
}
