package com.safeguardFamily.diabezone.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
            }

//            binding.tvDays.text = model.timings.days
            val list = model.timings.days.split(" ")

            for (i in 1..7) {
                val textView = TextView(binding.root.context)
                textView.text = when (i) {
                    1 -> "M  "
                    2 -> "T  "
                    3 -> "W  "
                    4 -> "T  "
                    5 -> "F  "
                    6 -> "S  "
                    7 -> "S  "
                    else -> ""
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textView.layoutParams = params
                if (model.timings.days.contains(i.toString()))
                    textView.setTextColor(binding.root.context.getColor(R.color.blue))
                else textView.setTextColor(binding.root.context.getColor(R.color.red))
                textView.textSize = 15F
                binding.llDays.addView(textView)
            }
        }
    }
}