package com.safeguardFamily.diabezone.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.KEY_APPOINTMENT
import com.safeguardFamily.diabezone.common.DateUtils.formatTo12Hrs
import com.safeguardFamily.diabezone.common.DateUtils.fromAPIFormat
import com.safeguardFamily.diabezone.model.response.Appointment
import com.safeguardFamily.diabezone.ui.activity.ScheduleAppointmentActivity

class AppointmentAdapter(items: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.NotificationViewHolder?>() {

    private val mItems: List<Appointment>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.setOnBoardingData(mItems[position])
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView
        private val tvProfession: TextView
        private val tvRatingVal: TextView
        private val tvDate: TextView
        private val tvTime: TextView
        private val btJoinOnline: Button
        private val btReschedule: Button
        private val ivProfileImage: ImageView
        fun setOnBoardingData(item: Appointment) {
            tvName.text = item.provider.name
            tvProfession.text = item.provider.speciality
            tvRatingVal.text = item.provider.rating + " out of 5"
            tvDate.text = fromAPIFormat(item.booking_date)
            tvTime.text = formatTo12Hrs(item.slot)?.uppercase()
            Glide.with(itemView.context).load(item.provider.pic).into(ivProfileImage)
            btJoinOnline.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(item.provider.vchat_url)
                    )
                )
            }
            btReschedule.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(KEY_APPOINTMENT, Gson().toJson(item))
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        ScheduleAppointmentActivity::class.java
                    ).putExtras(bundle)
                )
            }
        }

        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvProfession = itemView.findViewById(R.id.tvProfession)
            tvRatingVal = itemView.findViewById(R.id.tvRatingVal)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            btJoinOnline = itemView.findViewById(R.id.btJoinOnline)
            btReschedule = itemView.findViewById(R.id.btReschedule)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage)
        }
    }

    init {
        this.mItems = items
    }
}