package com.example.data.model

object CaptionLibrary {

    data class CaptionIdea(
        val category: String, // "Funny", "Relatable", "Friendship", "Student Life", "Relationship", "Office"
        val language: String = "English", // "English", "Hindi", "Hinglish"
        val top: String,
        val bottom: String = ""
    )

    val CATEGORIES = listOf(
        "All",
        "Funny",
        "Relatable",
        "Friendship",
        "Student Life",
        "Relationship",
        "Office"
    )

    val LANGUAGES = listOf(
        "All",
        "English",
        "Hindi (हिन्दी)",
        "Hinglish"
    )

    val CAPTION_IDEAS = listOf(
        // === FUNNY ===
        CaptionIdea("Funny", "English", "I followed my heart...", "and it led me straight to the refrigerator."),
        CaptionIdea("Funny", "English", "Common sense is like deodorant.", "The people who need it most never use it."),
        CaptionIdea("Funny", "English", "I'm not lazy.", "I'm just on energy saving mode."),
        CaptionIdea("Funny", "English", "My bank balance after the weekend:", "0.00 (Emotional damage included)"),
        CaptionIdea("Funny", "English", "Life is short.", "Smile while you still have teeth!"),
        CaptionIdea("Funny", "Hinglish", "Diet shuru ki thi subah 9 baje,", "Aur 11 baje samose ne apna bana liya!"),
        CaptionIdea("Funny", "Hinglish", "Phone ki battery 1% pe chal rahi hai,", "Aur meri zindagi bhi lagbhag wahi hai!"),
        CaptionIdea("Funny", "Hinglish", "Duniya chaand par pahunch gayi,", "Aur main abhi bhi remote pe thappad maar raha hoon!"),
        CaptionIdea("Funny", "Hindi", "ज़िंदगी में बस इतना अमीर होना है,", "कि MRP देखे बिना सामान खरीद सकूँ!"),
        CaptionIdea("Funny", "Hindi", "मेरी चाय और मेरी नींद,", "दोनों के बीच में कोई नहीं आ सकता!"),

        // === RELATABLE ===
        CaptionIdea("Relatable", "English", "Me calculating how much sleep I'll get", "if I fall asleep in precisely 3 minutes"),
        CaptionIdea("Relatable", "English", "When you reply in your head", "and realize 3 days later you never hit send"),
        CaptionIdea("Relatable", "English", "Entering a room and completely forgetting", "what you came in here for"),
        CaptionIdea("Relatable", "English", "Looking at my phone screen in pitch dark:", "Blinded by the light ✨"),
        CaptionIdea("Relatable", "Hinglish", "Room mein aate hi bhool gaya,", "Aakhir yahan lene kya aaya tha?"),
        CaptionIdea("Relatable", "Hinglish", "Raat ko 2 baje random YouTube documentaries dekhna:", "Ab banunga expert!"),
        CaptionIdea("Relatable", "Hinglish", "Online delivery track karte waqt:", "Delivery guy mere ghar se 2 gali door hai!"),
        CaptionIdea("Relatable", "Hindi", "सुबह अलार्म बजने के बाद,", "बस 5 मिनट और सोने का लालच!"),
        CaptionIdea("Relatable", "Hindi", "जब घर वाले कहें 'फोन रख दो सब ठीक हो जाएगा':", "हर बीमारी का एक ही इलाज!"),

        // === FRIENDSHIP ===
        CaptionIdea("Friendship", "English", "Best friends don't let you do stupid things...", "alone! 😂"),
        CaptionIdea("Friendship", "English", "We'll be best friends forever,", "because you already know too many secrets."),
        CaptionIdea("Friendship", "English", "Finding friends with the same mental disorder:", "Priceless! ❤️"),
        CaptionIdea("Friendship", "Hinglish", "Dost aisa hona chahiye,", "Jo bole 'Bhai tu chal, dekh lenge!'"),
        CaptionIdea("Friendship", "Hinglish", "Group photo mein sabse pehle,", "Sirf apni shakal check karna!"),
        CaptionIdea("Friendship", "Hinglish", "Bhai treat kab de raha hai?", "Har dosti ka sabse purana sawaal!"),
        CaptionIdea("Friendship", "Hindi", "सच्चे दोस्त वही होते हैं,", "जो बेइज़्ज़ती भी प्यार से करते हैं!"),
        CaptionIdea("Friendship", "Hindi", "दोस्ती की कोई एक्सपायरी डेट नहीं होती,", "जब मिलो वही पुराना पागलपन!"),

        // === STUDENT LIFE ===
        CaptionIdea("Student Life", "English", "Starting a 500-page syllabus", "at 11:45 PM before tomorrow's 9 AM exam"),
        CaptionIdea("Student Life", "English", "Teacher: 'The exam will be easy and conceptual'", "The Exam: 💀💀💀"),
        CaptionIdea("Student Life", "English", "Group study session breakdown:", "10% studying, 90% gossiping & food orders"),
        CaptionIdea("Student Life", "English", "Opening the question paper and wondering:", "Who wrote this in which language?"),
        CaptionIdea("Student Life", "Hinglish", "Kal subah se pakka padhunga,", "The biggest lie students tell themselves!"),
        CaptionIdea("Student Life", "Hinglish", "Assignment submission at 11:59 PM:", "Adrenaline rush faster than F1 cars!"),
        CaptionIdea("Student Life", "Hinglish", "Viva examiner: 'Bas simple sawaal poochunga'", "Me praying to all 33 crore deities!"),
        CaptionIdea("Student Life", "Hindi", "परीक्षा हॉल में जब आगे वाला भी,", "सिर खुजला रहा हो तो तसल्ली मिलती है!"),
        CaptionIdea("Student Life", "Hindi", "पूरे साल किताब नहीं खोली,", "और रात भर में विद्वान बनने का सपना!"),

        // === RELATIONSHIP ===
        CaptionIdea("Relationship", "English", "Me explaining the dramatic plot of my day:", "Him nodding while playing video games"),
        CaptionIdea("Relationship", "English", "Her: 'I don't care where we eat'", "Also Her: 'No, not that place.'"),
        CaptionIdea("Relationship", "English", "Single life status:", "Romantically involved with my bed and snacks"),
        CaptionIdea("Relationship", "English", "Couples taking cute candid photos:", "Us taking 47 pictures to get 1 okay shot"),
        CaptionIdea("Relationship", "Hinglish", "Ghar wale pooch rahe hain shaadi kab karoge,", "Idhar weekend plan decide nahi ho raha!"),
        CaptionIdea("Relationship", "Hinglish", "'Main 5 minute mein ready ho rahi hoon',", "Legends say he is still waiting outside!"),
        CaptionIdea("Relationship", "Hinglish", "Relationship status:", "In a committed relationship with Chai & Maggi ❤️"),
        CaptionIdea("Relationship", "Hindi", "जब वो कहे 'कुछ नहीं हुआ सब ठीक है',", "समझो तूफान आने वाला है!"),
        CaptionIdea("Relationship", "Hindi", "सच्चा प्यार वो है जो आपकी हर बकवास,", "मुस्कुरा कर सुन ले!"),

        // === OFFICE ===
        CaptionIdea("Office", "English", "Per my previous email...", "which clearly you didn't even bother to read."),
        CaptionIdea("Office", "English", "Logging in at 9:00 AM on Monday:", "Already daydreaming about Friday 5:00 PM"),
        CaptionIdea("Office", "English", "Boss: 'We need this done ASAP'", "Me: Pretending my Wi-Fi is buffering 📶"),
        CaptionIdea("Office", "English", "This meeting could have definitely", "been a 2-line email."),
        CaptionIdea("Office", "English", "Me nodding professionally in Zoom call", "while completely muted and eating chips"),
        CaptionIdea("Office", "Hinglish", "Salary aayi nahi ki EMIs ne pehle,", "Apna hissa kaat liya! 💸"),
        CaptionIdea("Office", "Hinglish", "Monday morning alarm:", "Retirement age 25 kyun nahi ho sakti?"),
        CaptionIdea("Office", "Hinglish", "Team call pe camera on karne ko bolo toh,", "Duniya ka sabse bada dukh!"),
        CaptionIdea("Office", "Hindi", "सोमवार का दिन और ऑफिस का काम,", "ऊपर से बॉस का 'Just a quick sync' कॉल!"),
        CaptionIdea("Office", "Hindi", "सैलरी अकाउंट में आई और गई,", "जैसे हवा का झोंका!"),

        // === DESI & CRICKET ===
        CaptionIdea("Relatable", "Hindi", "क्रिकेट के आखिरी ओवर में सांस लेना भी मना है!", "#TeamIndia 🇮🇳"),
        CaptionIdea("Relatable", "Hinglish", "Chai is not a beverage,", "It is an emotion and emergency fuel! ☕")
    )

    fun getIdeasByCategory(category: String, language: String = "All"): List<CaptionIdea> {
        return CAPTION_IDEAS.filter { idea ->
            val matchCategory = category == "All" || idea.category.equals(category, ignoreCase = true) ||
                    (category.contains("Funny", ignoreCase = true) && idea.category == "Funny") ||
                    (category.contains("Relatable", ignoreCase = true) && idea.category == "Relatable") ||
                    (category.contains("Friendship", ignoreCase = true) && idea.category == "Friendship") ||
                    (category.contains("Student", ignoreCase = true) && idea.category == "Student Life") ||
                    (category.contains("Relationship", ignoreCase = true) && idea.category == "Relationship") ||
                    (category.contains("Office", ignoreCase = true) && idea.category == "Office")

            val matchLang = when (language) {
                "All" -> true
                "Hindi (हिन्दी)", "Hindi" -> idea.language == "Hindi"
                "Hinglish" -> idea.language == "Hinglish"
                "English" -> idea.language == "English"
                else -> true
            }

            matchCategory && matchLang
        }
    }
}
