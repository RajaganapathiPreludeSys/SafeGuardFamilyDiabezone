package com.safeguardFamily.diabezone.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.safeguardFamily.diabezone.common.Bundle.KEY_PROFESSOR
import com.safeguardFamily.diabezone.databinding.ItemDoctorBinding
import com.safeguardFamily.diabezone.model.response.Provider
import com.safeguardFamily.diabezone.ui.activity.AppointmentDoctorActivity

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
            Glide.with(itemView.context).load(model.pic).into(binding.ivProfileImages)
            binding.rlDiabetes.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(KEY_PROFESSOR, Gson().toJson(model))
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        AppointmentDoctorActivity::class.java
                    ).putExtras(bundle)
                )

            }
        }
    }
}