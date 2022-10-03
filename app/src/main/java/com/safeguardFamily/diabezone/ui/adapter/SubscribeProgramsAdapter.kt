package com.safeguardFamily.diabezone.ui.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.databinding.ItemSubscribeProgramBinding
import com.safeguardFamily.diabezone.model.response.Package

class SubscribeProgramsAdapter(
    private val mList: List<Package>,
    private var onItemClicked: ((pack: Package) -> Unit)
) : RecyclerView.Adapter<SubscribeProgramsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubscribeProgramBinding.inflate(
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
        private val binding: ItemSubscribeProgramBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Package) {

            binding.tvTitle.text = model.pname
            binding.tvPrice.text = "|     \u20B9 ${model.price}"

            if (model.showButton!!) {
                binding.llSubscribe.visibility = View.VISIBLE
                binding.llMessage.visibility = View.GONE
            } else {
                binding.llSubscribe.visibility = View.GONE
                if (model.message!!.isNotEmpty()) {
                    binding.llMessage.visibility = View.VISIBLE
                    binding.tvMessage.text = model.message
                }
            }

            if (!model.isSubscribed!!) {
                binding.llSubscribe.visibility = View.VISIBLE
                binding.llMessage.visibility = View.GONE
            } else {
                binding.llSubscribe.visibility = View.GONE
                binding.llMessage.visibility = View.VISIBLE
                binding.tvMessage.text =
                    if (model.message!!.isNotEmpty()) model.message else "Already Subscribed"
            }

            binding.llSubscribe.setOnClickListener {
                onItemClicked(model)
            }

            model.content!!.forEach { it ->
                when (it.type!!) {
                    "paragraph" -> {
                        val textView = TextView(binding.root.context)
                        textView.text = Html.fromHtml(
                            "<b>${it.subHeading} : </b>${it.body}",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 20)
                        textView.layoutParams = params

                        textView.setTextColor(binding.root.context.getColor(R.color.black))
                        textView.textSize = 16F
                        binding.llContent.addView(textView)
                    }
                    "list" -> {
                        if (it.subHeading!!.isNotEmpty()) {
                            val textView = TextView(binding.root.context)
                            textView.text = Html.fromHtml(
                                "<b>${it.subHeading} : </b>",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            textView.layoutParams = params
                            textView.setTextColor(binding.root.context.getColor(R.color.black))
                            textView.textSize = 16F
                            binding.llContent.addView(textView)
                        }
                        it.list!!.forEach {
                            val textView = TextView(binding.root.context)
                            textView.text = "\u2727 $it"
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins(0, 0, 0, 20)
                            textView.layoutParams = params
                            textView.setTextColor(binding.root.context.getColor(R.color.black))
                            textView.textSize = 16F
                            binding.llContent.addView(textView)
                        }

                    }
                }
            }

        }
    }
}