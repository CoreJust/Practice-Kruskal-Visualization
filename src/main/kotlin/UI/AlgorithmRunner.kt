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

class AlgorithmRunner(private val alertDialogHelper: AlertDialogHelper) {
    private var algorithm: Kruskal? = null

    // The history of previous graph color states that allows making a step back
    private val algorithmHistory: ArrayList<GraphColoring> = arrayListOf()
    private var currentStep = 0

    // Initializes the algorithm on algorithm mode start
    fun initAlgorithm() {
        assert(algorithm == null)

        try {
            algorithm = Kruskal(GraphView.renderableGraph)
            algorithm!!.init()
            algorithmHistory.add(GraphView.renderableGraph.graphColoring)
            currentStep = 0
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

        GraphView.renderableGraph.resetColors()
        GraphView.onGraphChange(GraphView.renderableGraph)
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
            alertDialogHelper.open(title = "Algorithm exception", message = "Algorithm is finished, no more steps can be done")
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
        currentStep -= 1
        GraphView.renderableGraph.graphColoring = algorithmHistory[currentStep]
        GraphView.onGraphChange(GraphView.renderableGraph)
    }
}