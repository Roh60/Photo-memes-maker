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
    val textColor: Long = 0xFFFFFFFF,
    val isPremium: Boolean = false
)

enum class TemplateCategory {
    PREMIUM,
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

    val PREMIUM_TEMPLATES = listOf(
        PhotoTemplate(
            id = "royal_gold_wedding",
            name = "Royal Vivah Gold",
            category = TemplateCategory.PREMIUM,
            subCategory = "Wedding & Royalty",
            headerTitle = "👑 SHREE VIVAH UTSAV 👑",
            defaultGreeting = "Two souls, two hearts, joined in sacred celebration of lifelong love & harmony.",
            footerQuote = "#RoyalWedding #TogetherForever #GoldenBond",
            gradientColors = listOf(0xFF1E1035, 0xFF4A0E2E, 0xFFB45309, 0xFFFBBF24),
            frameShape = "circle",
            frameBorderColor = 0xFFFDE047,
            frameBorderWidthDp = 5f,
            decorativeEmoji = "👑",
            cornerDecorEmoji = "✨",
            isPremium = true
        ),
        PhotoTemplate(
            id = "vintage_polaroid_nostalgia",
            name = "Retro Polaroid Film",
            category = TemplateCategory.PREMIUM,
            subCategory = "Vintage & Nostalgia",
            headerTitle = "📸 TIMELESS MEMORIES 📸",
            defaultGreeting = "Caught between nostalgic amber sunbeams and unforgettable golden days.",
            footerQuote = "#VintageFilm #RetroAesthetic #PolaroidMoments",
            gradientColors = listOf(0xFF1C1917, 0xFF292524, 0xFF44403C),
            frameShape = "rect",
            frameBorderColor = 0xFFFEF3C7,
            frameBorderWidthDp = 6f,
            decorativeEmoji = "📸",
            cornerDecorEmoji = "🎞️",
            isPremium = true
        ),
        PhotoTemplate(
            id = "cyberpunk_neon_edge",
            name = "Cyberpunk 2077 HUD",
            category = TemplateCategory.PREMIUM,
            subCategory = "Cyber & Tech",
            headerTitle = "⚡ SYSTEM OVERDRIVE ⚡",
            defaultGreeting = "Neon reflections in electric rain; living beyond digital boundaries.",
            footerQuote = "#CyberVibes #Synthwave #NeonPulse",
            gradientColors = listOf(0xFF030712, 0xFF1E1B4B, 0xFF0E7490),
            frameShape = "rounded",
            frameBorderColor = 0xFF22D3EE,
            frameBorderWidthDp = 4f,
            decorativeEmoji = "👾",
            cornerDecorEmoji = "⚡",
            isPremium = true
        ),
        PhotoTemplate(
            id = "vogue_haute_couture",
            name = "Paris Haute Couture",
            category = TemplateCategory.PREMIUM,
            subCategory = "Luxury Fashion",
            headerTitle = "🌟 VOGUE GLOBAL ISSUE 🌟",
            defaultGreeting = "High elegance, fearless couture, and iconic personal charisma.",
            footerQuote = "#VogueCover #HauteCouture #Supermodel",
            gradientColors = listOf(0xFF09090B, 0xFF18181B, 0xFF831843),
            frameShape = "rect",
            frameBorderColor = 0xFFFFFFFF,
            frameBorderWidthDp = 3f,
            decorativeEmoji = "💎",
            cornerDecorEmoji = "🌟",
            isPremium = true
        ),
        PhotoTemplate(
            id = "sunset_golden_glow",
            name = "Aesthetic Golden Hour",
            category = TemplateCategory.PREMIUM,
            subCategory = "Aesthetic Glow",
            headerTitle = "🌅 GOLDEN HOUR MAGIC 🌅",
            defaultGreeting = "Basked in honey sunlight, soft warm breezes, and pure summer bliss.",
            footerQuote = "#GoldenHour #WarmTones #SunKissed",
            gradientColors = listOf(0xFF451A03, 0xFF92400E, 0xFFF59E0B),
            frameShape = "circle",
            frameBorderColor = 0xFFFEF08A,
            frameBorderWidthDp = 4f,
            decorativeEmoji = "🌅",
            cornerDecorEmoji = "✨",
            isPremium = true
        ),
        PhotoTemplate(
            id = "bappa_royal_aarti",
            name = "Siddhivinayak Darshan",
            category = TemplateCategory.PREMIUM,
            subCategory = "Ganesh Special",
            headerTitle = "🌺 VIGHNAHARTA BLESSINGS 🌺",
            defaultGreeting = "May Ganpati Bappa shower divine wisdom, immense health, and royal prosperity!",
            footerQuote = "#Siddhivinayak #JaiGanesh #BappaMorya",
            gradientColors = listOf(0xFF450A0A, 0xFF7C2D12, 0xFFEA580C),
            frameShape = "rounded",
            frameBorderColor = 0xFFFDE047,
            frameBorderWidthDp = 5f,
            decorativeEmoji = "🌺",
            cornerDecorEmoji = "🕉️",
            isPremium = true
        ),
        PhotoTemplate(
            id = "bholenath_trinetra",
            name = "Mahadev Shiv Shambhu",
            category = TemplateCategory.PREMIUM,
            subCategory = "Shivratri & Devotion",
            headerTitle = "🔱 HAR HAR MAHADEV 🔱",
            defaultGreeting = "Om Namah Shivaya! In cosmic tranquility lies infinite spiritual power.",
            footerQuote = "#Bholenath #ShivShambhu #MahakalBlessings",
            gradientColors = listOf(0xFF020617, 0xFF0F172A, 0xFF1E3A8A),
            frameShape = "circle",
            frameBorderColor = 0xFF38BDF8,
            frameBorderWidthDp = 4f,
            decorativeEmoji = "🔱",
            cornerDecorEmoji = "🕉️",
            isPremium = true
        ),
        PhotoTemplate(
            id = "desi_bollywood_swag",
            name = "Blockbuster Hero Swag",
            category = TemplateCategory.PREMIUM,
            subCategory = "Bollywood Masala",
            headerTitle = "🔥 APNA TIME AAYEGA 🔥",
            defaultGreeting = "Kaha tha na humara jalwa hai, tha aur hamesha rahega!",
            footerQuote = "#DesiSwag #FullOnTashan #BlockbusterHero",
            gradientColors = listOf(0xFF3B0764, 0xFF831843, 0xFFDC2626),
            frameShape = "rect",
            frameBorderColor = 0xFFFACC15,
            frameBorderWidthDp = 5f,
            decorativeEmoji = "🔥",
            cornerDecorEmoji = "🎬",
            isPremium = true
        ),
        PhotoTemplate(
            id = "karwa_chauth_chand",
            name = "Karwa Chauth Moon",
            category = TemplateCategory.PREMIUM,
            subCategory = "Love & Tradition",
            headerTitle = "🌙 SHUBH KARWA CHAUTH 🌙",
            defaultGreeting = "Praying for your long life, endless happiness, and unconditional companionship.",
            footerQuote = "#KarwaChauthSpecial #MoonlightLove #Suhaag",
            gradientColors = listOf(0xFF4C0519, 0xFF881337, 0xFFBE123C),
            frameShape = "circle",
            frameBorderColor = 0xFFFDE047,
            frameBorderWidthDp = 4f,
            decorativeEmoji = "🌙",
            cornerDecorEmoji = "🌹",
            isPremium = true
        ),
        PhotoTemplate(
            id = "executive_leader_pro",
            name = "Forbes Executive Spotlight",
            category = TemplateCategory.PREMIUM,
            subCategory = "Business & Leader",
            headerTitle = "💼 30 UNDER 30 VISIONARY 💼",
            defaultGreeting = "Transforming the future with bold strategy, resilience, and vision.",
            footerQuote = "#LeaderSpotlight #ForbesAesthetic #GameChanger",
            gradientColors = listOf(0xFF030712, 0xFF111827, 0xFF1F2937),
            frameShape = "rect",
            frameBorderColor = 0xFF38BDF8,
            frameBorderWidthDp = 3f,
            decorativeEmoji = "🎯",
            cornerDecorEmoji = "⭐",
            isPremium = true
        )
    )

    val TRENDING_DESIGNS: List<PhotoTemplate> by lazy {
        listOf(
            getTemplateById("royal_gold_wedding") ?: PREMIUM_TEMPLATES[0],
            getTemplateById("magazine_vogue") ?: STYLISH_TEMPLATES[0],
            getTemplateById("diwali_royal") ?: FESTIVAL_TEMPLATES[0],
            getTemplateById("cyberpunk_neon_edge") ?: PREMIUM_TEMPLATES[2],
            getTemplateById("bappa_royal_aarti") ?: PREMIUM_TEMPLATES[5],
            getTemplateById("sunset_golden_glow") ?: PREMIUM_TEMPLATES[4]
        )
    }

    val NEW_TEMPLATES: List<PhotoTemplate> by lazy {
        listOf(
            getTemplateById("vintage_polaroid_nostalgia") ?: PREMIUM_TEMPLATES[1],
            getTemplateById("vogue_haute_couture") ?: PREMIUM_TEMPLATES[3],
            getTemplateById("bholenath_trinetra") ?: PREMIUM_TEMPLATES[6],
            getTemplateById("desi_bollywood_swag") ?: PREMIUM_TEMPLATES[7],
            getTemplateById("karwa_chauth_chand") ?: PREMIUM_TEMPLATES[8],
            getTemplateById("executive_leader_pro") ?: PREMIUM_TEMPLATES[9]
        )
    }

    fun getAllTemplates(): List<PhotoTemplate> = PREMIUM_TEMPLATES + FESTIVAL_TEMPLATES + STYLISH_TEMPLATES

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
