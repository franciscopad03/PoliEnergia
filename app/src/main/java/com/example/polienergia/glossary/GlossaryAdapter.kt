package com.example.polienergia.glossary

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.polienergia.databinding.ItemGlossaryCardBinding

class GlossaryAdapter(private val terms: List<GlossaryTerm>) :
    RecyclerView.Adapter<GlossaryAdapter.GlossaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlossaryViewHolder {
        val binding = ItemGlossaryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GlossaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GlossaryViewHolder, position: Int) {
        holder.bind(terms[position])
    }

    override fun getItemCount(): Int = terms.size

    inner class GlossaryViewHolder(private val binding: ItemGlossaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(term: GlossaryTerm) {
            binding.glossaryImage.setImageResource(term.imageRes)
            binding.glossaryTitle.text = context.getString(term.title)

            // --- Interpretar HTML en la descripción ---
            val descriptionText = context.getString(term.description)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.glossaryDescription.text = Html.fromHtml(descriptionText, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                binding.glossaryDescription.text = Html.fromHtml(descriptionText)
            }
        }
    }
}
