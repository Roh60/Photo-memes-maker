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
        "Gaming & Tech",
        "Couple & Breakup",
        "Family & Festivals",
        "POV & Daily Life",
        "Before & After",
        "Pets & Animals",
        "Motivation & Sarcasm"
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
        ,
        MemeTemplate(
            id = "couple_chat_misunderstanding",
            title = "Couple Chat Confusion",
            category = "Couple & Breakup",
            layoutType = MemeLayoutType.CHAT_STYLE,
            defaultTopText = "ME: I'M FINE",
            defaultBottomText = "ALSO ME: REPLAYS THE CONVERSATION 47 TIMES",
            sampleGraphicType = "crying"
        ),
        MemeTemplate(
            id = "family_function_food",
            title = "Family Function Food Mission",
            category = "Family & Festivals",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "GOING TO THE FAMILY FUNCTION",
            defaultBottomText = "RETURNING WITH 4 FOOD PACKETS",
            sampleGraphicType = "smirk"
        ),
        MemeTemplate(
            id = "pov_five_more_minutes",
            title = "POV: Five More Minutes",
            category = "POV & Daily Life",
            layoutType = MemeLayoutType.HEADER_CAPTION,
            defaultTopText = "POV: YOU SET 5 ALARMS AND SNOOZE ALL OF THEM",
            sampleGraphicType = "shock"
        ),
        MemeTemplate(
            id = "before_after_salary",
            title = "Before and After Salary",
            category = "Before & After",
            layoutType = MemeLayoutType.TWO_PANEL_VERTICAL,
            panel1Header = "SALARY DAY",
            panel2Header = "END OF THE MONTH",
            defaultTopText = "I CAN FINALLY SAVE MONEY",
            defaultBottomText = "WHO SPENT EVERYTHING?",
            sampleGraphicType = "crying",
            panels = 2
        ),
        MemeTemplate(
            id = "pet_owner_logic",
            title = "Pet Owner Logic",
            category = "Pets & Animals",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "I DON'T NEED ANY MORE PET TOYS",
            defaultBottomText = "BUYS ANOTHER ONE THE SAME DAY",
            sampleGraphicType = "smirk"
        ),
        MemeTemplate(
            id = "motivation_monday",
            title = "Monday Motivation Reality",
            category = "Motivation & Sarcasm",
            layoutType = MemeLayoutType.DRAKE_COMPARISON,
            panel1Header = "❌ MONDAY 6 AM",
            panel2Header = "✅ MONDAY 10 AM",
            defaultTopText = "BECOMING A NEW PERSON TODAY",
            defaultBottomText = "JUST ONE MORE COFFEE FIRST",
            sampleGraphicType = "smart",
            panels = 2
        ),
        MemeTemplate(
            id = "desi_guest_arrival",
            title = "Desi Guests Are Coming",
            category = "Family & Festivals",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "GUESTS: WE'RE 5 MINUTES AWAY",
            defaultBottomText = "FAMILY: STARTS A 2-HOUR CLEANING MISSION",
            sampleGraphicType = "shock"
        ),
        MemeTemplate(
            id = "exam_result_refresh",
            title = "Refreshing Exam Results",
            category = "Student & Exams",
            layoutType = MemeLayoutType.CLASSIC_TOP_BOTTOM,
            defaultTopText = "RESULTS WEBSITE FINALLY OPENS",
            defaultBottomText = "SERVER ERROR: TRY AGAIN LATER",
            sampleGraphicType = "crying"
        ),
        MemeTemplate(
            id = "gaming_one_last_round",
            title = "One Last Round",
            category = "Gaming & Tech",
            layoutType = MemeLayoutType.HEADER_CAPTION,
            defaultTopText = "FRIEND SAYS THIS IS THE LAST ROUND",
            defaultBottomText = "THREE HOURS LATER...",
            sampleGraphicType = "smirk"
        ),
        MemeTemplate(
            id = "friend_group_plans",
            title = "Friend Group Planning",
            category = "Friendship & Dating",
            layoutType = MemeLayoutType.TWO_PANEL_SIDE,
            panel1Header = "IN THE GROUP CHAT",
            panel2Header = "ON THE ACTUAL DAY",
            defaultTopText = "EVERYONE IS COMING",
            defaultBottomText = "ONLY TWO PEOPLE SHOW UP",
            sampleGraphicType = "shock",
            panels = 2
        )

    )

    fun getTemplatesByCategory(cat: String): List<MemeTemplate> {
        if (cat == "All") return TEMPLATES
        return TEMPLATES.filter { it.category == cat }
    }
}
