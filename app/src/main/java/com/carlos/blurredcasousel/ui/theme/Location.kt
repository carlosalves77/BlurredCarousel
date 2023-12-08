package com.carlos.blurredcasousel.ui.theme

import com.carlos.blurredcasousel.R

data class Location(
    val image: Int,
    val title: String,
    val subtitle: String,
    val rating: Int,
)

val locations = listOf(
    Location(
        image = R.drawable.tabriz,
        title = "Tabriz",
        subtitle = "A city in Iran",
        rating = 5
    ),
    Location(
        image = R.drawable.dubai,
        title = "Dubai",
        subtitle = "A city in United Arab emirates",
        rating = 3
    ),
    Location(
        image = R.drawable.london,
        title = "London",
        subtitle = "Capital of England and the United Kingdom",
        rating = 3
    ),
    Location(
        image = R.drawable.los_angeles,
        title = "Los Angels",
        subtitle = "a sprawling Southern California City",
        rating = 4
    ),
    Location(
        image = R.drawable.sweden,
        title = "Sweden",
        subtitle = "A beautiful country",
        rating = 5
    ),
    Location(
        image = R.drawable.kazan,
        title = "Kazan",
        subtitle = "A city in Russia",
        rating = 5
    )
)
