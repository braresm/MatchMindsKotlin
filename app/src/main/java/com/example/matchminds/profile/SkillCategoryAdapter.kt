package com.example.matchminds.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.matchminds.R
import com.example.matchminds.models.SkillCategory

class SkillCategoryAdapter(private val categories: List<SkillCategory>) :
    RecyclerView.Adapter<SkillCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val categoryLevelTextView: TextView = itemView.findViewById(R.id.categoryLevelTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill_category, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryNameTextView.text = category.name
        holder.categoryLevelTextView.text = "Level: ${category.level}" // Customize as needed.
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
