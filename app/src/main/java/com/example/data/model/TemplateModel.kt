package com.example.data.model

data class PhotoTemplate(
    val id: String,
    val name: String,
    val category: TemplateCategory,
    val subCategory: String,
    val headerTitle: String,
    val defaultGreeting: String,
    val footerQuote: String,
    val gradientColors: List<Long>,
    val frameShape: String = "rounded", // "rounded", "circle", "heart", "rect"
    val frameBorderColor: Long = 0xFFFFFFFF,
    val frameBorderWidthDp: Float = 4f,
    val aspectRatio: Float = 1.0f, // 1.0f (1:1), 0.8f (4:5), 0.5625f (9:16)
    val decorativeEmoji: String = "✨",
    val cornerDecorEmoji: String = "🎉",
    val textColor: Long = 0xFFFFFFFF
)

enum class TemplateCategory {
    FESTIVAL,
    STYLISH
}

object TemplateRepository {

    val FESTIVAL_TEMPLATES = listOf(
        PhotoTemplate(
            id = "diwali_royal",
            name = "Royal Shubh Deepavali",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Diwali",
            headerTitle = "🪔 SHUBH DEEPAVALI 🪔",
            defaultGreeting = "May the divine light illuminate your life with joy & prosperity!",
            footerQuote = "#HappyDiwali #FestivalOfLights",
            gradientColors = listOf(0xFF7F1D1D, 0xFFB45309, 0xFFF59E0B),
            frameShape = "rounded",
            frameBorderColor = 0xFFFDE047,
            decorativeEmoji = "🪔",
            cornerDecorEmoji = "✨"
        ),
        PhotoTemplate(
            id = "holi_colors",
            name = "Vibrant Holi Festival",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Holi",
            headerTitle = "🎨 HAPPY HOLI 🎨",
            defaultGreeting = "Splash of vibrant joy, love, and laughter in every color!",
            footerQuote = "#RangBarse #HoliHai",
            gradientColors = listOf(0xFFEC4899, 0xFF8B5CF6, 0xFF3B82F6),
            frameShape = "circle",
            frameBorderColor = 0xFFFFFFFF,
            decorativeEmoji = "🎨",
            cornerDecorEmoji = "🌈"
        ),
        PhotoTemplate(
            id = "raksha_bandhan",
            name = "Sweet Rakhi Bond",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Raksha Bandhan",
            headerTitle = "💫 HAPPY RAKSHA BANDHAN 💫",
            defaultGreeting = "Celebrating the eternal bond of love, care, and protection!",
            footerQuote = "#BestSiblingForever #RakhiLove",
            gradientColors = listOf(0xFF831843, 0xFFDB2777, 0xFFF472B6),
            frameShape = "rounded",
            frameBorderColor = 0xFFFDF2F8,
            decorativeEmoji = "🧵",
            cornerDecorEmoji = "💖"
        ),
        PhotoTemplate(
            id = "navratri_garba",
            name = "Navratri Garba Nights",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Navratri",
            headerTitle = "✨ SHUBH NAVRATRI ✨",
            defaultGreeting = "Nine nights of devotion, rhythm, and Maa Durga's divine blessings!",
            footerQuote = "#JaiMataDi #GarbaVibes",
            gradientColors = listOf(0xFF581C87, 0xFF9333EA, 0xFFF43F5E),
            frameShape = "rounded",
            frameBorderColor = 0xFFFDE047,
            decorativeEmoji = "🪘",
            cornerDecorEmoji = "🔥"
        ),
        PhotoTemplate(
            id = "ganesh_chaturthi",
            name = "Bappa Divine Blessings",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Ganesh Chaturthi",
            headerTitle = "🌺 GANPATI BAPPA MORYA 🌺",
            defaultGreeting = "May Lord Ganesha remove all obstacles and bestow peace!",
            footerQuote = "#MangalMurtiMorya #BappaBlessings",
            gradientColors = listOf(0xFF7C2D12, 0xFFEA580C, 0xFFFBBF24),
            frameShape = "rounded",
            frameBorderColor = 0xFFFEF08A,
            decorativeEmoji = "🌺",
            cornerDecorEmoji = "🕉️"
        ),
        PhotoTemplate(
            id = "eid_mubarak",
            name = "Eid Mubarak Gold",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Eid",
            headerTitle = "🌙 EID MUBARAK 🌙",
            defaultGreeting = "Wishing peace, boundless happiness, and prosperous blessings!",
            footerQuote = "#EidUlFitr #EidCelebration",
            gradientColors = listOf(0xFF064E3B, 0xFF059669, 0xFF10B981),
            frameShape = "circle",
            frameBorderColor = 0xFFFDE047,
            decorativeEmoji = "🌙",
            cornerDecorEmoji = "✨"
        ),
        PhotoTemplate(
            id = "christmas_magic",
            name = "Merry Christmas Cheer",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Christmas",
            headerTitle = "🎄 MERRY CHRISTMAS 🎄",
            defaultGreeting = "Warm wishes, peace on earth, and good cheer to you and family!",
            footerQuote = "#HoHoHo #MerryChristmas",
            gradientColors = listOf(0xFF881337, 0xFF14532D, 0xFF166534),
            frameShape = "rounded",
            frameBorderColor = 0xFFFFFFFF,
            decorativeEmoji = "🎄",
            cornerDecorEmoji = "❄️"
        ),
        PhotoTemplate(
            id = "new_year",
            name = "New Year Celebrations",
            category = TemplateCategory.FESTIVAL,
            subCategory = "New Year",
            headerTitle = "🎆 HAPPY NEW YEAR 🎆",
            defaultGreeting = "365 new days, 365 new chances! Make every moment count!",
            footerQuote = "#NewYearVibes #CheersToNewBeginnings",
            gradientColors = listOf(0xFF0F172A, 0xFF312E81, 0xFF4338CA),
            frameShape = "rounded",
            frameBorderColor = 0xFFFDE047,
            decorativeEmoji = "🥂",
            cornerDecorEmoji = "🎇"
        ),
        PhotoTemplate(
            id = "independence_day",
            name = "Tiranga Pride",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Independence Day",
            headerTitle = "🇮🇳 VANDE MATARAM 🇮🇳",
            defaultGreeting = "Saluting the brave heroes and celebrating the glory of our nation!",
            footerQuote = "#ProudIndian #JaiHind",
            gradientColors = listOf(0xFFEA580C, 0xFF1E293B, 0xFF16A34A),
            frameShape = "rounded",
            frameBorderColor = 0xFFFFFFFF,
            decorativeEmoji = "🇮🇳",
            cornerDecorEmoji = "🕊️"
        ),
        PhotoTemplate(
            id = "birthday_bash",
            name = "Special Birthday Wish",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Birthday",
            headerTitle = "🎂 HAPPY BIRTHDAY 🎂",
            defaultGreeting = "May your day be filled with sweet surprises, laughter, and love!",
            footerQuote = "#BirthdayCheers #MakeAWish",
            gradientColors = listOf(0xFF4C1D95, 0xFF7C3AED, 0xFFEC4899),
            frameShape = "circle",
            frameBorderColor = 0xFFFDE047,
            decorativeEmoji = "🎂",
            cornerDecorEmoji = "🎈"
        ),
        PhotoTemplate(
            id = "wedding_anniversary",
            name = "Golden Anniversary",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Anniversary",
            headerTitle = "💍 HAPPY ANNIVERSARY 💍",
            defaultGreeting = "Here's to another beautiful year of love, friendship, and togetherness!",
            footerQuote = "#ForeverTogether #CoupleGoals",
            gradientColors = listOf(0xFF451A03, 0xFF92400E, 0xFFD97706),
            frameShape = "rounded",
            frameBorderColor = 0xFFFEF3C7,
            decorativeEmoji = "🥂",
            cornerDecorEmoji = "❤️"
        ),
        PhotoTemplate(
            id = "good_morning_vibes",
            name = "Sunrise Good Morning",
            category = TemplateCategory.FESTIVAL,
            subCategory = "Good Morning",
            headerTitle = "☀️ GOOD MORNING ☀️",
            defaultGreeting = "Wake up with a grateful heart and let your light shine bright!",
            footerQuote = "#PositiveVibes #HaveABlessedDay",
            gradientColors = listOf(0xFFC2410C, 0xFFF59E0B, 0xFFFDE047),
            frameShape = "rounded",
            frameBorderColor = 0xFFFFFFFF,
            decorativeEmoji = "☕",
            cornerDecorEmoji = "🌻"
        )
    )

    val STYLISH_TEMPLATES = listOf(
        PhotoTemplate(
            id = "attitude_badass",
            name = "King Attitude",
            category = TemplateCategory.STYLISH,
            subCategory = "Attitude & Stylish",
            headerTitle = "🔥 BORN TO STAND OUT 🔥",
            defaultGreeting = "My silence is my attitude, my success is my noise.",
            footerQuote = "#KingStatus #Unstoppable",
            gradientColors = listOf(0xFF09090B, 0xFF27272A, 0xFF3F3F46),
            frameShape = "rect",
            frameBorderColor = 0xFFEF4444,
            frameBorderWidthDp = 5f,
            decorativeEmoji = "⚡",
            cornerDecorEmoji = "👑"
        ),
        PhotoTemplate(
            id = "romantic_love",
            name = "Pure Romance",
            category = TemplateCategory.STYLISH,
            subCategory = "Love & Romantic",
            headerTitle = "❤️ YOU ARE MY ALWAYS ❤️",
            defaultGreeting = "In all the world, there is no heart for me like yours.",
            footerQuote = "#MyHeart #TrueLove",
            gradientColors = listOf(0xFF831843, 0xFFBE185D, 0xFFFDA4AF),
            frameShape = "rounded",
            frameBorderColor = 0xFFFFF1F2,
            decorativeEmoji = "🌹",
            cornerDecorEmoji = "💖"
        ),
        PhotoTemplate(
            id = "couple_aesthetic",
            name = "Soulmate Forever",
            category = TemplateCategory.STYLISH,
            subCategory = "Couple Frames",
            headerTitle = "✨ TOGETHER IS MY FAVORITE PLACE ✨",
            defaultGreeting = "Two hearts, one soul, infinite unforgettable memories.",
            footerQuote = "#BetterTogether #Soulmates",
            gradientColors = listOf(0xFF312E81, 0xFF4338CA, 0xFFEC4899),
            frameShape = "circle",
            frameBorderColor = 0xFFFBCFE8,
            decorativeEmoji = "💍",
            cornerDecorEmoji = "✨"
        ),
        PhotoTemplate(
            id = "friendship_goals",
            name = "Best Friends Squad",
            category = TemplateCategory.STYLISH,
            subCategory = "Friendship & Family",
            headerTitle = "🤝 PARTNERS IN CRIME 🤝",
            defaultGreeting = "Real friends are like stars; you don't always see them, but they're always there!",
            footerQuote = "#YaariDosti #FriendsForLife",
            gradientColors = listOf(0xFF065F46, 0xFF0D9488, 0xFF2DD4BF),
            frameShape = "rounded",
            frameBorderColor = 0xFFCCFBF1,
            decorativeEmoji = "🤙",
            cornerDecorEmoji = "🎉"
        ),
        PhotoTemplate(
            id = "pro_profile",
            name = "Executive Portfolio",
            category = TemplateCategory.STYLISH,
            subCategory = "Professional Profile",
            headerTitle = "💼 LEADERSHIP & VISION 💼",
            defaultGreeting = "Empowering growth, innovation, and high-impact excellence.",
            footerQuote = "#Professional #ProfileSpotlight",
            gradientColors = listOf(0xFF0F172A, 0xFF1E293B, 0xFF334155),
            frameShape = "circle",
            frameBorderColor = 0xFF38BDF8,
            decorativeEmoji = "🎯",
            cornerDecorEmoji = "⭐"
        ),
        PhotoTemplate(
            id = "cinematic_teal",
            name = "Cinematic Mood",
            category = TemplateCategory.STYLISH,
            subCategory = "Cinematic & Aesthetic",
            headerTitle = "🎬 DIRECTORS CUT 🎬",
            defaultGreeting = "Every sunset brings the promise of a new cinematic dawn.",
            footerQuote = "#TealAndOrange #FilmLook",
            gradientColors = listOf(0xFF164E63, 0xFF0E7490, 0xFFEA580C),
            frameShape = "rect",
            frameBorderColor = 0xFFF59E0B,
            decorativeEmoji = "🎥",
            cornerDecorEmoji = "🎞️"
        ),
        PhotoTemplate(
            id = "cyber_neon",
            name = "Neon Cyberpunk",
            category = TemplateCategory.STYLISH,
            subCategory = "Neon & Modern",
            headerTitle = "⚡ RETRO CYBER WAVE ⚡",
            defaultGreeting = "Lost in neon dreams and electric midnight frequencies.",
            footerQuote = "#Cyberpunk #NeonVibes",
            gradientColors = listOf(0xFF050505, 0xFF581C87, 0xFFD946EF),
            frameShape = "rounded",
            frameBorderColor = 0xFF06B6D4,
            decorativeEmoji = "👾",
            cornerDecorEmoji = "⚡"
        ),
        PhotoTemplate(
            id = "magazine_vogue",
            name = "Vogue Style Cover",
            category = TemplateCategory.STYLISH,
            subCategory = "Magazine Cover",
            headerTitle = "🌟 THE CREATIVE ISSUE 🌟",
            defaultGreeting = "Fashion, authenticity, and fearless self expression.",
            footerQuote = "#CoverModel #TrendingStyle",
            gradientColors = listOf(0xFF18181B, 0xFF27272A, 0xFF52525B),
            frameShape = "rect",
            frameBorderColor = 0xFFFFFFFF,
            frameBorderWidthDp = 2f,
            decorativeEmoji = "💎",
            cornerDecorEmoji = "✨"
        )
    )

    val TRENDING_DESIGNS: List<PhotoTemplate> by lazy {
        listOf(
            getTemplateById("magazine_vogue") ?: STYLISH_TEMPLATES[0],
            getTemplateById("diwali_royal") ?: FESTIVAL_TEMPLATES[0],
            getTemplateById("neon_cyber") ?: STYLISH_TEMPLATES[1],
            getTemplateById("golden_hour") ?: STYLISH_TEMPLATES[2],
            getTemplateById("holi_colors") ?: FESTIVAL_TEMPLATES[1],
            getTemplateById("aesthetic_pastel") ?: STYLISH_TEMPLATES[3]
        )
    }

    val NEW_TEMPLATES: List<PhotoTemplate> by lazy {
        listOf(
            getTemplateById("new_year") ?: FESTIVAL_TEMPLATES[7],
            getTemplateById("cinematic_mood") ?: STYLISH_TEMPLATES[4],
            getTemplateById("good_morning_vibes") ?: FESTIVAL_TEMPLATES[11],
            getTemplateById("polaroid_vintage") ?: STYLISH_TEMPLATES[5],
            getTemplateById("birthday_bash") ?: FESTIVAL_TEMPLATES[9],
            getTemplateById("monochrome_minimal") ?: STYLISH_TEMPLATES[6]
        )
    }

    fun getAllTemplates(): List<PhotoTemplate> = FESTIVAL_TEMPLATES + STYLISH_TEMPLATES

    fun getTemplateById(id: String): PhotoTemplate? =
        getAllTemplates().firstOrNull { it.id == id }

    fun getRandomTemplate(): PhotoTemplate =
        getAllTemplates().random()

    fun getDailyInspiration(): PhotoTemplate {
        val all = getAllTemplates()
        if (all.isEmpty()) return FESTIVAL_TEMPLATES.first()
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return all[dayOfYear % all.size]
    }
}
