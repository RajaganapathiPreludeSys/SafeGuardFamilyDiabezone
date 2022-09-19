package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormatTwoFromAPIDateTime
import com.safeguardFamily.diabezone.model.response.Procedure

class ProcedureAdapter(items: List<Procedure>) :
    RecyclerView.Adapter<ProcedureAdapter.ViewHolder>() {

    private val mItems: List<Procedure>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_procedures, parent, false
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
        private val tvDate: TextView
        fun setOnBoardingData(item: Procedure) {
            tvTitle.text = item.title
            tvDate.text = displayingDateFormatTwoFromAPIDateTime(item.procedureDate!!)
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDate = itemView.findViewById(R.id.tvDate)
        }
    }

    init {
        this.mItems = items
    }
}