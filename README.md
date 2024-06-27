# Practice-Kruskal-Visualization

Репозиторий летней практики 2024 бригады №1:

1) Ефремова Анна, группа 2303
2) Ильин Егор, группа 2303
3) Ламашовский Денис, группа 2303

## Спецификации

### 1. Постановка задачи
Разработка программы на языке Kotlin для визуализации работы алгоритма Краскала построения МОД.

### 2. Описание алгоритма Краскала
1. На входе имеется неориентированный взвешенный униграф (т.е. без мультирёбер), без петель.
2. Всем вершинам присвается цвет (у каждой вершины он изначально разный).
3. Создаётся список рёбер и сортируется в порядке невозрастания веса.
4. Выбираем из списка очередное ребро (наименьшее по весу среди нерассмотренных). В случае, если вершины, инцидентные этому ребру, разного цвета, то включаем это ребро в МОД, иначе рассматриваем следующее ребро.
5. Если очередное ребро имеет вершины разного цвета на концах - добавляем ребро в МОД, окрашиваем все вершины одного из цветов на концах в цвет второй вершины (т.е. изначально ребро соединяет два кластера вершин, каждый из которых имеет свой цвет, а после кластеры объединяются и один из них получает цвет второго).
6. Повторяем шаги 4-5 пока не достигнут конец списка.

#### 2.1. Псевдокод алгоритма Краскала
```sh
fun findMSTWithKruskal(G: Graph): List<Edge> {
    G.vertices.forEach { it.color = getColorFromIndex(it.index) } // Присваиваем различные цвета вершинам
    
    val result: ArrayList<Edge> = arrayListOf()
    val edgeList = G.edges.sortedBy { it.weight } // Создаём отсортированный по весу список рёбер
    var i = 0
    while (i < edgeList.size) {
        val edge = edgeList[i] // Берём очередное ребро
        if (edge.first.color != edge.second.color) { // Если вершины на концах ребра разного цвета
            G.replaceVertexColor(oldColor = edge.first.color, newColor = edge.second.color) // Меняем цвет всех вершин одного из цветов на концах ребра в цвет вершины на другом конце ребра
            result.add(edge) // Добавляем в МОД
        }
        
        i += 1
    }
    
    return edgeList
}
```

### 3. Техническое задание к разрабатываемой программе
#### 3.1. Ввод исходных данных:
Пользователю предоставляется несколько способов введения исходных данных:
1) Загрузка графа из файла в одном из поддерживаемых форматов:
   a) Файл .tgf (Trivial Graph Format) (ссылка)
   b) Файл .dot (.gv) (DOT - язык описания графов) (ссылка)
2) Создание графа непосредственно в среде приложения на холсте. Для этого сначала надо включить редактирование:
   В рамках режима редактирования есть 2 подрежима: режим редактирования вершин и режим редактирования ребер.
   a) Режим редактирования вершин:
   Можно нажатием ЛКМ добавить в месте нажатия новую вершину, либо переместить существующую, зажав её. Нажатием ПКМ вершину можно удалить.
   b) Режим редактирования ребер:
   Нажатием ЛКМ / ПКМ по одной из вершин можно ее выделить, далее нажатием ЛКМ на другую вершину вставить между ними ребро. Вес ребра по-умолчанию равен единице. Используя вместо ЛКМ ПКМ на второй вершине ребро между вершинами можно удалить.
   c) Есть возможность отменить последнее действие и заново сделать его используя комбинации клавиш Ctrl+Z и Ctrl+Y, однако при переключении режимов история действий сбрасывается. Можно использовать встроенные функции очистки холста, а также вставки полного либо нуль-графа с указанным числом вершин. Созданный таким образом граф можно также сохранить в файл (в одном из доступных форматов).
   d) Можно временно использовать противоположный подрежим, зажав Shift (после отжатия режим вернется обратно).
   Помимо редактирования на холсте, возможно использовать текстовый ввод (в котором можно вводить последовательность команд для редактирования графа, например, очистка графа, вставка вершины / ребра и т.д.).

#### 3.2. Визуализация:
Текстовое описание:
Графический интерфейс программы содержит следующие составляющие:
В верхней части расположено меню:
1) Вкладка “File” - содержит кнопки “Load graph”, “Save graph”, “Exit”
2) Вкладка “Mode” - содержит радиокнопки “Edit mode” и “Algorithm mode”
3) Вкладка “Options” - кнопки “Graph render options” и “Algorithm options”
4) Вкладка “Info” - кнопки “Guide” и “About program”.

Остальное меню меняется в зависимости от режима:
Режим редактирования:
1) В холсте (поле с графом) граф с возможностью редактирования в соответствии с пунктом 2 описания входных данных (помимо прочего, возможно приближать/отдалять граф и перемещаться по полю, зажав пустое место и двигая мышь).
2) Текстовое окошко работает в режиме условной консоли, в которую можно вводить команды для редактирования графа (по типу add node A, del edge A-B, clear (для очистки графа), и т.д.). Под консолью - кнопка “Execute” для выполнения введенных команд.
3) Справа от холста набор кнопок:
   a) Переключающаяся кнопка “Редактирование графа” и “Редактирование ребер” - два состояния, при нажатии режим меняется на противоположный (на деле будут использованы картинки, т.к. названия просто не влезут).
   b) Кнопка для очистки графа
   c) Кнопка для вставки шаблонного графа (открывает окошко в котором выбирается тип графа и его характеристики, например, полный и нуль-граф и их размер)
   d) Кнопка для авторасположения графа (программа автоматически распределяет граф как если бы он был загружен из файла TGF без указания позиций вершин)
   e) Кнопка для получения информации о графе (например, число вершин, число ребер, число комнонент связности)

Режим алгоритма:
1) В холсте возможно только перемещать вершины (но не редактировать граф)
2) Текстовое окошко используется для вывода подробностей о работе алгоритма.
3) Справа от холста:
   a) Кнопка для авторасположения графа (программа автоматически распределяет граф как если бы он был загружен из файла)
   b) Кнопка для получения информации о графе
4) Под консолью - кнопки “Шаг назад”, “Авторабота алгоритма”, “Шаг вперед”. При нажатии “Авторабота алгоритма” сама кнопка меняется на “Остановить автоработу”, соседние кнопки на “Быстрее” и “Медленнее”.


[![photo-2024-06-27-19-35-58.jpg](https://i.postimg.cc/4xPKfpFT/photo-2024-06-27-19-35-58.jpg)](https://postimg.cc/xXkjgN2s)
Интерфейс в режиме редактирования.

[![photo-2024-06-27-19-48-16.jpg](https://i.postimg.cc/qR3M3KQq/photo-2024-06-27-19-48-16.jpg)](https://postimg.cc/yg7HqkMC)
Интерфейс в режиме работы алгоритма.

### 3.3. Работа приложения
Режим редактирования был описан выше. Второй режим - режим работы алгоритма. В нем можно сделать шаг алгоритма вперед/назад. Можно включить работу алгоритма с некоторой скоростью (и тогда шаги будут выполняться автоматически с некоторым интервалом), а также приостановить её.
Работа алгоритма сохраняется в историю, история логируется. Файл лога можно использовать для отладки программы и поиска причины ошибки.

## 4. План работы
1) 28.06.2024 - защита вводного задания, демонстрация спецификации и плана
   К этому этапу планируется иметь план и спецификации, по которым в дальнейшем будет идти разработка приложения.
2) 03.07.2024 - защита прототипа
   В рамках прототипа планируется как минимум иметь интерфейс, местами без функционала, возможность открывать и просматривать граф, возможность запустить алгоритм и получить его результат (без пошагового исполнения).
3) 05.07.2024 - бета версия
   В рамках бета-версии планируется завершить разработку основного функционала, внесение правок по итогам защиты прототипа.
4) 08.07.2024 - релиз
   В рамках релиза планируется подправить интерфейс приложения, внести правки по результатам защиты бета-версии, покрыть приложение тестами и отладить.
5) 09.07.2024 - отчёт

## 5. Распределение ролей
Ламашовский Денис - работа с файлами, с логом, тесты, часть отчёта со своим кодом.

Ефремова Анна - реализация алгоритма, отчёт

Ильин Егор - API графа, визуализация, интерфейс, часть отчёта со своим кодом.
