package com.stathis.util.model

import androidx.compose.ui.graphics.Color
import com.stathis.ui.theme.AngryColor
import com.stathis.ui.theme.AwfulColor
import com.stathis.ui.theme.BoredColor
import com.stathis.ui.theme.CalmColor
import com.stathis.ui.theme.DepressedColor
import com.stathis.ui.theme.DisappointedColor
import com.stathis.ui.theme.HappyColor
import com.stathis.ui.theme.HumorousColor
import com.stathis.ui.theme.LonelyColor
import com.stathis.ui.theme.MysteriousColor
import com.stathis.ui.theme.NeutralColor
import com.stathis.ui.theme.RomanticColor
import com.stathis.ui.theme.ShamefulColor
import com.stathis.ui.theme.SurprisedColor
import com.stathis.ui.theme.SuspiciousColor
import com.stathis.ui.theme.TenseColor
import com.stathis.util.R

enum class Mood(
    val icon: Int,
    val contentColor: Color,
    val containerColor: Color
) {
    Neutral(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = NeutralColor
    ),
    Happy(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = HappyColor
    ),
    Angry(
        icon = R.drawable.logo,
        contentColor = Color.White,
        containerColor = AngryColor
    ),
    Bored(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = BoredColor
    ),
    Calm(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = CalmColor
    ),
    Depressed(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = DepressedColor
    ),
    Disappointed(
        icon = R.drawable.logo,
        contentColor = Color.White,
        containerColor = DisappointedColor
    ),
    Humorous(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = HumorousColor
    ),
    Lonely(
        icon = R.drawable.logo,
        contentColor = Color.White,
        containerColor = LonelyColor
    ),
    Mysterious(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = MysteriousColor
    ),
    Romantic(
        icon = R.drawable.logo,
        contentColor = Color.White,
        containerColor = RomanticColor
    ),
    Shameful(
        icon = R.drawable.logo,
        contentColor = Color.White,
        containerColor = ShamefulColor
    ),
    Awful(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = AwfulColor
    ),
    Surprised(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = SurprisedColor
    ),
    Suspicious(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = SuspiciousColor
    ),
    Tense(
        icon = R.drawable.logo,
        contentColor = Color.Black,
        containerColor = TenseColor
    )
}