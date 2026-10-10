package com.example.data.model

enum class MemeLayoutType {
    CLASSIC_TOP_BOTTOM,  // Impact text over image top & bottom
    HEADER_CAPTION,      // Modern meme: white box header on top with image below
    TWO_PANEL_VERTICAL,  // Top panel vs bottom panel
    TWO_PANEL_SIDE,      // Left panel vs right panel
    DRAKE_COMPARISON,    // Disapprove vs Approve panels
    FOUR_PANEL_GRID,     // 4 quadrant meme
    CHAT_STYLE           // Messaging bubble punchline
}

data class MemeTextBox(
    val id: String = java.util.UUID.randomUUID().toString(),
    var text: String = "",
    var xPercent: Float = 0.5f,
    var yPercent: Float = 0.5f,
    var fontSizeSp: Float = 24f,
    var textColor: Long = 0xFFFFFFFF,
    var strokeColor: Long = 0xFF000000,
    var isBold: Boolean = true,
    var isAllCaps: Boolean = true,
    var textAlign: String = "center", // "left", "center", "right"
    var isVisible: Boolean = true,
    var isLocked: Boolean = false
)

data class MemeTemplate(
    val id: String,
    val title: String,
    val category: String,
    val layoutType: MemeLayoutType,
    val defaultTopText: String = "",
    val defaultBottomText: String = "",
    val sampleGraphicType: String = "smirk", // for offline built-in visual
    val panels: Int = 1,
    val panel1Header: String = "",
    val panel2Header: String = ""
)

object MemeTemplateRepository {

    val CATEGORIES = listOf(
        "All",
        "Funny & Comedy",
        "Relatable Life",
        "Work & Office",
        "Student & Exams",
        "Friendship & Dating",
        "Desi & Cricket",
        "Expectation vs Reality",
        "Sarcastic & Witty",
        "Gaming & Tech"
    )

    val TEMPLATES = listOf(
        MemeTemplate(
            id = "classic_impact",
            title = "Classic Top & Bottom",
            category = "Funny & Comedy",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "WHEN YOU FIX THE BUG",
            defaultBottomText = "ON THE FIRST TRY",
            sampleGraphicType = "smirk"
        ),
        MemeTemplate(
            id = "header_tweet",
            title = "Modern Header Meme",
            category = "Relatable Life",
            layoutType = MemeLayoutType.HEADER_CAPTION,
            defaultTopText = "Me pretending to listen after someone talks for more than 10 seconds:",
            defaultBottomText = "",
            sampleGraphicType = "shock"
        ),
        MemeTemplate(
            id = "exp_vs_reality",
            title = "Expectation vs Reality",
            category = "Expectation vs Reality",
            layoutType = MemeLayoutType.TWO_PANEL_VERTICAL,
            panel1Header = "EXPECTATION",
            panel2Header = "REALITY",
            defaultTopText = "Me planning to study at 8:00 AM",
            defaultBottomText = "Me waking up at 2:00 PM with regret",
            sampleGraphicType = "shock",
            panels = 2
        ),
        MemeTemplate(
            id = "drake_compare",
            title = "Dislike vs Approve",
            category = "Sarcastic & Witty",
            layoutType = MemeLayoutType.DRAKE_COMPARISON,
            panel1Header = "❌ Nah:",
            panel2Header = "✅ Yeah:",
            defaultTopText = "Sleeping at a reasonable hour",
            defaultBottomText = "Reorganizing my entire life at 3:17 AM",
            sampleGraphicType = "smart",
            panels = 2
        ),
        MemeTemplate(
            id = "office_meeting",
            title = "Meeting That Could Be An Email",
            category = "Work & Office",
            layoutType = MemeLayoutType.HEADER_CAPTION,
            defaultTopText = "Surviving another 1-hour meeting that could have been a 2-word Slack message:",
            defaultBottomText = "",
            sampleGraphicType = "smirk"
        ),
        MemeTemplate(
            id = "exam_syllabus",
            title = "Night Before Exams",
            category = "Student & Exams",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "SYLLABUS ENTERS THE CHAT",
            defaultBottomText = "ME WHO OPENED THE BOOK 5 MINS AGO",
            sampleGraphicType = "shock"
        ),
        MemeTemplate(
            id = "friends_plans",
            title = "Making Weekend Plans",
            category = "Friendship & Dating",
            layoutType = MemeLayoutType.HEADER_CAPTION,
            defaultTopText = "My friend: 'Let's meet at 5:00 sharp!'\nBoth of us at 5:30:",
            defaultBottomText = "",
            sampleGraphicType = "smart"
        ),
        MemeTemplate(
            id = "desi_cricket",
            title = "Last Over Drama",
            category = "Desi & Cricket",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "2 RUNS NEEDED IN 1 BALL",
            defaultBottomText = "ENTIRE NATION'S BLOOD PRESSURE 📈",
            sampleGraphicType = "shock"
        ),
        MemeTemplate(
            id = "four_stages",
            title = "4 Stages of Realization",
            category = "Relatable Life",
            layoutType = MemeLayoutType.FOUR_PANEL_GRID,
            defaultTopText = "Stage 1: Confident  |  Stage 2: Wait what\nStage 3: Panic  |  Stage 4: Acceptance",
            defaultBottomText = "",
            sampleGraphicType = "smart",
            panels = 4
        ),
        MemeTemplate(
            id = "gamer_rage",
            title = "Just One More Game",
            category = "Gaming & Tech",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "'LAST GAME AND THEN I GO TO BED'",
            defaultBottomText = "SUNLIGHT COMING THROUGH THE WINDOW",
            sampleGraphicType = "shock"
        )
    )

    fun getTemplatesByCategory(cat: String): List<MemeTemplate> {
        if (cat == "All") return TEMPLATES
        return TEMPLATES.filter { it.category == cat }
    }
}
