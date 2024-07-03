import UI.Console
import UI.GraphView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import graph.GraphException
import graph.RenderableGraph

/*
* CommandProcessor.kt
* Contains the implementation of the CommandProcessor class that handles console commands.
*/

class CommandProcessor {
    companion object {
        fun execute(command: String) {
            execute(command.substringBefore(' '), command.substringAfter(' ', "").split(' '))
        }

        private fun execute(command: String, args: List<String>) {
            when(command) {
                "help", "h" -> Console.println(
                    "Commands available:\n" +
                    "help/h\n" +
                    "add/a vertex/v [name] [x y]\n" +
                    "add/a edge/e first second [weight]\n" +
                    "delete/d vertex/v name\n" +
                    "delete/d edge/e first second\n" +
                    "rename/r name newname\n" +
                    "weight/w first second weight\n" +
                    "clear console\n" +
                    "clear graph\n" +
                    "position - automatically set vertex positions"
                )
                "add", "a" -> executeAdd(args)
                "delete", "d" -> executeDelete(args)
                "rename", "r" -> executeRename(args)
                "weight", "w" -> executeWeight(args)
                "clear" -> executeClear(args)
                "position" -> executePosition(args)
                "" -> return
                else -> Console.println("Unknown command: $command, type help to get available commands list", Color.Red)
            }
        }

        // Executes command add/a ...
        private fun executeAdd(args: List<String>) {
            if (args.isEmpty()) {
                Console.println("Command add takes edge/e or vertex/v as first argument", Color.Red)
                return
            }

            try {
                when (args[0]) {
                    "vertex", "v" -> {
                        if (args.size > 4 || args.size == 3) {
                            Console.println("Command add vertex can take 1, 2, or 4 arguments, but ${args.size} were given", Color.Red)
                        } else {
                            val vertexName: String
                            var vertexPosition: Offset? = null
                            if (args.size >= 2) {
                                vertexName = args[1]
                                if (args.size == 4) {
                                    val x = args[2].toFloatOrNull()
                                    val y = args[3].toFloatOrNull()
                                    if (x == null || y == null) {
                                        Console.println("Vertex position must be a pair of numbers (x, y), but (${args[2]}, ${args[3]}) was given", Color.Red)
                                        return
                                    }

                                    vertexPosition = Offset(x, y)
                                }
                            } else {
                                vertexName = GraphView.renderableGraph.makeUpVertexName()
                            }

                            GraphView.renderableGraph.addVertex(vertexName, vertexPosition)
                            GraphView.onGraphChange(GraphView.renderableGraph)
                            if (vertexPosition == null) {
                                GraphView.repositionVertices()
                            }
                        }
                    }
                    "edge", "e" -> {
                        if (args.size != 3 && args.size != 4) {
                            Console.println("Command add edge must have <first>, <second> and [<weight>] arguments to define an edge", Color.Red)
                        } else {
                            if (args.size == 3) {
                                GraphView.renderableGraph.addEdge(args[1], args[2], 1)
                                GraphView.onGraphChange(GraphView.renderableGraph)
                            } else {
                                val weight = args[3].toIntOrNull()
                                if (weight == null) {
                                    Console.println("Weight must be an integer, but ${args[3]} was given", Color.Red)
                                    return
                                }

                                GraphView.renderableGraph.addEdge(args[1], args[2], weight)
                                GraphView.onGraphChange(GraphView.renderableGraph)
                            }
                        }
                    }
                    else -> Console.println("Command add takes edge/e or vertex/v as first argument", Color.Red)
                }
            } catch (e: GraphException) {
                Console.println("Algorithm exception: ${e.message ?: ""}", Color.Red)
            }
        }

        // Executes command delete/d ...
        private fun executeDelete(args: List<String>) {
            if (args.isEmpty()) {
                Console.println("Command delete takes edge/e or vertex/v as first argument", Color.Red)
                return
            }

            try {
                when (args[0]) {
                    "vertex", "v" -> {
                        if (args.size != 2) {
                            Console.println("Command delete vertex must have <name> argument", Color.Red)
                        } else {
                            GraphView.renderableGraph.removeVertex(args[1])
                            GraphView.onGraphChange(GraphView.renderableGraph)
                        }
                    }
                    "edge", "e" -> {
                        if (args.size != 3) {
                            Console.println("Command delete edge must have <first> and <second> arguments to define an edge", Color.Red)
                        } else {
                            GraphView.renderableGraph.removeEdge(args[1], args[2])
                            GraphView.onGraphChange(GraphView.renderableGraph)
                        }
                    }
                    else -> Console.println("Command delete takes edge/e or vertex/v as first argument", Color.Red)
                }
            } catch (e: GraphException) {
                Console.println("Algorithm exception: ${e.message ?: ""}", Color.Red)
            }
        }

        // Executes command rename/r ...
        private fun executeRename(args: List<String>) {
            if (args.size != 2) {
                Console.println("Command rename takes 2 arguments: name, newname", Color.Red)
            } else {
                try {
                    GraphView.renderableGraph.renameVertex(args[0], args[1])
                    GraphView.onGraphChange(GraphView.renderableGraph)
                } catch (e: GraphException) {
                    Console.println("Algorithm exception: ${e.message ?: ""}", Color.Red)
                }
            }
        }

        // Executes command weight/w ...
        private fun executeWeight(args: List<String>) {
            if (args.size != 3) {
                Console.println("Command weight takes 3 arguments: first, second, weight", Color.Red)
            } else {
                val newWeight = args[2].toIntOrNull()
                if (newWeight == null) {
                    Console.println("Weight must be an integer, but ${args[2]} was given", Color.Red)
                    return
                }

                try {
                    GraphView.renderableGraph.setEdgeWeight(args[0], args[1], newWeight)
                    GraphView.onGraphChange(GraphView.renderableGraph)
                } catch (e: GraphException) {
                    Console.println("Algorithm exception: ${e.message ?: ""}", Color.Red)
                }
            }
        }

        // Executes command clear ...
        private fun executeClear(args: List<String>) {
            if (args.size != 1) {
                Console.println("Command clear takes 1 argument: clear or graph", Color.Red)
            } else {
                when (args[0]) {
                    "console" -> Console.clear()
                    "graph" -> GraphView.onGraphChange(RenderableGraph())
                    else -> Console.println("Unknown argument ${args[0]}, clear takes clear or graph", Color.Red)
                }
            }
        }

        // Executes command position ...
        private fun executePosition(args: List<String>) {
            if (args.isNotEmpty()) {
                Console.println("Command position takes no arguments", Color.Red)
            } else {
                GraphView.repositionVertices()
            }
        }
    }
}