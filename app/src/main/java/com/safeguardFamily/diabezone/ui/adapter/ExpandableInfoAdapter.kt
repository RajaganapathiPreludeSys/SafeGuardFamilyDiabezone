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
import com.safeguardFamily.diabezone.databinding.ItemExpandableBinding
import com.safeguardFamily.diabezone.model.response.Info

class ExpandableInfoAdapter(
    private var info: List<Info>
) : RecyclerView.Adapter<ExpandableInfoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExpandableBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemExpandableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(info[position]) {
                binding.tvTitle.text = this.heading
                binding.expandedView.visibility = if (this.expand!!) View.VISIBLE else View.GONE

                binding.llContent.removeAllViews()
                this.body!!.forEach { it ->
                    when (it.type!!) {
                        "paragraph" -> {
                            if (it.subHeading!!.isNotEmpty()) {
                                val textView = TextView(binding.root.context)
                                textView.text = Html.fromHtml(
                                    "<b>${it.subHeading} </b>",
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

                            val textView = TextView(binding.root.context)
                            textView.text = it.body
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
                                    "<b>${it.subHeading} </b>",
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
                            it.list!!.forEach {
                                val textView = TextView(binding.root.context)
                                textView.text = "\u2727 ${it}"
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
                        "blank" -> {
                            val textView = TextView(binding.root.context)
                            textView.text = ""
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins(0, 0, 0, 10)
                            textView.layoutParams = params

                            textView.setTextColor(binding.root.context.getColor(R.color.black))
                            textView.textSize = 16F
                            binding.llContent.addView(textView)
                        }
                    }
                }

                binding.cardLayout.setOnClickListener {
                    this.expand = !this.expand!!
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = info.size
}
