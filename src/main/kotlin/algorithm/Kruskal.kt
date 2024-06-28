package algorithm

import graph.RenderableGraph

// Заглушка для класса алгоритма
/*
* Предполагаемый формат работы:
* var algorithm: Kruskal? = null
*
* fun onAlgorithmModeStart() {
*   algorithm = Kruskal(graph.copy())
*   algorithm.init()
*   renderGraph(algorithm.graph)
* }
*
* fun onAlgorithmStep() {
*   if (!algorithm.step()) {
*       canRunAlgorithm = false
*   }
*
*   algorithmSteps.add(algorithm.graph.copy())
*   renderGraph()
* }
*/

// Для раскраски графа предполагается реализовать некоторый внутренний в рамках модуля algorithm функционал,
// генерирующий множество визуально различных цветов для исходной раскраски графа

class Kruskal(public val graph: RenderableGraph) {
    // Изначально окрашивает вершины и рёбра графа, приводит его в исходному состоянию
    public fun init() {

    }

    // Делает один шаг алгоритма, изменяя при этом граф
    // Возвращает true, если работа алгоритма ещё не закончена, и false, если алгоритм закончил работу
    public fun step(): Boolean {
        return false
    }
}