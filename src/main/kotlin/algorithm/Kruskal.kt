package algorithm

import UI.Console
import UI.EdgeWindow
import UI.GraphView
import androidx.compose.ui.graphics.Color
import graph.Edge
import graph.RenderableGraph
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
    private var stepNum = 0 //номер шага
    private var skiped = 0  //пропущено на текущем шаге

    var state:ArrayList<Boolean> = arrayListOf()                         //находится в МОД - true, не находится - false
    var algorithmEdgeColor:Color = Color(red = 255, green = 0, blue = 0, alpha = 255)
    val weightMST:Int                                                    //вес МОД
        get() {
            var sum = 0
            for(i in result)
                sum += i.weight
            return sum
        }

    fun printInformConsole(stepNum:Int, skiped:Int, added:Edge?, recolored:Int){
        Console.println("___________ Step №$stepNum _____________")
        Console.println("$skiped edges skipped")
        if(added != null)
            Console.println("Added ${added.first.name} --(weight = ${added.weight})-- ${added.second.name}")
        Console.println("Recolored $recolored vertices")
        Console.println("Current MST weight: $weightMST")
    }


    fun createTextEdges(ind:Int, edgeList:List<Edge>){  //ind - текущее ребро (cur), edgeList - отсортированный список рёбер
        var res:String = ""
        var cur:String = " skipped"
        var pos = 0
        for(i in 0..edgeList.size - 1)
        {
            if(state[i])
                res += "" + edgeList[i].first.name + " --(weight = " + edgeList[i].weight + ")-- " + edgeList[i].second.name + " - in MST\n"
            else
            {
                if(i <= ind)
                    res += "" + edgeList[i].first.name + " --(weight = " + edgeList[i].weight + ")-- " + edgeList[i].second.name + cur + "\n"
                else
                    res += "" + edgeList[i].first.name + " --(weight = " + edgeList[i].weight + ")-- " + edgeList[i].second.name + "\n"
                if(i == ind)
                    pos = res.length + 1
            }
        }

        //вывод списка ребер в отдельное окно + установка позиции на cur
        EdgeWindow.clear()
        EdgeWindow.println(res)
        EdgeWindow.setAnchor(pos)
        EdgeWindow.render()
    }

    fun setAlgorithmColor(EdgeColor:Color){
        algorithmEdgeColor = EdgeColor                                      // Установили цвет ребра в алгоритме
    }

    fun init() {                                                            // Изначально окрашивает вершины и рёбра графа, приводит его в исходному состоянию
        if(graph.vertices.size > 500)                                       //если слишком много вершин
            throw TooManyVertices("The number of vertices should not be more than 500, your number of vertices: ${graph.vertices.size}")
        if(graph.splitIntoComponents().size > 1)                            //если больше 1 компоненты связности
            throw TooManyComponents("The number of connectivity components should not be more than 1. The real number of connectivity components: ${graph.splitIntoComponents().size}")

        val usedColors: HashSet<Color> = hashSetOf(Color.Black, Color.White) //запретили белый и чёрный цвет
        var step = 256
        var countPerComponent = 2
        var i = 0

        fun nextColor(): Color {                                              //расчёт следующего цвета, достаточно отличного от текущего
            val r = minOf((i % countPerComponent) * step, 255)
            val g = minOf(((i / countPerComponent) % countPerComponent) * step, 255)
            val b = minOf(((i / (countPerComponent * countPerComponent)) % countPerComponent) * step, 255)
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
            skiped++
            createTextEdges(i, edgeList)                //проход по каждому ребру в поиске необходимого
            i++                                         //если текущее ребро не подходит, берём следующее
        }
        return newIndex
    }

    fun setStartState(edgeList:List<Edge>)
    {
        for(k in edgeList){                                  //записали состояния рёбер на текущем шаге
            if(result.contains(k))
                state.add(true)
            else
                state.add(false)
        }
    }

    // Делает один шаг алгоритма, изменяя при этом граф
    // Возвращает true, если работа алгоритма ещё не закончена, и false, если алгоритм закончил работу

    fun step(): Boolean {
        stepNum++
        skiped = 0
        val edgeList = graph.edges.sortedBy {it.weight}      //сортируем массив рёбер во возрастанию
        setStartState(edgeList)                              //записали список состояний для каждого ребра по порядку

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
        createTextEdges(j-1, edgeList)                  //записали рёбра (до итерации)

        if(j >= graph.edges.size) {                           //если все рёбра перебрали то дерево собрано
            printInformConsole(stepNum, skiped, null, 0) //вывод в консоль
            return false                                    //алгоритм завершил свою работу
        }

        //найдём следующее ребро, у которого цвета вершин разные
        j = findNextEdge(j, edgeList)                       //метод поиска следующего ребра (малый шаг)

        if(j == -1) {
            printInformConsole(stepNum, skiped, null, 0) //вывод в консоль
            return false                                    //алгоритм завершил свою работу
        }
        else
        {
            val recolored = graph.replaceVertexColor(edgeList[j].first.color, edgeList[j].second.color)
            result.add(edgeList[j])                         //добавили ребро в результат
            state[j] = true                                 //отметили, что ребро в МОД
            printInformConsole(stepNum, skiped, edgeList[j], recolored) //вывод в консоль
            createTextEdges(j, edgeList)                    //вывели ребра после добавления
            graph.setEdgeColor(edgeList[j], algorithmEdgeColor)
            GraphView.onGraphChange(graph)                  //отрисовка графа
        }
        //проверка не прошли ли мы все ребра
        return true                                         //после перекраски возвращаем результат
    }
}