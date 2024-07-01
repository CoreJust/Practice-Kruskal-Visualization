package algorithm

open class AlgorithmException(text: String) : Exception(text)

class TooManyVertices(message: String) : AlgorithmException("Message: $message")

class TooManyComponents(message: String): AlgorithmException("Message: $message")

class IsEmptyGraph(message: String): AlgorithmException("Message: $message")