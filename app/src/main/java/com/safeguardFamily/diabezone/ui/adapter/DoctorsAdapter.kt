package com.safeguardFamily.diabezone.ui.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.KEY_DOCTOR
import com.safeguardFamily.diabezone.databinding.ItemDoctorBinding
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.ui.activity.DoctorDetailsActivity

class DoctorsAdapter(
    private val mList: List<Provider>
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    inner class DoctorViewHolder(
        private val binding: ItemDoctorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Provider) {
            binding.item = model
            binding.rlDiabetes.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(KEY_DOCTOR, Gson().toJson(model))
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        DoctorDetailsActivity::class.java
                    ).putExtras(bundle)
                )
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Go to Doctor details screen from Appointment Screen ${model.puid}")
                }
            }
            binding.llDays.removeAllViews()
            addView("M ", model.timings.days!!.mon!!.length > 2)
            addView("T ", model.timings.days!!.tue!!.length > 2)
            addView("W ", model.timings.days!!.wed!!.length > 2)
            addView("T ", model.timings.days!!.thu!!.length > 2)
            addView("F ", model.timings.days!!.fri!!.length > 2)
            addView("S ", model.timings.days!!.sat!!.length > 2)
            addView("S ", model.timings.days!!.sun!!.length > 2)
        }

        private fun addView(text: String, b: Boolean) {
            val textView = TextView(binding.root.context)
            textView.text = text
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = params
            if (b) textView.setTextColor(binding.root.context.getColor(R.color.blue))
            else textView.setTextColor(binding.root.context.getColor(R.color.red))
            textView.textSize = 15F
            binding.llDays.addView(textView)
        }
    }
}