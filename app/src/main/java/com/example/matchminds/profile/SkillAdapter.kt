package com.example.matchminds.profile

import android.content.Context
import androidx.core.content.ContextCompat
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.matchminds.R
import com.example.matchminds.models.Account
import com.example.matchminds.models.Skill
import com.example.matchminds.models.SkillCategory
import com.example.matchminds.repository.AuthRepository
import com.example.matchminds.repository.SkillRepository

class SkillAdapter(
    private val context: Context, private val skills: List<Skill>
) : RecyclerView.Adapter<SkillAdapter.ViewHolder>() {

    private val authRepository = AuthRepository()
    private val skillRepository = SkillRepository()
    private val currentAccount: Account? = authRepository.getCurrentAccount();

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillNameTextView: TextView = itemView.findViewById(R.id.skillNameTextView)
        val skillCheckBox: CheckBox = itemView.findViewById(R.id.skillCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_skill_checkbox, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = skills[position]
        holder.skillNameTextView.text = skill.name
        // You can set the initial state of the checkbox here if needed.
        holder.skillCheckBox.isChecked = false

        val levelsAvg = skillRepository.calculateCategoriesLevelAvg(skill)

        if(levelsAvg > 0) {
            holder.skillCheckBox.isChecked = true

            if (levelsAvg <= 1) {
                holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.skill_bad
                    )
                )
            } else if (levelsAvg <= 2) {
                holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.skill_neutral
                    )
                )
            } else {
                holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.skill_good
                    )
                )
            }
        }

        // Set up a click listener for the checkbox
        holder.skillCheckBox.setOnClickListener {
            holder.skillCheckBox.isChecked = true

            val selectedSkill = skills[position]
            val dialog = SkillCategoryDialog(holder.itemView.context, selectedSkill,
                onSaveButtonClick = { avgOfLevels ->
                    if (avgOfLevels <= 1) {
                        holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.skill_bad
                            )
                        )
                    } else if (avgOfLevels <= 2) {
                        holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.skill_neutral
                            )
                        )
                    } else {
                        holder.skillCheckBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.skill_good
                            )
                        )
                    }

                    if(currentAccount != null) {
                        skillRepository.saveSkillForStudent(currentAccount, selectedSkill) { success, error ->
                            if (success != null) {
                                Toast.makeText(context, success, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                })
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return skills.size
    }
}
