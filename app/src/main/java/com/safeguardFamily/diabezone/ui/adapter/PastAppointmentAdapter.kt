package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFromAPI
import com.safeguardFamily.diabezone.common.DateUtils.displayingTimeFromAPI
import com.safeguardFamily.diabezone.databinding.ItemPastAppointmentBinding
import com.safeguardFamily.diabezone.model.response.PastAppointment

class PastAppointmentAdapter(
    private val mList: List<PastAppointment>
) : RecyclerView.Adapter<PastAppointmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPastAppointmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    inner class ViewHolder(
        private val binding: ItemPastAppointmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: PastAppointment) {
            binding.item = model
            binding.tvDate.text = displayingDateFromAPI(model.booking_date!!)
            binding.tvTime.text = displayingTimeFromAPI(model.booking_date!!)
        }
    }
}