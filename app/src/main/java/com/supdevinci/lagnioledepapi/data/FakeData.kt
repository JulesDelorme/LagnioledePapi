package com.supdevinci.lagnioledepapi.data

import com.supdevinci.lagnioledepapi.model.*

object FakeData {
    val jokes = listOf(
        "C'est pas de l'alcoolisme, c'est de l'oenologie sportive ! 🍺",
        "Tu sais pourquoi les poissons n'aiment pas le tennis ? Ils ont peur du filet ! 🎾",
        "Au PMU, même l'eau a l'air de mentir.",
        "J'ai arrêté de boire de l'eau: trop de poissons dedans.",
        "Le Ricard, c'est du soleil liquide pour les gens de mauvaise foi. 🍋",
        "Plus c'est trouble, plus ça a du goût. Enfin je crois.",
        "Le vin rouge, c'est du raisin qui a fait une connerie.",
        "Une bière artisanale, c'est juste une mousse avec un CV.",
        "Le dimanche matin, même le tire-bouchon fait grise mine.",
        "Si le pastis collait moins, on en mettrait sur les tartines.",
        "J'ai un palais très fin: je reconnais le cubi au bruit du bouchon.",
        "Le mojito, c'est de la salade avec des responsabilités.",
        "Le rhum arrange tout, surtout quand il est arrangé.",
        "Y a des gens sobres, mais on n'a pas élevé les cochons ensemble.",
        "Le premier verre appelle le second, et le troisième arrive sans rendez-vous.",
        "Au bistrot, le cardio c'est lever le coude avec régularité.",
        "La modération, c'est juste l'entracte entre deux tournées.",
        "Le champagne, c'est quand le beauf met une chemise blanche.",
        "Une soirée réussie, c'est quand le glaçon abandonne avant toi.",
        "Souffler dans le téléphone, c'est la version terroir du Bluetooth. 💨"
    )

    val rankings = listOf(
        RankingEntry(1, "Carlos El Borracho", 532, "Glouglou Maximo", RankingRegion.MONDIAL, "🇪🇸"),
        RankingEntry(2, "Marco La Bouteille", 506, "Gosier d'Acier", RankingRegion.MONDIAL, "🇮🇹"),
        RankingEntry(3, "Dédé Le Soiffard", 498, "Foie de Champion", RankingRegion.MONDIAL, "🇫🇷"),
        RankingEntry(4, "Hans La Chope", 481, "Sacré Poivrot", RankingRegion.MONDIAL, "🇩🇪"),
        RankingEntry(5, "Olga La Goulue", 459, "Reine du Glaçon", RankingRegion.MONDIAL, "🇵🇱"),
        RankingEntry(6, "Hans La Chope", 487, "Sacré Poivrot", RankingRegion.EUROPEEN, "🇩🇪"),
        RankingEntry(7, "Marco La Bouteille", 463, "Bec Fin de Zinc", RankingRegion.EUROPEEN, "🇮🇹"),
        RankingEntry(8, "Olga La Goulue", 451, "Reine du Glaçon", RankingRegion.EUROPEEN, "🇵🇱"),
        RankingEntry(9, "Sven Le Fût", 437, "Mousse Nordique", RankingRegion.EUROPEEN, "🇸🇪"),
        RankingEntry(10, "Manu La Sangria", 425, "Paëlla Liquide", RankingRegion.EUROPEEN, "🇪🇸"),
        RankingEntry(11, "Dédé Le Soiffard", 523, "Foie de Champion", RankingRegion.FRANCAIS, "🇫🇷"),
        RankingEntry(12, "Jean-Mi L'Assoiffé", 456, "Sacré Poivrot", RankingRegion.FRANCAIS, "🇫🇷"),
        RankingEntry(13, "Jojo Le Demi", 441, "Mitraillette à Mousse", RankingRegion.FRANCAIS, "🇫🇷"),
        RankingEntry(14, "Ginette du PMU", 428, "Tireuse d'Élite", RankingRegion.FRANCAIS, "🇫🇷"),
        RankingEntry(15, "Momo du Comptoir", 417, "Tonton du Houblon", RankingRegion.FRANCAIS, "🇫🇷")
    )

    val drinkPresets = listOf(
        DrinkPreset("beer", "Bière", 330, 5.0, "🍺"),
        DrinkPreset("wine", "Vin", 150, 12.0, "🍷"),
        DrinkPreset("vodka", "Vodka", 40, 40.0, "🍸"),
        DrinkPreset("whisky", "Whisky", 40, 40.0, "🥃"),
        DrinkPreset("rhum", "Rhum", 40, 40.0, "🏴‍☠️"),
        DrinkPreset("gin", "Gin", 40, 40.0, "🍋"),
        DrinkPreset("champagne", "Champagne", 150, 12.0, "🥂"),
        DrinkPreset("pastis", "Pastis", 45, 45.0, "🫒")
    )
}
