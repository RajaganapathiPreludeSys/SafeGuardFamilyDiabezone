package com.safeguardFamily.diabezone.ui.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.KEY_APPOINTMENT
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFromAPI
import com.safeguardFamily.diabezone.common.DateUtils.formatTo12Hrs
import com.safeguardFamily.diabezone.model.response.Appointment
import com.safeguardFamily.diabezone.ui.activity.ScheduleAppointmentActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AppointmentAdapter(items: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ViewHolder?>() {

    private val mItems: List<Appointment>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment, parent, false
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
        private val tvName: TextView
        private val tvProfession: TextView
        private val tvDate: TextView
        private val tvTime: TextView
        private val tvEducation: TextView
        private val btJoinOnline: Button
        private val btReschedule: Button
        private val ivProfileImage: ImageView
        fun setOnBoardingData(item: Appointment) {
            tvName.text = item.provider.name
            tvProfession.text = item.provider.speciality
            tvEducation.text = item.provider.education
            tvDate.text = displayingDateFromAPI(item.booking_date)
            tvTime.text = formatTo12Hrs(item.slot)?.uppercase()
            Glide.with(itemView.context).load(item.provider.pic).into(ivProfileImage)
            btJoinOnline.setOnClickListener {

                val parsedMillis = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).parse(item.booking_date)!!.time

                val c = Calendar.getInstance()
                c.timeInMillis = parsedMillis
                c.add(Calendar.MINUTE, -5)

                Log.d("RRR", "In the past...$parsedMillis -- ${System.currentTimeMillis()}")

                Log.d("RRR", "In the past...$parsedMillis -- ${c.timeInMillis}")

                val simple: DateFormat = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                )

                var result = Date(c.timeInMillis)
                Log.d("RRR", "Backend time ${simple.format(result)}")
                result = Date(System.currentTimeMillis())
                Log.d("RRR", "Current time ${simple.format(result)}")
                result = Date(c.timeInMillis)
                Log.d("RRR", "Current ---- ${Date(parsedMillis)} > ${Date(c.timeInMillis)}")
                Log.d("RRR", "Current ---- ${Date(System.currentTimeMillis()).after(Date(c.timeInMillis))} > ${c.timeInMillis}")

                if (Date(System.currentTimeMillis()).after(Date(c.timeInMillis)))
                    itemView.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(item.provider.vchat_url)
                        )
                    )
                else Toast.makeText(
                    itemView.context,
                    "You can only join at the allocated time slot",
                    Toast.LENGTH_LONG
                ).show()
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Join Appointment ${item.aid}")
                }
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
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Reschedule Appointment ${item.aid}")
                }
            }
        }

        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvProfession = itemView.findViewById(R.id.tvProfession)
            tvEducation = itemView.findViewById(R.id.tvEducation)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            btJoinOnline = itemView.findViewById(R.id.btJoinOnline)
            btReschedule = itemView.findViewById(R.id.btReschedule)
            ivProfileImage = itemView.findViewById(R.id.cvProfileImages)
        }
    }

    init {
        this.mItems = items
    }
}