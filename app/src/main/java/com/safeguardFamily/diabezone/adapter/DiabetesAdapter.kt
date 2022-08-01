package com.safeguardFamily.diabezone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.DialogDateTimeBinding
import com.safeguardFamily.diabezone.databinding.DialogDiabetesBinding
import com.safeguardFamily.diabezone.databinding.ItemDiabetesBinding
import com.safeguardFamily.diabezone.model.DiabetesModel
import java.util.*

class DiabetesAdapter(items: List<DiabetesModel>) :
    RecyclerView.Adapter<DiabetesAdapter.DiabetesViewHolder?>() {

    private val mItems: List<DiabetesModel>
    private lateinit var mContext: Context

    private lateinit var binding: ItemDiabetesBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiabetesViewHolder {
        mContext = parent.context
        binding = ItemDiabetesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiabetesViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: DiabetesViewHolder, position: Int) =
        holder.bind(mItems[position], mContext)

    override fun getItemCount() = mItems.size

    class DiabetesViewHolder(private val binding: ItemDiabetesBinding, context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiabetesModel, mContext: Context) {
            binding.item = item
            binding.ivEdit.setOnClickListener { editDiabetes(mContext, item) }
        }

        private fun editDiabetes(mContext: Context, item: DiabetesModel) {
            val dialogBinding: DialogDiabetesBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.dialog_diabetes,
                    null,
                    false
                )

            val mDialog = AlertDialog.Builder(mContext, 0).create()

            mDialog.apply {
                setView(dialogBinding.root)
                setCancelable(false)
            }.show()

            dialogBinding.item = item

            val items = arrayOf("Before Meal", "After Meal", "Random")
            val adapter =
                ArrayAdapter(mContext, android.R.layout.simple_list_item_1, items)
            dialogBinding.spType.adapter = adapter

            dialogBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogBinding.tlTimeContainer.setOnClickListener { showCustomDialog(mContext, item) }
        }

        private fun showCustomDialog(mContext: Context, item: DiabetesModel) {
            var dateString = ""
            var timeString = ""
            val timeValidator = Calendar.getInstance()
            val dialogBinding: DialogDateTimeBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.dialog_date_time,
                    null,
                    false
                )

            val mDialog = AlertDialog.Builder(mContext, 0).create()

            mDialog.apply {
                setView(dialogBinding.root)
                setCancelable(false)
            }.show()

            val mCalendar = Calendar.getInstance()
            dialogBinding.datePicker.maxDate = mCalendar.timeInMillis
            dialogBinding.datePicker.init(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            ) { view, year, month, day ->
                val _month = month + 1
                dateString = "$day/$_month/$year"
                dialogBinding.tvSelectedVal.text = "$dateString$timeString"
                timeValidator.set(year, _month, day)
            }

            dialogBinding.timePicker.setOnTimeChangedListener { tp, _hour, minute ->
                var hour = _hour
                var am_pm = ""
                when {
                    hour == 0 -> {
                        hour += 12
                        am_pm = "AM"
                    }
                    hour == 12 -> am_pm = "PM"
                    hour > 12 -> {
                        hour -= 12
                        am_pm = "PM"
                    }
                    else -> am_pm = "AM"
                }
                val hourString = if (hour < 10) "0$hour" else hour
                val min = if (minute < 10) "0$minute" else minute
                timeString = ", $hourString:$min $am_pm"
                dialogBinding.tvSelectedVal.text = "$dateString$timeString"

            }

            dialogBinding.tvSelectedVal.text = item.time
            dialogBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogBinding.btPickDate.setOnClickListener {

            }
        }
    }

    init {
        this.mItems = items
    }

}