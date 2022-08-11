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
import com.safeguardFamily.diabezone.model.DoctorModel
import com.safeguardFamily.diabezone.ui.activity.AppointmentDoctorActivity

class DoctorsAdapter(
    private val largeNewsList: List<DoctorModel>
) : RecyclerView.Adapter<DoctorsAdapter.ProfessorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessorsViewHolder {
        return ProfessorsViewHolder(
            ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfessorsViewHolder, position: Int) {
        holder.bind(largeNewsList[position])
    }

    override fun getItemCount(): Int = largeNewsList.size

    inner class ProfessorsViewHolder(
        private val binding: ItemDoctorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: DoctorModel) {
            binding.item = model
            Glide.with(itemView.context).load(model.image).into(binding.ivProfileImages)
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