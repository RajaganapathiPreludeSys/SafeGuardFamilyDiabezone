package com.safeguardFamily.diabezone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateTimeFormat
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.DialogDateTimeBinding
import com.safeguardFamily.diabezone.databinding.DialogDiabetesBinding
import com.safeguardFamily.diabezone.databinding.ItemDiabetesBinding
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.model.response.Log
import java.util.*

class DiabetesAdapter(items: List<Log>, onDone: ((request: DiabetesLogRequest) -> Unit)) :
    RecyclerView.Adapter<DiabetesAdapter.DiabetesViewHolder?>() {

    private val mItems: List<Log>
    private val mOnDone: (request: DiabetesLogRequest) -> Unit

    private lateinit var binding: ItemDiabetesBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiabetesViewHolder {
        binding = ItemDiabetesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiabetesViewHolder(binding, mOnDone)
    }

    override fun onBindViewHolder(holder: DiabetesViewHolder, position: Int) =
        holder.bind(mItems[position], mOnDone)

    override fun getItemCount() = mItems.size

    class DiabetesViewHolder(
        private val binding: ItemDiabetesBinding,
        mOnDone: (request: DiabetesLogRequest) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Log, mOnDone: (request: DiabetesLogRequest) -> Unit) {
            binding.item = item
            binding.ivEdit.setOnClickListener {
                editDiabetes(itemView.context, item) {
                    mOnDone(it)
                }
            }
            binding.tvTime.text = displayingDateTimeFormat(item.measureDate!!)
            binding.tvType.text = when (item.period) {
                "before_meal" -> "Before Meal"
                "after_meal" -> "After Meal"
                "random" -> "Random"
                else -> "Random"
            }
        }

        private fun editDiabetes(
            mContext: Context,
            item: Log,
            onDone: ((request: DiabetesLogRequest) -> Unit)
        ) {
            var formattedDate = item.measureDate!!
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

            dialogBinding.spType.adapter =
                ArrayAdapter(mContext, android.R.layout.simple_list_item_1, items)
            dialogBinding.spType.setSelection(
                when (item.period) {
                    "before_meal" -> 0
                    "after_meal" -> 1
                    "random" -> 2
                    else -> 0
                }
            )
            dialogBinding.tvTimeValue.text = displayingDateTimeFormat(item.measureDate!!)
            dialogBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogBinding.tlTimeContainer.setOnClickListener {
                showCustomDialog(mContext, item) {
                    dialogBinding.tvTimeValue.text = it
                    formattedDate = DateUtils.displayingDateTimeFormatToAPIFormat(it)!!
                    android.util.Log.d(
                        TAG,
                        "editDiabetes: $it"
                    )
                    android.util.Log.d(
                        TAG,
                        "editDiabetes: ${DateUtils.displayingDateTimeFormatToAPIFormat(it)}"
                    )
                }
            }
            dialogBinding.btSubmit.setOnClickListener {
                if (dialogBinding.etBloodSugar.text?.isNotEmpty() == false ||
                    dialogBinding.etBloodSugar.text.toString().toInt() <= 0
                )
                    Toast.makeText(
                        itemView.context,
                        "Enter a valid blood sugar value",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    onDone(
                        DiabetesLogRequest(
                            lid = item.lid!!,
                            measureDate = formattedDate,
                            logValue = dialogBinding.etBloodSugar.text.toString().toInt(),
                            uid = SharedPref.getUserId()!!,
                            period = when (dialogBinding.spType.selectedItem) {
                                "Before Meal" -> "before_meal"
                                "After Meal" -> "after_meal"
                                "Random" -> "random"
                                else -> ""
                            }
                        )
                    )
                    mDialog.dismiss()
                }
            }

        }

        private fun showCustomDialog(
            mContext: Context,
            item: Log,
            onSelected: ((time: String) -> Unit)
        ) {
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
                dateString = "$year-$_month-$day"
                dialogBinding.tvSelectedVal.text =
                    if (dateString.length > 1) "${DateUtils.displayingDateFormat(dateString)}$timeString"
                    else timeString
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
                timeString = " $hourString:$min $am_pm"
                dialogBinding.tvSelectedVal.text =
                    if (dateString.length > 1) "${DateUtils.displayingDateFormat(dateString)}$timeString"
                    else timeString
            }

            dialogBinding.tvSelectedVal.text = displayingDateTimeFormat(item.measureDate!!)
            dialogBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogBinding.btPickDate.setOnClickListener {
                if (dateString.isEmpty()) Toast.makeText(
                    itemView.context,
                    "Select a valid date",
                    Toast.LENGTH_LONG
                ).show()
                else if (timeString.isEmpty()) Toast.makeText(
                    itemView.context,
                    "Select a valid time",
                    Toast.LENGTH_LONG
                ).show()
                else {
                    onSelected("${DateUtils.displayingDateFormat(dateString)}$timeString")
                    mDialog.dismiss()
                }
            }
        }
    }

    init {
        this.mItems = items
        this.mOnDone = onDone
    }

}