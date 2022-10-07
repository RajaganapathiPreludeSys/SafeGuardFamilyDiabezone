package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.response.PersonalHabit

class HabitsAdapter(items: List<PersonalHabit>) :
    RecyclerView.Adapter<HabitsAdapter.ViewHolder>() {

    private val mItems: List<PersonalHabit>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_habit, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.setOnBoardingData(mItems[position])

    override fun getItemCount(): Int = mItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView
        private val tvStatus: TextView
        private val tvComment: TextView
        private val ivAlert: ImageView
        fun setOnBoardingData(item: PersonalHabit) {
            tvTitle.text = item.title
            tvStatus.text = "Status - ${item.status}"
            tvComment.text =
                if (item.comment!!.length > 1) "Comment - ${item.comment}" else "Comment - NIL"
            if (item.isAlert!!) {
                tvTitle.setTextColor(itemView.rootView.context.getColor(R.color.red))
                ivAlert.setImageDrawable(itemView.rootView.context.getDrawable(R.drawable.ic_red_drop))
            } else {
                tvTitle.setTextColor(itemView.rootView.context.getColor(R.color.black))
                ivAlert.setImageDrawable(null)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            tvComment = itemView.findViewById(R.id.tvComment)
            ivAlert = itemView.findViewById(R.id.ivAlert)
        }
    }

    init {
        this.mItems = items
    }
}