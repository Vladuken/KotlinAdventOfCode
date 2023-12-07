package year2023.`07`

import readInput
import utils.printlnDebug

private const val CURRENT_DAY = "07"


sealed class Card {
    abstract val order: Int

    data class A(override val order: Int = 14) : Card()
    data class K(override val order: Int = 13) : Card()
    data class Q(override val order: Int = 12) : Card()
    data class J(override val order: Int = 11) : Card()
    data class T(override val order: Int = 10) : Card()
    data class Card9(override val order: Int = 9) : Card()
    data class Card8(override val order: Int = 8) : Card()
    data class Card7(override val order: Int = 7) : Card()
    data class Card6(override val order: Int = 6) : Card()
    data class Card5(override val order: Int = 5) : Card()
    data class Card4(override val order: Int = 4) : Card()
    data class Card3(override val order: Int = 3) : Card()
    data class Card2(override val order: Int = 2) : Card()
}


fun String.toCard(isJoker: Boolean): Card {
    return when (this) {
        "A" -> Card.A()
        "K" -> Card.K()
        "Q" -> Card.Q()
        "J" -> Card.J().let {
            it.copy(order = it.order.takeUnless { isJoker } ?: 1)
        }

        "T" -> Card.T()
        "9" -> Card.Card9()
        "8" -> Card.Card8()
        "7" -> Card.Card7()
        "6" -> Card.Card6()
        "5" -> Card.Card5()
        "4" -> Card.Card4()
        "3" -> Card.Card3()
        "2" -> Card.Card2()
        else -> error("Failed to parse $this")
    }
}

fun Card.toValue(): String {
    return when (this) {
        is Card.A -> "A"
        is Card.K -> "K"
        is Card.Q -> "Q"
        is Card.J -> "J"
        is Card.T -> "T"
        is Card.Card9 -> "9"
        is Card.Card8 -> "8"
        is Card.Card7 -> "7"
        is Card.Card6 -> "6"
        is Card.Card5 -> "5"
        is Card.Card4 -> "4"
        is Card.Card3 -> "3"
        is Card.Card2 -> "2"
    }
}

sealed class CardSet {
    abstract val setOfCards: List<Card>
    abstract val id: Int
    abstract val bid: Long

    data class FiveOfAKind(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 6,
    ) : CardSet()

    data class FourOfAKind(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 5,
    ) : CardSet()

    data class FullHouse(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 4,
    ) : CardSet()

    data class ThreeOfAKind(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 3,
    ) : CardSet()

    data class TwoPair(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 2,
    ) : CardSet()

    data class OnePair(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 1,
    ) : CardSet()

    data class HighCard(
        override val setOfCards: List<Card>,
        override val bid: Long,
        override val id: Int = 0,
    ) : CardSet()

    fun withAnotherCards(cards: List<Card>): CardSet {
        return when (this) {
            is FiveOfAKind -> copy(setOfCards = cards)
            is FourOfAKind -> copy(setOfCards = cards)
            is FullHouse -> copy(setOfCards = cards)
            is ThreeOfAKind -> copy(setOfCards = cards)
            is TwoPair -> copy(setOfCards = cards)
            is OnePair -> copy(setOfCards = cards)
            is HighCard -> copy(setOfCards = cards)
        }
    }
}

private fun mapStringIntoCard(line: String, isJoker: Boolean): List<Card> {
    return line
        .split("")
        .filter { it.isNotBlank() }
        .map { it.toCard(isJoker) }
}

private fun mapLineInto(line: String, isJoker: Boolean): Pair<List<Card>, Long> {
    val (setOfCards, bidString) = line.split(" ")
    val cards = mapStringIntoCard(setOfCards, isJoker)
    val bid = bidString.toLongOrNull() ?: error("mapLineInto illegal bidString $bidString")
    return cards to bid
}

private fun Pair<List<Card>, Long>.toCardSet(): CardSet {
    val cardList = first
    val bid = second

    return when {
        cardList.isFiveOfAKind() -> CardSet.FiveOfAKind(cardList, bid)
        cardList.isFourOfAKind() -> CardSet.FourOfAKind(cardList, bid)
        cardList.isFullHouse() -> CardSet.FullHouse(cardList, bid)
        cardList.isThreeOfAKind() -> CardSet.ThreeOfAKind(cardList, bid)
        cardList.isTwoPairs() -> CardSet.TwoPair(cardList, bid)
        cardList.isOnePair() -> CardSet.OnePair(cardList, bid)
        cardList.isHighCard() -> CardSet.HighCard(cardList, bid)
        else -> error("toCardSet illegal list $cardList")
    }
}

fun CardSet.findBestVersionOfItself(): CardSet {
    val howMuchJ = this.setOfCards.count { it is Card.J }
    val initialString = this.setOfCards.joinToString("") { it.toValue() }

    var cardSetsString = listOf(initialString)
    repeat(howMuchJ) {
        cardSetsString = cardSetsString.flatMap { currentInitialString ->
            prepareAllChangesForJ(currentInitialString)
        }
    }

    val sortedRes = cardSetsString.map { setString ->
        val cards = mapStringIntoCard(
            line = setString,
            isJoker = false
        ) to this.bid

        cards.toCardSet()
    }
        .sortedWith(comparatorForCardSets())

    return sortedRes.last()
        .withAnotherCards(setOfCards)
}

private val indexOfChange = listOf(
    "A",
    "K",
    "Q",
    "T",
    "9",
    "8",
    "7",
    "6",
    "5",
    "4",
    "3",
    "2",
)

private fun prepareAllChangesForJ(line: String): List<String> {
    return indexOfChange.map {
        line.replaceFirst("J", it)
    }
}

// region List of cards to card type
fun List<Card>.isFiveOfAKind(): Boolean {
    return map { it.toValue() }.distinct().size == 1
}

fun List<Card>.isFourOfAKind(): Boolean {
    val group = groupBy { it.toValue() }.values
    return group.any { it.size == 4 } // && group.any { it.size == 1 }
}

fun List<Card>.isFullHouse(): Boolean {
    val group = groupBy { it.toValue() }.values
    return group.any { it.size == 3 } && group.any { it.size == 2 }
}

fun List<Card>.isThreeOfAKind(): Boolean {
    val group = groupBy { it.toValue() }.values
    return group.any { it.size == 3 } // && group.any { it.size == 2 }
}


fun List<Card>.isTwoPairs(): Boolean {
    val group = groupBy { it.toValue() }.values
    val sizesList = group.map { it.size }.sorted()
    return sizesList == listOf(1, 2, 2)
}

fun List<Card>.isOnePair(): Boolean {
    val group = groupBy { it.toValue() }.values
    val sizesList = group.map { it.size }.sorted()
    return sizesList == listOf(1, 1, 1, 2)
}

fun List<Card>.isHighCard(): Boolean {
    val group = groupBy { it.toValue() }.values
    return group.size == 5
}
// endregion

private fun comparatorForCardSets(): Comparator<CardSet> = compareBy(
    { it.id },
    { it.setOfCards.first().order },
    { it.setOfCards[1].order },
    { it.setOfCards[2].order },
    { it.setOfCards[3].order },
    { it.setOfCards[4].order },
)

fun main() {

    fun part1(input: List<String>): Long {
        val sortedSets = input.map { mapLineInto(it, false) }
            .map { it.toCardSet() }
            .sortedWith(comparatorForCardSets())
            .mapIndexed { index, cardSet -> cardSet to index.inc() }
            .reversed()

        sortedSets.forEach { printlnDebug { it } }

        return sortedSets.sumOf { (cardSet, index) -> cardSet.bid * index }
    }

    fun part2(input: List<String>): Long {
        val sortedSets = input.map { mapLineInto(it, true) }
            .map { it.toCardSet().findBestVersionOfItself() }
            .sortedWith(comparatorForCardSets())
            .mapIndexed { index, cardSet -> cardSet to index.inc() }
            .reversed()
        sortedSets.forEach { printlnDebug { it } }

        return sortedSets.sumOf { (cardSet, index) -> cardSet.bid * index }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${CURRENT_DAY}_test")
    val part1Test = part1(testInput)

    println(part1Test)
    check(part1Test == 6440L)

    val input = readInput("Day$CURRENT_DAY")

    // Part 1
    val part1 = part1(input)
    println(part1)
    check(part1 == 247961593L)

    // Part 2
    val part2 = part2(input)
    println(part2)
    check(part2 == 248750699L)
}
