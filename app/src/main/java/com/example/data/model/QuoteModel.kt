package com.example.data.model

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color

enum class QuoteCategory(val displayName: String, val icon: String) {
    ALL("All", "🌟"),
    MOTIVATIONAL("Motivational", "🔥"),
    FACTS("Facts Quotes", "💡"),
    SUCCESS("Success & Hustle", "🏆"),
    WISDOM("Deep Wisdom", "🧠"),
    LIFE("Life Lessons", "✨"),
    HINDI_SUVICHAR("Hindi Suvichar", "📜")
}

enum class QuoteFrameStyle(val displayName: String, val description: String) {
    MINIMAL_BORDER("Minimalist Notch", "Clean fine border with notched corner brackets"),
    GOLD_LUXURY("Gold Luxury", "Royal golden double border with diamond accents"),
    NEON_CYBER("Cyber Neon Glow", "Futuristic cyberpunk lines with tech tags"),
    TYPEWRITER_VINTAGE("Typewriter Vintage", "Classic card with decorative bracket dividers"),
    CINEMATIC_LETTERBOX("Cinematic Frame", "Widescreen letterbox borders with cinema styling"),
    GLASS_CARD("Frosted Glass", "Translucent glass card with soft luminous border"),
    EDITORIAL_MAGAZINE("Editorial Magazine", "Modern asymmetric layout with bold side stripe"),
    POLAROID_PIN("Polaroid Pin", "Polaroid photo card with handwritten feel"),
    DOUBLE_RULE_CLASSIC("Classic Double Rule", "Timeless inner & outer border with ornamental marks"),
    ROUNDED_PILL("Pastel Modern Pill", "Smooth curved card with top pill category badge")
}

data class QuoteBackgroundPreset(
    val id: String,
    val name: String,
    val colors: List<Long>,
    val defaultTextColor: Long = 0xFFFFFFFF,
    val defaultBorderColor: Long = 0xFFE2E8F0
)

data class QuoteItem(
    val id: String,
    val category: QuoteCategory,
    val quote: String,
    val author: String,
    val badgeIcon: String = "✨",
    val suggestedFrame: QuoteFrameStyle = QuoteFrameStyle.MINIMAL_BORDER,
    val suggestedBgId: String = "midnight"
)

object QuoteRepository {

    val BACKGROUND_PRESETS: List<QuoteBackgroundPreset> = listOf(
        QuoteBackgroundPreset(
            id = "midnight",
            name = "Midnight Aurora",
            colors = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
            defaultTextColor = 0xFFFFFFFF,
            defaultBorderColor = 0xFF38BDF8
        ),
        QuoteBackgroundPreset(
            id = "sunset",
            name = "Golden Sunset",
            colors = listOf(0xFF1F0D24, 0xFF581C87, 0xFFE11D48),
            defaultTextColor = 0xFFFFF1F2,
            defaultBorderColor = 0xFFFDE047
        ),
        QuoteBackgroundPreset(
            id = "royal",
            name = "Royal Velvet",
            colors = listOf(0xFF0F172A, 0xFF1E1B4B, 0xFF312E81),
            defaultTextColor = 0xFFFFFFFF,
            defaultBorderColor = 0xFFA78BFA
        ),
        QuoteBackgroundPreset(
            id = "emerald",
            name = "Deep Emerald",
            colors = listOf(0xFF064E3B, 0xFF065F46, 0xFF047857),
            defaultTextColor = 0xFFECFDF5,
            defaultBorderColor = 0xFF6EE7B7
        ),
        QuoteBackgroundPreset(
            id = "cyber",
            name = "Neon Cyberpunk",
            colors = listOf(0xFF030712, 0xFF180B2B, 0xFF0A192F),
            defaultTextColor = 0xFF22D3EE,
            defaultBorderColor = 0xFFF43F5E
        ),
        QuoteBackgroundPreset(
            id = "obsidian",
            name = "Pure Obsidian",
            colors = listOf(0xFF000000, 0xFF111827, 0xFF1F2937),
            defaultTextColor = 0xFFF9FAFB,
            defaultBorderColor = 0xFFD4AF37 // Gold
        ),
        QuoteBackgroundPreset(
            id = "terracotta",
            name = "Warm Amber",
            colors = listOf(0xFF29150B, 0xFF451A03, 0xFF78350F),
            defaultTextColor = 0xFFFEF3C7,
            defaultBorderColor = 0xFFFBBF24
        ),
        QuoteBackgroundPreset(
            id = "crimson",
            name = "Dark Crimson",
            colors = listOf(0xFF2B090E, 0xFF4C0519, 0xFF881337),
            defaultTextColor = 0xFFFFF1F2,
            defaultBorderColor = 0xFFFDA4AF
        )
    )

    val QUOTES: List<QuoteItem> = listOf(
        // === MOTIVATIONAL QUOTES ===
        QuoteItem(
            id = "m1",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "The secret of getting ahead is getting started.",
            author = "Mark Twain",
            badgeIcon = "🔥",
            suggestedFrame = QuoteFrameStyle.MINIMAL_BORDER,
            suggestedBgId = "midnight"
        ),
        QuoteItem(
            id = "m2",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Do not wait to strike till the iron is hot; but make it hot by striking.",
            author = "William Butler Yeats",
            badgeIcon = "⚡",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "m3",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "It always seems impossible until it's done.",
            author = "Nelson Mandela",
            badgeIcon = "🏆",
            suggestedFrame = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            suggestedBgId = "sunset"
        ),
        QuoteItem(
            id = "m4",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Your time is limited, so don't waste it living someone else's life.",
            author = "Steve Jobs",
            badgeIcon = "💡",
            suggestedFrame = QuoteFrameStyle.GLASS_CARD,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "m5",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Hard work beats talent when talent fails to work hard.",
            author = "Tim Notke",
            badgeIcon = "💪",
            suggestedFrame = QuoteFrameStyle.NEON_CYBER,
            suggestedBgId = "cyber"
        ),
        QuoteItem(
            id = "m6",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Don't count the days, make the days count.",
            author = "Muhammad Ali",
            badgeIcon = "🥊",
            suggestedFrame = QuoteFrameStyle.CINEMATIC_LETTERBOX,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "m7",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Believe you can and you're halfway there.",
            author = "Theodore Roosevelt",
            badgeIcon = "🌟",
            suggestedFrame = QuoteFrameStyle.DOUBLE_RULE_CLASSIC,
            suggestedBgId = "terracotta"
        ),
        QuoteItem(
            id = "m8",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
            author = "Winston Churchill",
            badgeIcon = "🛡️",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "m9",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Stay hungry. Stay foolish. Never stop learning.",
            author = "Steve Jobs",
            badgeIcon = "🚀",
            suggestedFrame = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            suggestedBgId = "midnight"
        ),
        QuoteItem(
            id = "m10",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "A champion is defined not by their wins, but by how they can recover when they fall.",
            author = "Serena Williams",
            badgeIcon = "👑",
            suggestedFrame = QuoteFrameStyle.ROUNDED_PILL,
            suggestedBgId = "sunset"
        ),

        // === FACTS QUOTES ===
        QuoteItem(
            id = "f1",
            category = QuoteCategory.FACTS,
            quote = "Honey never spoils. Archaeologists have found 3,000-year-old honey in Egyptian tombs that is still perfectly edible.",
            author = "Food Science Fact",
            badgeIcon = "🍯",
            suggestedFrame = QuoteFrameStyle.TYPEWRITER_VINTAGE,
            suggestedBgId = "terracotta"
        ),
        QuoteItem(
            id = "f2",
            category = QuoteCategory.FACTS,
            quote = "Octopuses have three hearts, nine brains, and blue blood.",
            author = "Marine Biology Fact",
            badgeIcon = "🐙",
            suggestedFrame = QuoteFrameStyle.NEON_CYBER,
            suggestedBgId = "cyber"
        ),
        QuoteItem(
            id = "f3",
            category = QuoteCategory.FACTS,
            quote = "A day on Venus is longer than a whole year on Venus. It takes 243 Earth days to rotate once.",
            author = "Cosmic Astronomy Fact",
            badgeIcon = "🪐",
            suggestedFrame = QuoteFrameStyle.MINIMAL_BORDER,
            suggestedBgId = "midnight"
        ),
        QuoteItem(
            id = "f4",
            category = QuoteCategory.FACTS,
            quote = "Bananas are curved because they grow towards the sun against gravity, a process called negative geotropism.",
            author = "Nature & Botany Fact",
            badgeIcon = "🍌",
            suggestedFrame = QuoteFrameStyle.ROUNDED_PILL,
            suggestedBgId = "emerald"
        ),
        QuoteItem(
            id = "f5",
            category = QuoteCategory.FACTS,
            quote = "The human brain generates about 20 watts of electrical power—enough to illuminate a dim LED light bulb.",
            author = "Neuroscience Fact",
            badgeIcon = "🧠",
            suggestedFrame = QuoteFrameStyle.GLASS_CARD,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "f6",
            category = QuoteCategory.FACTS,
            quote = "Sharks existed before trees. Sharks have been swimming in oceans for over 400 million years.",
            author = "Evolutionary Fact",
            badgeIcon = "🦈",
            suggestedFrame = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            suggestedBgId = "midnight"
        ),
        QuoteItem(
            id = "f7",
            category = QuoteCategory.FACTS,
            quote = "Cleopatra lived closer in time to the first Moon landing than to the construction of the Great Pyramid of Giza.",
            author = "World History Fact",
            badgeIcon = "🏛️",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "f8",
            category = QuoteCategory.FACTS,
            quote = "Hot water can freeze faster than cold water under certain conditions. This is scientifically called the Mpemba effect.",
            author = "Physics Phenomenon",
            badgeIcon = "❄️",
            suggestedFrame = QuoteFrameStyle.CINEMATIC_LETTERBOX,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "f9",
            category = QuoteCategory.FACTS,
            quote = "There are more stars in the observable universe than grains of sand on all the Earth's beaches combined.",
            author = "Astrophysics Fact",
            badgeIcon = "🌌",
            suggestedFrame = QuoteFrameStyle.MINIMAL_BORDER,
            suggestedBgId = "midnight"
        ),
        QuoteItem(
            id = "f10",
            category = QuoteCategory.FACTS,
            quote = "Water can boil and freeze simultaneously at what is called the 'triple point' where temperature and pressure align.",
            author = "Chemistry Fact",
            badgeIcon = "⚗️",
            suggestedFrame = QuoteFrameStyle.DOUBLE_RULE_CLASSIC,
            suggestedBgId = "emerald"
        ),

        // === SUCCESS & HUSTLE ===
        QuoteItem(
            id = "s1",
            category = QuoteCategory.SUCCESS,
            quote = "Opportunities don't happen. You create them.",
            author = "Chris Grosser",
            badgeIcon = "💼",
            suggestedFrame = QuoteFrameStyle.MINIMAL_BORDER,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "s2",
            category = QuoteCategory.SUCCESS,
            quote = "I find that the harder I work, the more luck I seem to have.",
            author = "Thomas Jefferson",
            badgeIcon = "🍀",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "emerald"
        ),
        QuoteItem(
            id = "s3",
            category = QuoteCategory.SUCCESS,
            quote = "Action is the foundational key to all success.",
            author = "Pablo Picasso",
            badgeIcon = "🔑",
            suggestedFrame = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            suggestedBgId = "sunset"
        ),
        QuoteItem(
            id = "s4",
            category = QuoteCategory.SUCCESS,
            quote = "Success is walking from failure to failure with no loss of enthusiasm.",
            author = "Winston Churchill",
            badgeIcon = "🚶",
            suggestedFrame = QuoteFrameStyle.DOUBLE_RULE_CLASSIC,
            suggestedBgId = "royal"
        ),

        // === WISDOM & PHILOSOPHY ===
        QuoteItem(
            id = "w1",
            category = QuoteCategory.WISDOM,
            quote = "We suffer more often in imagination than in reality.",
            author = "Seneca",
            badgeIcon = "🏛️",
            suggestedFrame = QuoteFrameStyle.TYPEWRITER_VINTAGE,
            suggestedBgId = "terracotta"
        ),
        QuoteItem(
            id = "w2",
            category = QuoteCategory.WISDOM,
            quote = "He who has a why to live can bear almost any how.",
            author = "Friedrich Nietzsche",
            badgeIcon = "🧭",
            suggestedFrame = QuoteFrameStyle.CINEMATIC_LETTERBOX,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "w3",
            category = QuoteCategory.WISDOM,
            quote = "Mastering others is strength. Mastering yourself is true power.",
            author = "Lao Tzu",
            badgeIcon = "☯️",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "w4",
            category = QuoteCategory.WISDOM,
            quote = "In the middle of every difficulty lies opportunity.",
            author = "Albert Einstein",
            badgeIcon = "✨",
            suggestedFrame = QuoteFrameStyle.GLASS_CARD,
            suggestedBgId = "midnight"
        ),

        // === LIFE LESSONS ===
        QuoteItem(
            id = "l1",
            category = QuoteCategory.LIFE,
            quote = "Life is 10% what happens to you and 90% how you react to it.",
            author = "Charles R. Swindoll",
            badgeIcon = "🌱",
            suggestedFrame = QuoteFrameStyle.ROUNDED_PILL,
            suggestedBgId = "emerald"
        ),
        QuoteItem(
            id = "l2",
            category = QuoteCategory.LIFE,
            quote = "Happiness is not something ready made. It comes from your own actions.",
            author = "Dalai Lama",
            badgeIcon = "☀️",
            suggestedFrame = QuoteFrameStyle.POLAROID_PIN,
            suggestedBgId = "sunset"
        ),
        QuoteItem(
            id = "l3",
            category = QuoteCategory.LIFE,
            quote = "In the end, it's not the years in your life that count. It's the life in your years.",
            author = "Abraham Lincoln",
            badgeIcon = "⏳",
            suggestedFrame = QuoteFrameStyle.TYPEWRITER_VINTAGE,
            suggestedBgId = "terracotta"
        ),

        // === HINDI SUVICHAR ===
        QuoteItem(
            id = "h1",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "मंजिल उन्हीं को मिलती है, जिनके सपनों में जान होती है!\nपंखों से कुछ नहीं होता, हौसलों से उड़ान होती है।",
            author = "प्रेरक विचार",
            badgeIcon = "🦅",
            suggestedFrame = QuoteFrameStyle.GOLD_LUXURY,
            suggestedBgId = "obsidian"
        ),
        QuoteItem(
            id = "h2",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "समय और समझ दोनों एक साथ सिर्फ खुशनसीब लोगों को ही मिलते हैं।",
            author = "जीवन सत्य",
            badgeIcon = "⏳",
            suggestedFrame = QuoteFrameStyle.DOUBLE_RULE_CLASSIC,
            suggestedBgId = "royal"
        ),
        QuoteItem(
            id = "h3",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "मेहनत इतनी खामोशी से करो कि तुम्हारी सफलता शोर मचा दे।",
            author = "सफलता मंत्र",
            badgeIcon = "🎯",
            suggestedFrame = QuoteFrameStyle.NEON_CYBER,
            suggestedBgId = "cyber"
        ),
        QuoteItem(
            id = "h4",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "जो अपने कदमों की काबिलियत पर विश्वास रखते हैं, वही अक्सर मंजिल पर पहुँचते हैं।",
            author = "डॉ. एपीजे अब्दुल कलाम",
            badgeIcon = "🇮🇳",
            suggestedFrame = QuoteFrameStyle.MINIMAL_BORDER,
            suggestedBgId = "midnight"
        )
    )

    fun getAllQuotes(): List<QuoteItem> = QUOTES

    fun getQuotesByCategory(cat: QuoteCategory): List<QuoteItem> {
        if (cat == QuoteCategory.ALL) return QUOTES
        return QUOTES.filter { it.category == cat }
    }

    fun getQuoteById(id: String): QuoteItem? {
        return QUOTES.firstOrNull { it.id == id }
    }

    fun getRandomQuote(): QuoteItem {
        return QUOTES.random()
    }

    val TEMPLATES: List<QuoteTemplate> = listOf(
        QuoteTemplate(
            id = "tpl_golden_hustle",
            name = "Golden Hustle",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "The secret of getting ahead is getting started.",
            author = "Mark Twain",
            frameStyle = QuoteFrameStyle.GOLD_LUXURY,
            backgroundPresetId = "obsidian",
            fontId = "Rozha",
            fontSizeSp = 26f,
            textColor = 0xFFFFFBEB,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFF59E0B,
            borderWidthDp = 2.8f,
            badgeIcon = "🔥"
        ),
        QuoteTemplate(
            id = "tpl_cyber_fact",
            name = "Cyber Matrix Fact",
            category = QuoteCategory.FACTS,
            quote = "Octopuses have three hearts, nine brains, and blue blood.",
            author = "Marine Biology Fact",
            frameStyle = QuoteFrameStyle.NEON_CYBER,
            backgroundPresetId = "cyber",
            fontId = "Anton",
            fontSizeSp = 24f,
            textColor = 0xFF22D3EE,
            authorColor = 0xFF67E8F9,
            borderColor = 0xFFF43F5E,
            borderWidthDp = 2.5f,
            badgeIcon = "🐙"
        ),
        QuoteTemplate(
            id = "tpl_honey_fact",
            name = "Parchment History Fact",
            category = QuoteCategory.FACTS,
            quote = "Honey never spoils. Archaeologists have found 3,000-year-old honey in Egyptian tombs that is still perfectly edible.",
            author = "Archaeological Science",
            frameStyle = QuoteFrameStyle.TYPEWRITER_VINTAGE,
            backgroundPresetId = "terracotta",
            fontId = "Rozha",
            fontSizeSp = 22f,
            textColor = 0xFFFEF3C7,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFFBBF24,
            borderWidthDp = 2.5f,
            badgeIcon = "🍯"
        ),
        QuoteTemplate(
            id = "tpl_steve_jobs",
            name = "Editorial Power Mindset",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "Your time is limited, so don't waste it living someone else's life.",
            author = "Steve Jobs",
            frameStyle = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            backgroundPresetId = "sunset",
            fontId = "Anton",
            fontSizeSp = 25f,
            textColor = 0xFFFFFFFF,
            authorColor = 0xFFFED7AA,
            borderColor = 0xFFFDE047,
            borderWidthDp = 2.5f,
            badgeIcon = "🚀"
        ),
        QuoteTemplate(
            id = "tpl_stoic_seneca",
            name = "Stoic Philosophy",
            category = QuoteCategory.WISDOM,
            quote = "We suffer more often in imagination than in reality.",
            author = "Seneca",
            frameStyle = QuoteFrameStyle.TYPEWRITER_VINTAGE,
            backgroundPresetId = "obsidian",
            fontId = "Rozha",
            fontSizeSp = 24f,
            textColor = 0xFFE2E8F0,
            authorColor = 0xFFCBD5E1,
            borderColor = 0xFF94A3B8,
            borderWidthDp = 2.0f,
            badgeIcon = "🏛️"
        ),
        QuoteTemplate(
            id = "tpl_space_cosmos",
            name = "Deep Space Fact",
            category = QuoteCategory.FACTS,
            quote = "There are more stars in the observable universe than grains of sand on all the Earth's beaches combined.",
            author = "Cosmic Astronomy Fact",
            frameStyle = QuoteFrameStyle.MINIMAL_BORDER,
            backgroundPresetId = "midnight",
            fontId = "Poppins",
            fontSizeSp = 22f,
            textColor = 0xFFFFFFFF,
            authorColor = 0xFFBAE6FD,
            borderColor = 0xFF38BDF8,
            borderWidthDp = 2.2f,
            badgeIcon = "🌌"
        ),
        QuoteTemplate(
            id = "tpl_impossible",
            name = "Mandela Victory",
            category = QuoteCategory.MOTIVATIONAL,
            quote = "It always seems impossible until it's done.",
            author = "Nelson Mandela",
            frameStyle = QuoteFrameStyle.GOLD_LUXURY,
            backgroundPresetId = "royal",
            fontId = "Rozha",
            fontSizeSp = 26f,
            textColor = 0xFFFFFFFF,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFFBBF24,
            borderWidthDp = 3.0f,
            badgeIcon = "🏆"
        ),
        QuoteTemplate(
            id = "tpl_brain_power",
            name = "Neuro Brain Energy",
            category = QuoteCategory.FACTS,
            quote = "The human brain generates about 20 watts of electrical power—enough to illuminate a dim LED light bulb.",
            author = "Neuroscience Fact",
            frameStyle = QuoteFrameStyle.GLASS_CARD,
            backgroundPresetId = "royal",
            fontId = "Poppins",
            fontSizeSp = 22f,
            textColor = 0xFFE0E7FF,
            authorColor = 0xFFC7D2FE,
            borderColor = 0xFFA78BFA,
            borderWidthDp = 2.5f,
            badgeIcon = "🧠"
        ),
        QuoteTemplate(
            id = "tpl_cinema_ali",
            name = "Champion Cinema",
            category = QuoteCategory.SUCCESS,
            quote = "Don't count the days, make the days count.",
            author = "Muhammad Ali",
            frameStyle = QuoteFrameStyle.CINEMATIC_LETTERBOX,
            backgroundPresetId = "obsidian",
            fontId = "Anton",
            fontSizeSp = 26f,
            textColor = 0xFFF9FAFB,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFD4AF37,
            borderWidthDp = 2.5f,
            badgeIcon = "🥊"
        ),
        QuoteTemplate(
            id = "tpl_nietzsche",
            name = "Why To Live",
            category = QuoteCategory.WISDOM,
            quote = "He who has a why to live can bear almost any how.",
            author = "Friedrich Nietzsche",
            frameStyle = QuoteFrameStyle.EDITORIAL_MAGAZINE,
            backgroundPresetId = "terracotta",
            fontId = "Rozha",
            fontSizeSp = 24f,
            textColor = 0xFFFEF3C7,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFF59E0B,
            borderWidthDp = 2.5f,
            badgeIcon = "🧭"
        ),
        QuoteTemplate(
            id = "tpl_dalai_lama",
            name = "Polaroid Serenity",
            category = QuoteCategory.LIFE,
            quote = "Happiness is not something ready made. It comes from your own actions.",
            author = "Dalai Lama",
            frameStyle = QuoteFrameStyle.POLAROID_PIN,
            backgroundPresetId = "sunset",
            fontId = "Pacifico",
            fontSizeSp = 23f,
            textColor = 0xFFFFF1F2,
            authorColor = 0xFFFCE7F3,
            borderColor = 0xFFF472B6,
            borderWidthDp = 2.5f,
            badgeIcon = "☀️"
        ),
        QuoteTemplate(
            id = "tpl_serena",
            name = "Resilience Pill",
            category = QuoteCategory.SUCCESS,
            quote = "A champion is defined not by their wins, but by how they can recover when they fall.",
            author = "Serena Williams",
            frameStyle = QuoteFrameStyle.ROUNDED_PILL,
            backgroundPresetId = "emerald",
            fontId = "Poppins",
            fontSizeSp = 23f,
            textColor = 0xFFECFDF5,
            authorColor = 0xFFA7F3D0,
            borderColor = 0xFF34D399,
            borderWidthDp = 2.5f,
            badgeIcon = "👑"
        ),
        QuoteTemplate(
            id = "tpl_kalam_suvichar",
            name = "Kalam Golden Prerna",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "जो अपने कदमों की काबिलियत पर विश्वास रखते हैं, वही अक्सर मंजिल पर पहुँचते हैं।",
            author = "डॉ. एपीजे अब्दुल कलाम",
            frameStyle = QuoteFrameStyle.GOLD_LUXURY,
            backgroundPresetId = "obsidian",
            fontId = "Kalam",
            fontSizeSp = 25f,
            textColor = 0xFFFEF3C7,
            authorColor = 0xFFFDE68A,
            borderColor = 0xFFFBBF24,
            borderWidthDp = 3.0f,
            badgeIcon = "🇮🇳"
        ),
        QuoteTemplate(
            id = "tpl_hindi_sapne",
            name = "Hoslon Ki Udaan",
            category = QuoteCategory.HINDI_SUVICHAR,
            quote = "मंजिल उन्हीं को मिलती है, जिनके सपनों में जान होती है!\nपंखों से कुछ नहीं होता, हौसलों से उड़ान होती है।",
            author = "प्रेरक विचार",
            frameStyle = QuoteFrameStyle.DOUBLE_RULE_CLASSIC,
            backgroundPresetId = "royal",
            fontId = "Kalam",
            fontSizeSp = 23f,
            textColor = 0xFFFFFFFF,
            authorColor = 0xFFDDD6FE,
            borderColor = 0xFFA78BFA,
            borderWidthDp = 2.5f,
            badgeIcon = "🦅"
        ),
        QuoteTemplate(
            id = "tpl_water_freeze",
            name = "Physics Mpemba Effect",
            category = QuoteCategory.FACTS,
            quote = "Hot water can freeze faster than cold water under certain conditions. This is scientifically called the Mpemba effect.",
            author = "Physics Phenomenon",
            frameStyle = QuoteFrameStyle.MINIMAL_BORDER,
            backgroundPresetId = "midnight",
            fontId = "Poppins",
            fontSizeSp = 22f,
            textColor = 0xFFE0F2FE,
            authorColor = 0xFFBAE6FD,
            borderColor = 0xFF38BDF8,
            borderWidthDp = 2.2f,
            badgeIcon = "❄️"
        )
    )

    fun getAllTemplates(): List<QuoteTemplate> = TEMPLATES

    fun getTemplatesByCategory(cat: QuoteCategory): List<QuoteTemplate> {
        if (cat == QuoteCategory.ALL) return TEMPLATES
        return TEMPLATES.filter { it.category == cat }
    }

    fun getTemplateById(id: String): QuoteTemplate? {
        return TEMPLATES.firstOrNull { it.id == id }
    }
}

data class QuoteTemplate(
    val id: String,
    val name: String,
    val category: QuoteCategory,
    val quote: String,
    val author: String,
    val frameStyle: QuoteFrameStyle,
    val backgroundPresetId: String,
    val fontId: String = "Rozha",
    val fontSizeSp: Float = 24f,
    val textColor: Long = 0xFFFFFFFF,
    val authorColor: Long = 0xFFE2E8F0,
    val borderColor: Long = 0xFFD4AF37,
    val borderWidthDp: Float = 2.5f,
    val cornerRadiusDp: Float = 16f,
    val frameInsetDp: Float = 16f,
    val textAlign: String = "center",
    val showQuoteMarks: Boolean = true,
    val showBadgeIcon: Boolean = true,
    val badgeIcon: String = "✨",
    val hasShadow: Boolean = true,
    val defaultAspectRatio: String = "1:1"
)

