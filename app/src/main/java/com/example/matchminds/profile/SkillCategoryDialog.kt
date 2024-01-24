package com.example.matchminds.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.matchminds.R
import com.example.matchminds.models.Skill

class SkillCategoryDialog(
    context: Context,
    private val skill: Skill,
    private val onSaveButtonClick: (Double) -> Unit // Callback for passing sum
    ) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_skill_category)

        val titleTextView = findViewById<TextView>(R.id.dialogTitle)
        titleTextView.text = "Skill Categories"

        // Buttons
        val categoryButtonsLayout = findViewById<LinearLayout>(R.id.categoryButtonsLayout)
        createCategoryButtons(categoryButtonsLayout)

        // Save button
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val sumOfLevels = skill.categories.sumOf { it.level }
            val avgOfLevels = 1.0 * sumOfLevels / skill.categories.size

            // Pass the average of levels back in the com.example.matchminds.skills.SkillAdapter
            onSaveButtonClick(avgOfLevels)

            dismiss() // Close the dialog
        }

    }

    private fun createCategoryButtons(layout: LinearLayout) {
        for (index in skill.categories.indices) {
            val category = skill.categories[index]
            val button = Button(context)
            button.id = index
            button.text = category.name

            if(category.level == 1L) {
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.skill_bad))
            }
            if(category.level == 2L) {
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.skill_neutral))
            }
            if(category.level == 3L) {
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.skill_good))
            }

            button.setOnClickListener {
                showCategoryOptions(index)
            }
            layout.addView(button)
        }
    }

    private fun showCategoryOptions(categoryIndex: Int) {
        val selectedCategory = skill.categories[categoryIndex]
        val button = findViewById<Button>(categoryIndex)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Set Level for ${selectedCategory.name}")
            .setItems(arrayOf("Bad", "Neutral", "Good")) { _, which ->
                when (which) {
                    0 -> {
                        selectedCategory.level = 1
                        button.setBackgroundColor(ContextCompat.getColor(context, R.color.skill_bad))
                    }
                    1 -> {
                        selectedCategory.level = 2
                        button.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.skill_neutral
                        ))
                    }
                    2 -> {
                        selectedCategory.level = 3
                        button.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.skill_good
                        ))
                    }
                }
            }
            .create()
        dialog.show()
    }

}
