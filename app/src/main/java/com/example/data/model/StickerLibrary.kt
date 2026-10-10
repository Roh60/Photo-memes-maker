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

        // Glasses & Props
        DrawableStickerItem("cool_aviators", R.drawable.ic_sticker_cool_aviators, "Gold Aviators", "Glasses & Props", "Gold sunglasses"),
        DrawableStickerItem("mustache", R.drawable.ic_sticker_mustache, "Mustache", "Glasses & Props", "Classic handlebar mustache"),
        DrawableStickerItem("party_hat", R.drawable.ic_sticker_party_hat, "Party Hat", "Glasses & Props", "Festive cone hat"),

        // Badges & Stamps
        DrawableStickerItem("dank_stamp", R.drawable.ic_sticker_dank_stamp, "Certified Dank", "Badges & Stamps", "Approval seal"),
        DrawableStickerItem("green_approve", R.drawable.ic_sticker_green_approve, "Approve Check", "Badges & Stamps", "Drake Yes badge"),
        DrawableStickerItem("red_reject", R.drawable.ic_sticker_red_reject, "Reject X", "Badges & Stamps", "Drake No badge"),
        DrawableStickerItem("stonks_arrow", R.drawable.ic_sticker_stonks_arrow, "Stonks Up", "Badges & Stamps", "Diagonal green bull arrow"),

        // Bubbles & Bursts
        DrawableStickerItem("speech_bubble", R.drawable.ic_sticker_speech_bubble, "Speech Bubble", "Bubbles & Bursts", "Dialogue bubble"),
        DrawableStickerItem("thought_bubble", R.drawable.ic_sticker_thought_bubble, "Thought Cloud", "Bubbles & Bursts", "Pondering cloud"),
        DrawableStickerItem("boom_burst", R.drawable.ic_sticker_boom_burst, "Boom Burst", "Bubbles & Bursts", "Comic action starburst")
    )

    val STICKERS = listOf(
        // Festival & Celebrations
        StickerItem("🪔", "Diya", "Festivals"),
        StickerItem("✨", "Sparkle", "Festivals"),
        StickerItem("🎆", "Fireworks", "Festivals"),
        StickerItem("🎉", "Party Popper", "Festivals"),
        StickerItem("🎨", "Holi Color", "Festivals"),
        StickerItem("🎄", "Tree", "Festivals"),
        StickerItem("🌙", "Crescent", "Festivals"),
        StickerItem("🎂", "Cake", "Festivals"),
        StickerItem("🎈", "Balloon", "Festivals"),
        StickerItem("🌺", "Flower", "Festivals"),
        StickerItem("🇮🇳", "Flag", "Festivals"),
        StickerItem("🪘", "Dhol", "Festivals"),

        // Love & Romance
        StickerItem("❤️", "Red Heart", "Love"),
        StickerItem("💖", "Sparkle Heart", "Love"),
        StickerItem("🌹", "Rose", "Love"),
        StickerItem("💍", "Ring", "Love"),
        StickerItem("🥰", "Love Face", "Love"),
        StickerItem("😘", "Kiss", "Love"),
        StickerItem("💐", "Bouquet", "Love"),

        // Attitude & Cool
        StickerItem("🔥", "Fire", "Attitude"),
        StickerItem("👑", "Crown", "Attitude"),
        StickerItem("🕶️", "Sunglasses", "Attitude"),
        StickerItem("⚡", "Lightning", "Attitude"),
        StickerItem("💎", "Diamond", "Attitude"),
        StickerItem("🤙", "Call Me", "Attitude"),
        StickerItem("💯", "100", "Attitude"),
        StickerItem("🚀", "Rocket", "Attitude"),

        // Memes & Reactions
        StickerItem("😂", "Laughing Tears", "Meme"),
        StickerItem("💀", "Dead / I'm Dead", "Meme"),
        StickerItem("🤡", "Clown", "Meme"),
        StickerItem("👀", "Eyes", "Meme"),
        StickerItem("🤯", "Mind Blown", "Meme"),
        StickerItem("🤦‍♂️", "Facepalm", "Meme"),
        StickerItem("🗿", "Moai Chad", "Meme"),
        StickerItem("📈", "Stonks Up", "Meme"),
        StickerItem("📉", "Not Stonks", "Meme"),
        StickerItem("🚨", "Alert", "Meme")
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
