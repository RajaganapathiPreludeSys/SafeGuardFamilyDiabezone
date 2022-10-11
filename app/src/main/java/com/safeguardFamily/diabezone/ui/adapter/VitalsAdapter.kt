package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.response.Vital

class VitalsAdapter(items: List<Vital>) :
    RecyclerView.Adapter<VitalsAdapter.ViewHolder>() {

    private val mItems: List<Vital>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_vital, parent, false
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
        private val tvTitle: TextView
        private val tvValue: TextView
        private val tvTitleEven: TextView
        private val tvValueEven: TextView
        private val llOdd: LinearLayout
        private val llEven: LinearLayout
        fun setOnBoardingData(item: Vital) {
            tvTitle.text = item.name
            tvValue.text = item.value
            tvTitleEven.text = item.name
            tvValueEven.text = item.value

            if (adapterPosition % 2 == 0) {
                llOdd.visibility = View.VISIBLE
                llEven.visibility = View.GONE
            } else {
                llOdd.visibility = View.GONE
                llEven.visibility = View.VISIBLE
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvValue = itemView.findViewById(R.id.tvValue)
            tvTitleEven = itemView.findViewById(R.id.tvTitleEven)
            tvValueEven = itemView.findViewById(R.id.tvValueEven)
            llOdd = itemView.findViewById(R.id.llOdd)
            llEven = itemView.findViewById(R.id.llEven)
        }
    }

    init {
        this.mItems = items
    }
}