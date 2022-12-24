package year2022.`19`

import java.util.LinkedList
import java.util.PriorityQueue
import readInput
import utils.doWithPrintedTime

data class Resources(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int
) {
    infix operator fun minus(resources: Resources): Resources {
        return Resources(
            ore = ore - resources.ore,
            clay = clay - resources.clay,
            obsidian = obsidian - resources.obsidian,
            geode = geode - resources.geode
        )
    }
}

/**
 * Factory blueprint for building robots
 */
data class Blueprint(
    val index: Int,
    val oreRobotResources: Resources,
    val clayRobotResources: Resources,
    val obsidianRobotResources: Resources,
    val geodeRobotResources: Resources
)


/**
 * Data class representing currently created robots
 */
data class RobotAmount(
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
) {
    fun plusOreRobots(amount: Int): RobotAmount = copy(oreRobots = oreRobots + amount)
    fun plusClayRobots(amount: Int): RobotAmount = copy(clayRobots = clayRobots + amount)
    fun plusObsidianRobots(amount: Int): RobotAmount =
        copy(obsidianRobots = obsidianRobots + amount)

    fun plusGeodeRobots(amount: Int): RobotAmount = copy(geodeRobots = geodeRobots + amount)
}

/**
 * Tuple of currently collected [Resources], robots and remaining time
 */
data class State(
    val resources: Resources,
    val robotAmount: RobotAmount,
    val remainingTime: Int
) : Comparable<State> {

    override fun compareTo(other: State) = compareValuesBy(this, other) {
        it.robotAmount.clayRobots +
            it.robotAmount.oreRobots +
            it.robotAmount.obsidianRobots +
            it.robotAmount.geodeRobots
    }

    fun isBetterThan(other: State): Boolean =
        resources.ore >= other.resources.ore && resources.clay >= other.resources.clay
            && resources.obsidian >= other.resources.obsidian
            && resources.geode >= other.resources.geode
            && robotAmount.oreRobots >= other.robotAmount.oreRobots
            && robotAmount.clayRobots >= other.robotAmount.clayRobots
            && robotAmount.obsidianRobots >= other.robotAmount.obsidianRobots
            && robotAmount.geodeRobots >= other.robotAmount.geodeRobots
}

/**
 * Parse input and return list of [Blueprint]
 */
fun parseInput(input: List<String>): List<Blueprint> {
    fun List<Int>.toBlueprint(): Blueprint {
        return Blueprint(
            index = get(0),
            oreRobotResources = Resources(get(1), 0, 0, 0),
            clayRobotResources = Resources(get(2), 0, 0, 0),
            obsidianRobotResources = Resources(get(3), get(4), 0, 0),
            geodeRobotResources = Resources(get(5), 0, get(6), 0),
        )
    }
    return input
        .map { line ->
            line.split(" ", ":")
                .mapNotNull { it.toIntOrNull() }
                .toBlueprint()
        }
}

/**
 * Provide Initial State with remaining [time]
 */
fun partOneInitialState(time: Int): State = State(
    resources = Resources(ore = 0, clay = 0, obsidian = 0, geode = 0),
    robotAmount = RobotAmount(oreRobots = 1, clayRobots = 0, obsidianRobots = 0, geodeRobots = 0),
    remainingTime = time
)


/**
 * Calculate if there is enough resources to create robot
 */
fun Resources.isEnough(robotRequirements: Resources): Boolean {
    return ore >= robotRequirements.ore
        && clay >= robotRequirements.clay
        && obsidian >= robotRequirements.obsidian
        && geode >= robotRequirements.geode
}

/**
 * Small optimisation:
 * We should not build robot of type A when there are resources of type A that enough for every robot.
 */
private fun decideWhatShouldWeBuild(
    initialState: State,
    blueprint: Blueprint
): Triple<Boolean, Boolean, Boolean> {
    val shouldBuildObsidian = initialState.robotAmount.obsidianRobots < listOf(
        blueprint.clayRobotResources.obsidian,
        blueprint.obsidianRobotResources.obsidian,
        blueprint.oreRobotResources.obsidian,
        blueprint.geodeRobotResources.obsidian,
    ).max()
    val shouldBuildClay = initialState.robotAmount.clayRobots < listOf(
        blueprint.clayRobotResources.clay,
        blueprint.obsidianRobotResources.clay,
        blueprint.oreRobotResources.clay,
        blueprint.geodeRobotResources.clay,
    ).max()
    val shouldBuildOre = initialState.robotAmount.oreRobots < listOf(
        blueprint.clayRobotResources.ore,
        blueprint.obsidianRobotResources.ore,
        blueprint.oreRobotResources.ore,
        blueprint.geodeRobotResources.ore,
    ).max()

    return Triple(shouldBuildObsidian, shouldBuildClay, shouldBuildOre)
}

/**
 * Create set of possible next States given current initial data
 * @param initialState - current state we are in
 * @param afterMinute - same as current state but with resources mined and one minute spent
 * @param blueprint - factory blueprint for producing robots
 */
fun buildNextStepsOfRobots(
    initialState: State,
    afterMinute: State,
    blueprint: Blueprint
): Set<State> {
    val mutableStates = mutableSetOf<State>()

    val initialResources = initialState.resources
    val afterResources = afterMinute.resources

    /**
     * Try to add state when Geode Robot is build
     */
    if (initialResources.isEnough(blueprint.geodeRobotResources)) {
        initialState.copy(
            resources = afterResources - blueprint.geodeRobotResources,
            robotAmount = initialState.robotAmount.plusGeodeRobots(1),
            remainingTime = afterMinute.remainingTime
        ).let(mutableStates::add)
    } else {
        val (shouldBuildObsidian, shouldBuildClay, shouldBuildOre) = decideWhatShouldWeBuild(
            initialState = initialState,
            blueprint = blueprint
        )
        /**
         * Try to add state when Obsidian Robot is build
         */
        if (initialResources.isEnough(blueprint.obsidianRobotResources) && shouldBuildObsidian) {
            initialState.copy(
                resources = afterResources - blueprint.obsidianRobotResources,
                robotAmount = initialState.robotAmount.plusObsidianRobots(1),
                remainingTime = afterMinute.remainingTime
            ).let(mutableStates::add)
        }

        /**
         * Try to add state when Clay Robot is build
         */
        if (initialResources.isEnough(blueprint.clayRobotResources) && shouldBuildClay) {
            initialState.copy(
                resources = afterResources - blueprint.clayRobotResources,
                robotAmount = initialState.robotAmount.plusClayRobots(1),
                remainingTime = afterMinute.remainingTime
            ).let(mutableStates::add)
        }

        /**
         * Try to add state when Ore Robot is build
         */
        if (initialResources.isEnough(blueprint.oreRobotResources) && shouldBuildOre) {
            initialState.copy(
                resources = afterResources - blueprint.oreRobotResources,
                robotAmount = initialState.robotAmount.plusOreRobots(1),
                remainingTime = afterMinute.remainingTime
            ).let(mutableStates::add)
        }

        /**
         * Add state when nothing is build
         */
        mutableStates.add(afterMinute)
    }

    return mutableStates
}

/**
 * Create new state with mined resources and 1 minute spent
 */
fun State.mineAllOre(): State {
    return copy(
        resources = resources.copy(
            ore = resources.ore + robotAmount.oreRobots,
            clay = resources.clay + robotAmount.clayRobots,
            obsidian = resources.obsidian + robotAmount.obsidianRobots,
            geode = resources.geode + robotAmount.geodeRobots,
        ),
        remainingTime = remainingTime - 1
    )
}

/**
 * Calculate max geodes for current [Blueprint]
 */
fun calculateMaxGeodes(initialState: State, blueprint: Blueprint): Int {
    val stateQueue = LinkedList<State>().also { it.add(initialState) }
    val visitedStates = mutableSetOf<State>()
    val bestStates = PriorityQueue<State>()

    fun tryAddState(state: State) {
        if (state in visitedStates) return
        visitedStates.add(state)

        if (bestStates.any { it.isBetterThan(state) }) return
        bestStates.add(state)

        if (bestStates.size > 1000) bestStates.poll()

        stateQueue.add(state)
    }

    var maxGeodes = 0
    while (stateQueue.isNotEmpty()) {
        val currentState = stateQueue.remove()
        val stateWithMinedOres = currentState.mineAllOre()

        if (stateWithMinedOres.remainingTime == 0) {
            maxGeodes = maxOf(maxGeodes, stateWithMinedOres.resources.geode)
            continue
        }

        buildNextStepsOfRobots(currentState, stateWithMinedOres, blueprint)
            .forEach { newState -> tryAddState(newState) }
    }

    return maxGeodes
}


fun main() {

    fun part1(input: List<String>): Int {
        val blueprints = parseInput(input)
        val createInitialState = partOneInitialState(24)
        return blueprints.sumOf { it.index * calculateMaxGeodes(createInitialState, it) }
    }

    fun part2(input: List<String>): Int {
        val first3Blueprints = parseInput(input).take(3)
        val createInitialState = partOneInitialState(32)
        return first3Blueprints
            .map { calculateMaxGeodes(createInitialState, it) }
            .fold(1) { left, right -> left * right }
    }

    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
    doWithPrintedTime("Test 1") { part1(testInput) }
    doWithPrintedTime("Part 1") { part1(input) }
    doWithPrintedTime("Part 2") { part2(input) }
}
