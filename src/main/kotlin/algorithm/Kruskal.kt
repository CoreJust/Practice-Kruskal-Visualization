package algorithm

import graph.Edge
import graph.RenderableGraph
import androidx.compose.ui.graphics.Color //добавлено для строки №1
import UI.GraphView
import kotlin.math.abs

// Заглушка для класса алгоритма
/*
* Предполагаемый формат работы:
* var algorithm: Kruskal? = null
*
* fun onAlgorithmModeStart() {
*   algorithm = Kruskal(graph.copy())
*   try{
*       algorithm.init()
*   } catch (e: AlgorithmException){
*   //........что-то
*   }
*
*
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

class Kruskal(val graph: RenderableGraph) {

    internal var result:ArrayList<Edge> = arrayListOf()                  //массив ребер МОДа
    var algorithmEdgeColor:Color = Color(red = 255, green = 0, blue = 0, alpha = 255)
    val weightMST:Int                                                    //вес МОД
        get() {
            var sum = 0
            for(i in result)
                sum += i.weight
            return sum
        }


    fun setAlgorithmColor(EdgeColor:Color){
        algorithmEdgeColor = EdgeColor                                      // Установили цвет ребра в алгоритме
    }

    fun init() {                                                            // Изначально окрашивает вершины и рёбра графа, приводит его в исходному состоянию
        if(graph.vertices.size > 500)                                       //если слишком много вершин
            throw TooManyVertices("The number of vertices should not be more than 500, your number of vertices: $graph.vertices.size")
        if(graph.splitIntoComponents().size > 1)                            //если больше 1 компоненты связности
            throw TooManyComponents("The number of connectivity components should not be more than 1. The real number of connectivity components: $graph.splitIntoComponents().size")

        val usedColors: HashSet<Color> = hashSetOf(Color.Black, Color.White) //запретили белый и чёрный цвет
        var step = 255
        var countPerComponent = 2
        var i = 0

        fun nextColor(): Color {                                              //расчёт следующего цвета, достаточно отличного от текущего
            val r = (i % countPerComponent) * step
            val g = ((i / countPerComponent) % countPerComponent) * step
            val b = ((i / (countPerComponent * countPerComponent)) % countPerComponent) * step
            i += 1

            if (i >= Math.pow(countPerComponent.toDouble(), 3.0)) {    //!!! countPerComponent * countPerComponent * countPerComponent
                i = 0
                countPerComponent *= 2
                step /= 2
            }

            return Color(red = r, green = g, blue = b, alpha = 255)
        }

        fun dist(a: Color, b: Color): Float {
            return abs(a.red - b.red) + abs(a.green - b.green) + abs(a.blue - b.blue)
        }

        for(vertex in graph.vertices)
        {
            var color = nextColor()
            while (usedColors.contains(color) || dist(color, RenderableGraph.TEXT_COLOR) < 0.4f) {
                color = nextColor()
            }

            usedColors.add(color)

            graph.setVertexColor(vertex, color)   //присваиваем текущей вершине цвет
        }

        GraphView.onGraphChange(graph)                  //отрисовка графа
    }

    fun findNextEdge(cur:Int, edgeList:List<Edge>): Int {
        var i = cur                                     //создадим переменную для поиска
        var newIndex = -1                               //индекс нового ребра
        while(i < edgeList.size)                        //пока не перебрали все рёбра
        {
            if(edgeList[i].first.color != edgeList[i].second.color) {   //если цвета вершин разные
                newIndex = i                            //получаем индекс найдкнного ребра
                break                                   //выходим из цикла
            }
            i++                                         //если текущее ребро не подходит, берём следующее
        }
        return newIndex
    }


    // Делает один шаг алгоритма, изменяя при этом граф
    // Возвращает true, если работа алгоритма ещё не закончена, и false, если алгоритм закончил работу

    fun step(): Boolean {
        val edgeList = graph.edges.sortedBy {it.weight}      //сортируем массив рёбер во возрастанию
        var j = 0                                            //индекс

        if(result.size > 0){
            for(i in 0..edgeList.size - 1) {           //пробегаем по отсортированному массиву ребер
                if(result[result.size - 1] == edgeList[i])   //если текущее ребро массива совпадает с последним ребром МОДа
                {
                    j = i + 1                                //получили индекс первого элемента невключённого в МОД
                    break
                }
            }
        }

        if(j >= graph.edges.size)                           //если все рёбра перебрали то дерево собрано
            return false                                    //алгоритм завершил свою работу

        //найдём следующее ребро, у которого цвета вершин разные
        j = findNextEdge(j, edgeList)                       //метод поиска следующего ребра (малый шаг)
        if(j == -1)
            return false                                    //алгоритм завершил свою работу
        else
        {
            graph.replaceVertexColor(edgeList[j].first.color, edgeList[j].second.color)
            result.add(edgeList[j])                         //добавили ребро в результат
            graph.setEdgeColor(edgeList[j], algorithmEdgeColor)
            GraphView.onGraphChange(graph)                  //отрисовка графа
        }
        //проверка не прошли ли мы все ребра
        return true                                         //после перекраски возвращаем результат
    }
}