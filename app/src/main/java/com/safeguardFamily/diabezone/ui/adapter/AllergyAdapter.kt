package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.model.response.Allergy

class AllergyAdapter(items: List<Allergy>) :
    RecyclerView.Adapter<AllergyAdapter.ViewHolder>() {

    private val mItems: List<Allergy>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_allergy, parent, false
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
        private val tvAllergyTitle: TextView
        private val tvName: TextView
        private val tvDate: TextView
        private val tvTime: TextView
        private val tvHospital: TextView
        private val tvSeverity: TextView
        private val tvReaction: TextView
        private val ivAlert: ImageView
        private val llSeverity: LinearLayout
        private val llReaction: LinearLayout
        fun setOnBoardingData(item: Allergy) {
            tvAllergyTitle.text = item.title
            tvName.text = item.preparedBy
            tvHospital.text = item.hospital
            tvSeverity.text = item.severity
            tvReaction.text = item.reaction
            tvDate.text = DateUtils.displayingDateFormatTwoFromAPIDateTime(item.reportDate!!)
            tvTime.text = DateUtils.displayingTimeFormat(item.reportDate!!)
            if (item.isAlert!!) {
                tvAllergyTitle.setTextColor(itemView.rootView.context.getColor(R.color.red))
                ivAlert.setImageDrawable(itemView.rootView.context.getDrawable(R.drawable.ic_red_drop))
            } else {
                tvAllergyTitle.setTextColor(itemView.rootView.context.getColor(R.color.blue))
                ivAlert.setImageDrawable(null)
            }
        }

        init {
            tvAllergyTitle = itemView.findViewById(R.id.tvAllergyTitle)
            tvName = itemView.findViewById(R.id.tvName)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvHospital = itemView.findViewById(R.id.tvHospital)
            tvSeverity = itemView.findViewById(R.id.tvSeverity)
            tvReaction = itemView.findViewById(R.id.tvReaction)
            ivAlert = itemView.findViewById(R.id.ivAlert)
            llSeverity = itemView.findViewById(R.id.llSeverity)
            llReaction = itemView.findViewById(R.id.llReaction)
        }
    }

    init {
        this.mItems = items
    }
}