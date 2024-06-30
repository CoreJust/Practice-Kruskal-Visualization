/*
* UI.AlgorithmRunner.kt
* Contains the implementation of the AlgorithmRunner class that handles the algorithm actions.
* It gets external signals like algorithm init, destroy, step forth, step back, run with specified speed,
* and in accordance with those signals executes algorithm steps and applies them to the graph.
*/

package UI

import algorithm.AlgorithmException
import algorithm.Kruskal
import graph.GraphColoring
import java.util.*
import kotlin.concurrent.schedule

class AlgorithmRunner(private val alertDialogHelper: AlertDialogHelper) {
    private var algorithm: Kruskal? = null

    // The history of previous graph color states that allows making a step back
    private val algorithmHistory: ArrayList<GraphColoring> = arrayListOf()
    private var currentStep = 0

    private var hasMoreSteps = true
    private var isRunning = false
    private var stepInterval: Long = 1000
    private var runningTimer = Timer()

    // Initializes the algorithm on algorithm mode start
    fun initAlgorithm() {
        assert(algorithm == null)

        try {
            algorithm = Kruskal(GraphView.renderableGraph)
            algorithm!!.init()
            algorithmHistory.add(GraphView.renderableGraph.graphColoring)
            currentStep = 0
            hasMoreSteps = true
            isRunning = false

            EdgeWindow.isOpen = true
        } catch (e: AlgorithmException) {
            alertDialogHelper.open(title = "Algorithm exception", message = e.message ?: "")
        }
    }

    // Destroys internal state upon returning back to the edit mode
    fun destroyAlgorithm() {
        assert(algorithm != null)

        algorithm = null
        algorithmHistory.clear()
        currentStep = 0
        hasMoreSteps = false

        EdgeWindow.isOpen = false

        GraphView.renderableGraph.resetColors()
        GraphView.onGraphChange(GraphView.renderableGraph)
    }

    // Starts executing the algorithm at a fixed interval of stepInterval
    fun run() {
        pause() // In case it is already running

        isRunning = true
        runningTimer.schedule(delay = stepInterval, period = stepInterval) {
            try {
                stepForth()
            } catch (e: AlgorithmException) {
                alertDialogHelper.open(
                    title = "Algorithm exception",
                    message = e.message ?: ""
                )
            }

            if (!hasMoreSteps) {
                pause()
            }
        }
    }

    // Pauses the running execution
    fun pause() {
        if (isRunning) {
            isRunning = false
            runningTimer.cancel()
            runningTimer = Timer()
        }
    }

    // Increases algorithm execution speed
    fun accelerate() {
        stepInterval = Math.clamp(stepInterval * 4 / 5, 80L, 5000L)
        run()
    }

    // Decreases algorithm execution speed
    fun decelerate() {
        stepInterval = Math.clamp(stepInterval * 5 / 4, 80L, 5000L)
        run()
    }

    // Does a single algorithm step
    fun stepForth() {
        assert(algorithm != null)

        if (currentStep < algorithmHistory.size - 1) { // If the next step was already done
            currentStep += 1
            GraphView.renderableGraph.graphColoring = algorithmHistory[currentStep]
            GraphView.onGraphChange(GraphView.renderableGraph)

            return
        }

        if (!algorithm!!.step()) { // Check if no more algorithm steps can be done
            alertDialogHelper.open(title = "Info", message = "Algorithm is finished, no more steps can be done")
            hasMoreSteps = false
            return
        }

        // Otherwise, a step was successfully made
        algorithmHistory.add(GraphView.renderableGraph.graphColoring)
        currentStep += 1
    }

    // Does a single algorithm step back
    fun stepBack() {
        assert(algorithm != null)

        if (currentStep <= 0) { // Check if there are steps that can be undone
            alertDialogHelper.open(title = "Algorithm exception", message = "You are in the algorithm initial state, no steps to be undone")
            return
        }

        // Otherwise, a step can be undone
        hasMoreSteps = true
        currentStep -= 1
        GraphView.renderableGraph.graphColoring = algorithmHistory[currentStep]
        GraphView.onGraphChange(GraphView.renderableGraph)
    }
}