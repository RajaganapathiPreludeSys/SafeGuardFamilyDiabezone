package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.OnBoardingItem

class OnBoardingAdapter(OnBoardingItems: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder?>() {

    private val onBoardingItems: List<OnBoardingItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_boarding, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.setOnBoardingData(onBoardingItems[position])
    }

    override fun getItemCount(): Int {return onBoardingItems.size }

    inner class OnBoardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView
        private val imageOnBoarding: ImageView
        fun setOnBoardingData(OnBoardingItem: OnBoardingItem) {
            textTitle.text = OnBoardingItem.title
            imageOnBoarding.setImageResource(OnBoardingItem.image)
        }

        init {
            textTitle = itemView.findViewById(R.id.textTitle)
            imageOnBoarding = itemView.findViewById(R.id.imageOnBoarding)
        }
    }

    init {
        this.onBoardingItems = OnBoardingItems
    }
}