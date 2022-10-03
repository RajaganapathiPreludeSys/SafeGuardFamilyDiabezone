package com.safeguardFamily.diabezone.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormat
import com.safeguardFamily.diabezone.common.DateUtils.formatTo12Hrs
import com.safeguardFamily.diabezone.common.DateUtils.splitDate
import com.safeguardFamily.diabezone.common.DateUtils.splitTime
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.databinding.DialogDateBinding
import com.safeguardFamily.diabezone.databinding.DialogDiabetesBinding
import com.safeguardFamily.diabezone.databinding.DialogTimeBinding
import com.safeguardFamily.diabezone.databinding.ItemDiabetesBinding
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest
import com.safeguardFamily.diabezone.model.response.Log
import com.safeguardFamily.diabezone.ui.activity.DoctorDetailsActivity
import com.safeguardFamily.diabezone.ui.activity.SubscriptionActivity
import java.util.*

class DiabetesAdapter(items: List<Log>, onDone: ((request: DiabetesLogRequest) -> Unit)) :
    RecyclerView.Adapter<DiabetesAdapter.DiabetesViewHolder?>() {

    private val mItems: List<Log>
    private val mOnDone: (request: DiabetesLogRequest) -> Unit

    private lateinit var binding: ItemDiabetesBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiabetesViewHolder {
        binding = ItemDiabetesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiabetesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiabetesViewHolder, position: Int) =
        holder.bind(mItems[position], mOnDone)

    override fun getItemCount() = mItems.size

    class DiabetesViewHolder(private val binding: ItemDiabetesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var dateString = ""
        var timeString24 = ""
        private lateinit var dialogBinding: DialogDiabetesBinding
        fun bind(item: Log, mOnDone: (request: DiabetesLogRequest) -> Unit) {
            dateString = splitDate(item.measureDate!!)!!
            timeString24 = splitTime(item.measureDate!!)!!
            binding.ivEdit.bringToFront()
            binding.ivEdit.setOnClickListener {
                editDiabetes(itemView.context, item) {
                    mOnDone(it)
                }
            }
            binding.tvDate.text = displayingDateFormat(item.measureDate!!)
            binding.etBloodSugar.text = "${item.logValue} mg/dL"
            binding.tvTime.text = formatTo12Hrs(timeString24)
            binding.tvContactHealchCoach.text = item.message


            Glide.with(itemView.context).load(
                when (item.status) {
                    "hyper" -> R.drawable.ic_red_up_arrow
                    "hypo" -> R.drawable.ic_red_down_arrow
                    "normal" -> null
                    else -> null
                }
            ).into(binding.ivBloodSugar)

            when (item.status) {
                "hyper" -> {
                    binding.tvStatusRed.text = "Hyper"
                    binding.llHyperContainer.visibility = View.VISIBLE
                    binding.llNormalContainer.visibility = View.GONE
                }
                "hypo" -> {
                    binding.tvStatusRed.text = "Hypo"
                    binding.llHyperContainer.visibility = View.VISIBLE
                    binding.llNormalContainer.visibility = View.GONE
                }
                "normal" -> {
                    binding.llHyperContainer.visibility = View.GONE
                    binding.llNormalContainer.visibility = View.VISIBLE
                }
            }

            binding.llHyperContainer.setOnClickListener {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Contact health coach from diabetes logs ${item.lid}")
                }
                if (SharedPref.isMember()) {
                    val bundle = android.os.Bundle()
                    bundle.putString(
                        Bundle.KEY_DOCTOR,
                        SharedPref.read(SharedPref.Pref.PrefHealthCoach, "")
                    )
                    bundle.putString(Bundle.KEY_TITLE, "Health Coach")
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            DoctorDetailsActivity::class.java
                        ).putExtras(bundle)
                    )
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Only members can contact their Health Coach",
                        Toast.LENGTH_LONG
                    ).show()
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            SubscriptionActivity::class.java
                        )
                    )
                }
            }

            binding.tvType.text = when (item.period) {
                "before_meal" -> "Fasting"
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

            dialogBinding =
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

            val items = arrayOf("Fasting", "After Meal", "Random")

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
            dialogBinding.tvDate.text = "${displayingDateFormat(dateString)}"
            dialogBinding.tvTime.text = "${formatTo12Hrs(timeString24)}"
            dialogBinding.etBloodSugar.setText(item.logValue)

            dialogBinding.btCancel.setOnClickListener { mDialog.dismiss()
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.CONTENT, "Dismiss edit diabetes dialog ${item.lid}")
            } }
            dialogBinding.tlDateContainer.setOnClickListener { showDateDialog(itemView.context)
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Diabetes log Date picker ${item.lid}")
                } }
            dialogBinding.tlTimeContainer.setOnClickListener { showTimeDialog(itemView.context)
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Diabetes log time picker ${item.lid}")
                } }
            dialogBinding.etBloodSugar.addTextChangedListener {
                dialogBinding.tlBloodSugarContainer.setBackgroundResource(R.drawable.bg_blue_border)
            }
            dialogBinding.btAddLog.setOnClickListener {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Diabetes log adding item ${item.lid}")
                }
                if (dialogBinding.etBloodSugar.text?.isNotEmpty() == false ||
                    dialogBinding.etBloodSugar.text.toString().toInt() <= 0
                ) {
                    dialogBinding.tlBloodSugarContainer.setBackgroundResource(R.drawable.bg_red_border)
                    Toast.makeText(
                        itemView.context,
                        "Enter a valid blood sugar value",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    android.util.Log.d(TAG, "editDiabetes: $dateString$timeString24")
                    onDone(
                        DiabetesLogRequest(
                            lid = item.lid!!,
                            measureDate = "$dateString $timeString24",
                            logValue = dialogBinding.etBloodSugar.text.toString().toInt(),
                            uid = SharedPref.getUserId()!!,
                            period = when (dialogBinding.spType.selectedItem) {
                                "Fasting" -> "before_meal"
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

        private fun showDateDialog(mContext: Context) {
            val dialogDateBinding: DialogDateBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.dialog_date,
                    null,
                    false
                )

            val mDialog = AlertDialog.Builder(mContext, 0).create()

            mDialog.apply {
                setView(dialogDateBinding.root)
                setCancelable(false)
            }.show()

            val mCalendar = Calendar.getInstance()
            dialogDateBinding.datePicker.maxDate = mCalendar.timeInMillis

            dialogDateBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}"
            dialogDateBinding.datePicker.init(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            ) { _, year, month, day ->
                val m = month + 1
                dateString = "$year-$m-$day"
                dialogDateBinding.tvSelectedVal.text = "${displayingDateFormat(dateString)}"
            }

            dialogDateBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogDateBinding.btPickDate.setOnClickListener {
                dialogBinding.tvDate.text = "${displayingDateFormat(dateString)}"
                mDialog.dismiss()
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, " Diabetes log date picked")
                }
            }
        }

        private fun showTimeDialog(mContext: Context) {
            val dialogTimeBinding: DialogTimeBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.dialog_time,
                    null,
                    false
                )

            val mDialog = AlertDialog.Builder(mContext, 0).create()

            mDialog.apply {
                setView(dialogTimeBinding.root)
                setCancelable(false)
            }.show()
            timeString24 = "${formatTo12Hrs(timeString24)}"
            dialogTimeBinding.tvSelectedVal.text = timeString24
            dialogTimeBinding.timePicker.setOnTimeChangedListener { _, _hour, minute ->
                var hour = _hour
                val amPm: String
                when {
                    hour == 0 -> {
                        hour += 12
                        amPm = "AM"
                    }
                    hour == 12 -> amPm = "PM"
                    hour > 12 -> {
                        hour -= 12
                        amPm = "PM"
                    }
                    else -> amPm = "AM"
                }
                val hourString = if (hour < 10) "0$hour" else hour
                val min = if (minute < 10) "0$minute" else minute
                timeString24 = " $hourString:$min $amPm"
                dialogTimeBinding.tvSelectedVal.text = timeString24
                timeString24 = "${formatTo12Hrs(timeString24)}"
            }

            dialogTimeBinding.btCancel.setOnClickListener { mDialog.dismiss() }
            dialogTimeBinding.btPickTime.setOnClickListener {
                dialogBinding.tvTime.text = timeString24
                mDialog.dismiss()
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.CONTENT, "Diabetes log Time picked")
                }
            }
        }
    }

    init {
        this.mItems = items
        this.mOnDone = onDone
    }

}