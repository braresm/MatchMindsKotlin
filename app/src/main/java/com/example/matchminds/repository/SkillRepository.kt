package com.example.matchminds.repository

import com.example.matchminds.models.Account
import com.example.matchminds.models.Skill
import com.example.matchminds.models.SkillCategory
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SkillRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val skills: MutableList<Skill> = mutableListOf()

    init {
        // Initialize the repository with dummy data
        val backendCategories: MutableList<SkillCategory> = mutableListOf()
        backendCategories.add(SkillCategory(1, "Database management", 0))
        backendCategories.add(SkillCategory(2, "API development", 0))
        backendCategories.add(SkillCategory(3, "Authentication & Authorization", 0))
        backendCategories.add(SkillCategory(4, "Testing", 0))
        skills.add(Skill(1, "Backend", backendCategories))

        val gitCategories: MutableList<SkillCategory> = mutableListOf()
        gitCategories.add(SkillCategory(5, "Repository management", 0))
        gitCategories.add(SkillCategory(6, "Branching", 0))
        gitCategories.add(SkillCategory(7, "Merging", 0))
        gitCategories.add(SkillCategory(8, "Resolving conflicts", 0))
        skills.add(Skill(2, "Git", gitCategories))

        val communicationCategories: MutableList<SkillCategory> = mutableListOf()
        communicationCategories.add(SkillCategory(9, "Clarity & precision", 0))
        communicationCategories.add(SkillCategory(10, "Team collaboration", 0))
        communicationCategories.add(SkillCategory(11, "Presentation skills", 0))
        communicationCategories.add(SkillCategory(12, "Problem solving", 0))
        skills.add(Skill(3, "Communication", communicationCategories))

        val researchCategories: MutableList<SkillCategory> = mutableListOf()
        researchCategories.add(SkillCategory(13, "Requirement analysis", 0))
        researchCategories.add(SkillCategory(14, "Documentation", 0))
        researchCategories.add(SkillCategory(15, "Usability testing", 0))
        researchCategories.add(SkillCategory(16, "Competitive analysis", 0))
        skills.add(Skill(4, "Research", researchCategories))

        val frontendCategories: MutableList<SkillCategory> = mutableListOf()
        frontendCategories.add(SkillCategory(17, "UI/UX Design", 0))
        frontendCategories.add(SkillCategory(18, "Responsive design", 0))
        frontendCategories.add(SkillCategory(19, "Mobile design patterns", 0))
        frontendCategories.add(SkillCategory(20, "Design tools", 0))
        skills.add(Skill(5, "Frontend", frontendCategories))

        val uiuxCategories: MutableList<SkillCategory> = mutableListOf()
        uiuxCategories.add(SkillCategory(21, "User centered design", 0))
        uiuxCategories.add(SkillCategory(22, "Wireframing & Prototyping", 0))
        uiuxCategories.add(SkillCategory(23, "Visual design", 0))
        uiuxCategories.add(SkillCategory(24, "User flow & scenario", 0))
        skills.add(Skill(6, "UI/UX", uiuxCategories))

    }

    fun saveSkillForStudent(
        account: Account, skill: Skill, callback: (String?, String?) -> Unit
    ) {
        val skillsCollection =
            firestore.collection("accounts").document(account.id).collection("skills")

        val skillCategoriesMap = emptyMap<String, Long>().toMutableMap()

        for (category in skill.categories) {
            skillCategoriesMap[category.id.toString()] = category.level
        }

        skillsCollection.document(skill.id.toString()).set(
            mapOf(
                "name" to skill.name, "categories" to skillCategoriesMap
            )
        ).addOnSuccessListener {
            callback("Skills saved successfully", null)
        }.addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    fun saveIctSkillForStudent(
        account: Account, value: String, callback: (String?, String?) -> Unit
    ) {
        val skillsCollection =
            firestore.collection("accounts").document(account.id).collection("skills")

        skillsCollection.document("ict").set(
            mapOf(
                "value" to value
            )
        ).addOnSuccessListener {
            callback("Skills saved successfully", null)
        }.addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    fun getSkillsForAccount(account: Account, callback: (List<Skill>, String?) -> Unit) {
        val defaultSkills = skills

        firestore.collection("/accounts/${account.id}/skills").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    if (document.exists() && document.id != "ict") {
                        val data = document.data
                        val savedCategories = data?.get("categories") as Map<String, Long>

                        for (ds in defaultSkills) {
                            if (ds.id.toString() == document.id) {
                                for (category in ds.categories) {
                                    category.level = savedCategories[category.id.toString()] ?: 0
                                }
                            }
                        }

                    }
                }
                callback(defaultSkills, null)
            }.addOnFailureListener { e ->
                callback(emptyList(), "Error in get skills: ${e.message}")
            }
    }

    fun getIctSkillsForAccount(account: Account, callback: (String?, String?) -> Unit) {
        val docRef: DocumentReference = firestore.collection("accounts/${account.id}/skills").document("ict")
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                callback(documentSnapshot.data?.get("value") as String?, null)
            }
            .addOnFailureListener { e ->
                callback(null, "Error in get ITC skill: ${e.message}")
            }

    }

    fun calculateCategoriesLevelAvg(skill: Skill): Double {
        val sumOfLevels = skill.categories.sumOf { it.level }
        return 1.0 * sumOfLevels / skill.categories.size
    }
}