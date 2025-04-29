package com.g40.reflectly.data.utils

fun getXpForLevel(level: Int): Int = 90 + (level * 10)

fun getLevelFromTotalXp(totalXp: Int): Int {
    var xpSum = 0
    for (lvl in 1..50) {
        xpSum += getXpForLevel(lvl)
        if (totalXp < xpSum) return lvl
    }
    return 50
}

fun getXpProgressWithinLevel(totalXp: Int): Pair<Int, Int> {
    var xpSum = 0
    for (lvl in 1..50) {
        val xpForThisLevel = getXpForLevel(lvl)
        if (totalXp < xpSum + xpForThisLevel) {
            val earned = totalXp - xpSum
            return earned to xpForThisLevel
        }
        xpSum += xpForThisLevel
    }
    return 0 to getXpForLevel(50)
}
