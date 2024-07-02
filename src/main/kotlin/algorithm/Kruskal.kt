package algorithm

import UI.Console
import UI.EdgeWindow
import UI.GraphView
import androidx.compose.ui.graphics.Color
import graph.Edge
import graph.RenderableGraph
import kotlin.math.pow
import kotlin.math.sqrt

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
    var edgeList = listOf<Edge>()

    var state:ArrayList<Boolean> = arrayListOf()                         //находится в МОД - true, не находится - false
    val weightMST:Int                                                    //вес МОД
        get() {
            var sum = 0
            for(i in result)
                sum += i.weight
            return sum
        }

    companion object{
        val edgeInMST = Color.Red           //цвет рёбер в МОД
        var skippedEdge = Color.Gray        //цвет пропущенных рёбер

        val textEdgeInMST = Color.Magenta   //цвет текста о ребрах в МОД
        val textSkippedEdge = Color.Gray    //цвет текста о пропущенном ребре

        val allConsoleText = Color.Black    //основной цвет текста в консоли (шаг №n)
        val consoleSkippedEdge = Color(0, 24, 161, 255) //цвет строки про число пропущенных рёбер
        val consoleEdgeInMST = Color(47, 5, 173, 255)    //цвет строки про добавленное ребро в МОД
        val consoleRecoloredVert = Color(132, 0, 176, 255) //цвет строки про число перекрашенных вершин Color(red = 10, green = 140, blue = 191, alpha = 255)
        val consoleMSTWeight = Color.Magenta   //цвет строки с весом МОДа
    }

    fun printInformConsole(stepNum:Int, skiped:Int, added:Edge?, recolored:Int){
        Console.println("_______ Step №$stepNum ________", allConsoleText)
        Console.println("$skiped edges skipped", consoleSkippedEdge)
        if(added != null)
            Console.println("Added ${added.first.name} --(weight = ${added.weight})-- ${added.second.name}", consoleEdgeInMST)
        Console.println("Recolored $recolored vertices", consoleRecoloredVert)
        Console.println("Current MST weight: $weightMST", consoleMSTWeight)
    }


    fun createTextEdges(ind:Int, edgeList:List<Edge>){  //ind - текущее ребро (cur), edgeList - отсортированный список рёбер
        EdgeWindow.clear()                              //очислили окно с рёбрами
        var res:Int = 0
        var cur:String = " skipped"
        var pos = 0
        for(i in 0..edgeList.size - 1)
        {
            if(state[i]) {
                if(i == ind) {
                    EdgeWindow.println(
                        "" + edgeList[i].first.name + " – (w = " + edgeList[i].weight + ") – " + edgeList[i].second.name + " - in MST",
                        textEdgeInMST
                    )   //в дереве красные
                    res += edgeList[i].first.name.length + 12 + edgeList[i].weight.toString().length + edgeList[i].second.name.length + 10
                }
                else {
                    val alpha:Float = 0.4F
                    EdgeWindow.println(
                        "" + edgeList[i].first.name + " – (w = " + edgeList[i].weight + ") – " + edgeList[i].second.name + " - in MST",
                        Color(
                            red = textEdgeInMST.red,
                            green = textEdgeInMST.green,
                            blue = textEdgeInMST.blue,
                            alpha = alpha
                        )
                    )
                    res += edgeList[i].first.name.length + 12 + edgeList[i].weight.toString().length + edgeList[i].second.name.length + 10
                }
            }
            else
            {
                if(i <= ind) {
                    EdgeWindow.println(
                        "" + edgeList[i].first.name + " – (w = " + edgeList[i].weight + ") – " + edgeList[i].second.name + cur,
                        textSkippedEdge
                    )      //пропущенные серые
                    res += edgeList[i].first.name.length + 12 + edgeList[i].weight.toString().length + edgeList[i].second.name.length + cur.length + 1
                }
                else {
                    EdgeWindow.println("" + edgeList[i].first.name + " – (w = " + edgeList[i].weight + ") – " + edgeList[i].second.name)
                    res += edgeList[i].first.name.length + 12 + edgeList[i].weight.toString().length + edgeList[i].second.name.length + 1                  //не рассмотренные чёрные
                }
                if(i == ind)
                    pos = res + 1
            }
        }

        //установка позиции на pos
        EdgeWindow.setAnchor(pos)
        EdgeWindow.render()
    }


    fun init() {                                                            // Изначально окрашивает вершины и рёбра графа, приводит его в исходному состоянию
        if(graph.vertices.size > 500)                                       //если слишком много вершин
            throw TooManyVertices("The number of vertices should not be more than 500, your number of vertices: ${graph.vertices.size}")
        if(graph.splitIntoComponents().size > 1)                            //если больше 1 компоненты связности
            throw TooManyComponents("The number of connectivity components should not be more than 1. The real number of connectivity components: ${graph.splitIntoComponents().size}")
        if(graph.splitIntoComponents().size == 0)                            //если граф пуст
            throw IsEmptyGraph("You cannot run the algorithm on an empty graph")

        edgeList = graph.edges.sortedBy {it.weight}                           //сортируем массив рёбер во возрастанию
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
            return sqrt((a.red - b.red).pow(2) + (a.green - b.green).pow(2) + (a.blue - b.blue).pow(2))
        }

        for(vertex in graph.vertices)
        {
            var color = nextColor()
            while (usedColors.contains(color) || dist(color, RenderableGraph.TEXT_COLOR) < 0.6f) {
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
            if(edgeList[i].color != edgeInMST) {        //если ребро не в МОД
                graph.setEdgeColor(edgeList[i], skippedEdge)
                skiped++
            }

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
            graph.setEdgeColor(edgeList[j], edgeInMST)
            GraphView.onGraphChange(graph)                  //отрисовка графа
        }
        //проверка не прошли ли мы все ребра
        return true                                         //после перекраски возвращаем результат
    }
}