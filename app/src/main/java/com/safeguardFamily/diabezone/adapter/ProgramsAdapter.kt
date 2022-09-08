package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R

class ProgramsAdapter(items: List<Int>) :
    RecyclerView.Adapter<ProgramsAdapter.ViewHolder?>() {

    private val mItems: List<Int>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_program_banner, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setOnBoardingData(mItems[position])
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBanner: ImageView
        fun setOnBoardingData(item: Int) {
            ivBanner.setImageDrawable(itemView.rootView.context.getDrawable(item))

        }

        init {
            ivBanner = itemView.findViewById(R.id.ivBanner)
        }
    }

    init {
        this.mItems = items
    }
}