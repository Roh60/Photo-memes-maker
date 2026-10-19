package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

data class StickerItem(
    val emoji: String,
    val label: String,
    val category: String
)

data class DrawableStickerItem(
    val id: String,
    @DrawableRes val resId: Int,
    val name: String,
    val category: String,
    val description: String = ""
)

data class PlacedSticker(
    val id: String = java.util.UUID.randomUUID().toString(),
    val emoji: String = "",
    @DrawableRes val drawableResId: Int? = null,
    val label: String = "Sticker",
    var xPercent: Float = 0.5f,
    var yPercent: Float = 0.5f,
    var scale: Float = 1.0f,
    var rotation: Float = 0f,
    var sizeSp: Float = 44f,
    var sizeDp: Float = 88f,
    var isVisible: Boolean = true,
    var isLocked: Boolean = false
)

object StickerLibrary {

    val DRAWABLE_STICKERS = listOf(
        // Trending
        DrawableStickerItem("thug_glasses", R.drawable.ic_sticker_thug_glasses, "Thug Life Glasses", "Trending", "Deal with it 8-bit glasses"),
        DrawableStickerItem("laser_eyes", R.drawable.ic_sticker_laser_eyes, "Laser Eyes", "Trending", "Glowing red flare beams"),
        DrawableStickerItem("fire_flame", R.drawable.ic_sticker_fire_flame, "Lit Flame", "Trending", "Hot fiery flame"),
        DrawableStickerItem("crown_gold", R.drawable.ic_sticker_crown_gold, "Royal Crown", "Trending", "Gold jewel crown"),
        DrawableStickerItem("crying_laughing", R.drawable.ic_sticker_crying_laughing, "Laugh Tears", "Trending", "Iconic laughing emoji badge"),
        DrawableStickerItem("illuminati_eye", R.drawable.ic_sticker_illuminati_eye, "Illuminati Eye", "Trending", "Meme pyramid eye"),
        DrawableStickerItem("verified_badge", R.drawable.ic_sticker_verified_badge, "Blue Verified", "Trending", "Official Blue Tick Badge"),
        DrawableStickerItem("chad_jaw", R.drawable.ic_sticker_chad_jaw, "Sigma Gigachad", "Trending", "Sharp chiseled jawline badge"),

        // Glasses & Props
        DrawableStickerItem("cool_aviators", R.drawable.ic_sticker_cool_aviators, "Gold Aviators", "Glasses & Props", "Gold sunglasses"),
        DrawableStickerItem("deal_with_it", R.drawable.ic_sticker_deal_with_it, "Cyber Shades", "Glasses & Props", "Neon pixel futuristic glasses"),
        DrawableStickerItem("mustache", R.drawable.ic_sticker_mustache, "Mustache", "Glasses & Props", "Classic handlebar mustache"),
        DrawableStickerItem("party_hat", R.drawable.ic_sticker_party_hat, "Party Hat", "Glasses & Props", "Festive cone hat"),

        // Badges & Stamps
        DrawableStickerItem("dank_stamp", R.drawable.ic_sticker_dank_stamp, "Certified Dank", "Badges & Stamps", "Approval seal"),
        DrawableStickerItem("green_approve", R.drawable.ic_sticker_green_approve, "Approve Check", "Badges & Stamps", "Drake Yes badge"),
        DrawableStickerItem("red_reject", R.drawable.ic_sticker_red_reject, "Reject X", "Badges & Stamps", "Drake No badge"),
        DrawableStickerItem("stonks_arrow", R.drawable.ic_sticker_stonks_arrow, "Stonks Up", "Badges & Stamps", "Diagonal green bull arrow"),
        DrawableStickerItem("not_stonks", R.drawable.ic_sticker_not_stonks, "Not Stonks", "Badges & Stamps", "Diagonal red crash arrow"),

        // Reactions & Drama
        DrawableStickerItem("popcorn", R.drawable.ic_sticker_popcorn, "Drama Popcorn", "Reactions & Drama", "Popcorn tub for e-lafda / drama"),
        DrawableStickerItem("money_bag", R.drawable.ic_sticker_money_bag, "Paisa Hi Paisa", "Reactions & Drama", "Cash sack with coins"),
        DrawableStickerItem("skull_dead", R.drawable.ic_sticker_skull_dead, "Dead / I'm Dead", "Reactions & Drama", "Laughing dead skull"),
        DrawableStickerItem("warning_alert", R.drawable.ic_sticker_warning_alert, "Sarcasm Alert", "Reactions & Drama", "Hazard warning triangle"),
        DrawableStickerItem("heart_eyes", R.drawable.ic_sticker_heart_eyes, "Simp Alert", "Reactions & Drama", "Heart eyes love badge"),
        DrawableStickerItem("mic_drop", R.drawable.ic_sticker_mic_drop, "Mic Drop", "Reactions & Drama", "Savage punchline mic drop"),

        // Bubbles & Bursts
        DrawableStickerItem("speech_bubble", R.drawable.ic_sticker_speech_bubble, "Speech Bubble", "Bubbles & Bursts", "Dialogue bubble"),
        DrawableStickerItem("thought_bubble", R.drawable.ic_sticker_thought_bubble, "Thought Cloud", "Bubbles & Bursts", "Pondering cloud"),
        DrawableStickerItem("boom_burst", R.drawable.ic_sticker_boom_burst, "Boom Burst", "Bubbles & Bursts", "Comic action starburst")
    )

    val STICKERS = listOf(
        // Memes & Viral
        StickerItem("😂", "Laughing Tears", "Meme"),
        StickerItem("💀", "Dead / I'm Dead", "Meme"),
        StickerItem("🤡", "Clown", "Meme"),
        StickerItem("👀", "Side Eye", "Meme"),
        StickerItem("🤯", "Mind Blown", "Meme"),
        StickerItem("🤦‍♂️", "Facepalm", "Meme"),
        StickerItem("🗿", "Moai Chad", "Meme"),
        StickerItem("📈", "Stonks Up", "Meme"),
        StickerItem("📉", "Not Stonks", "Meme"),
        StickerItem("🚨", "Emergency Alert", "Meme"),
        StickerItem("🍿", "Eating Popcorn", "Meme"),
        StickerItem("💸", "Flying Cash", "Meme"),
        StickerItem("🤫", "Mewing / Shh", "Meme"),
        StickerItem("🥶", "Cold / Savage", "Meme"),
        StickerItem("🫡", "Respect Saluting", "Meme"),
        StickerItem("🧢", "Cap / Fake", "Meme"),
        StickerItem("🫠", "Melting", "Meme"),
        StickerItem("🤌", "Chef's Kiss", "Meme"),

        // Desi & Bollywood
        StickerItem("🪔", "Diya Light", "Desi & Festive"),
        StickerItem("🇮🇳", "Tiranga Flag", "Desi & Festive"),
        StickerItem("🏏", "Cricket Match", "Desi & Festive"),
        StickerItem("🪘", "Dhol Beats", "Desi & Festive"),
        StickerItem("🌺", "Marigold Garland", "Desi & Festive"),
        StickerItem("👳", "Babu Bhaiya", "Desi & Festive"),
        StickerItem("🕉️", "Om Blessings", "Desi & Festive"),
        StickerItem("🧿", "Nazar Evil Eye", "Desi & Festive"),
        StickerItem("🫖", "Chai Cutting", "Desi & Festive"),
        StickerItem("🎭", "Drama Nautanki", "Desi & Festive"),

        // Attitude & Swag
        StickerItem("🔥", "Lit Fire", "Attitude"),
        StickerItem("👑", "King Crown", "Attitude"),
        StickerItem("🕶️", "Black Shades", "Attitude"),
        StickerItem("⚡", "Electric Flash", "Attitude"),
        StickerItem("💎", "Rare Diamond", "Attitude"),
        StickerItem("🤙", "Vibe Call Me", "Attitude"),
        StickerItem("💯", "100 Percent", "Attitude"),
        StickerItem("🚀", "Rocket Moon", "Attitude"),
        StickerItem("💣", "Bomb Blast", "Attitude"),
        StickerItem("🏆", "Champion Trophy", "Attitude"),
        StickerItem("🦁", "Sher Lion", "Attitude"),
        StickerItem("🎯", "Bullseye Target", "Attitude"),

        // Love & Romance
        StickerItem("❤️", "Red Heart", "Love"),
        StickerItem("💖", "Sparkle Heart", "Love"),
        StickerItem("🌹", "Red Rose", "Love"),
        StickerItem("💍", "Diamond Ring", "Love"),
        StickerItem("🥰", "Blushing Smile", "Love"),
        StickerItem("😘", "Flying Kiss", "Love"),
        StickerItem("💐", "Bouquet Flowers", "Love"),
        StickerItem("💌", "Love Letter", "Love"),
        StickerItem("🧸", "Teddy Bear", "Love"),
        StickerItem("🫶", "Heart Hands", "Love"),

        // Festival & Celebrations
        StickerItem("✨", "Divine Sparkle", "Festivals"),
        StickerItem("🎆", "Fireworks Night", "Festivals"),
        StickerItem("🎉", "Party Popper", "Festivals"),
        StickerItem("🎨", "Holi Gulal", "Festivals"),
        StickerItem("🎄", "Xmas Tree", "Festivals"),
        StickerItem("🌙", "Eid Crescent", "Festivals"),
        StickerItem("🎂", "Birthday Cake", "Festivals"),
        StickerItem("🎈", "Helium Balloon", "Festivals"),
        StickerItem("🥳", "Party Blower", "Festivals"),
        StickerItem("🥂", "Cheers Glasses", "Festivals")
    )

    fun getDrawableStickersByCategory(category: String): List<DrawableStickerItem> {
        if (category == "All") return DRAWABLE_STICKERS
        return DRAWABLE_STICKERS.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getStickersByCategory(category: String): List<StickerItem> {
        if (category == "All") return STICKERS
        return STICKERS.filter { it.category.equals(category, ignoreCase = true) }
    }
}
