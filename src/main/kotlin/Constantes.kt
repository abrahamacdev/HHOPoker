object Constantes {

    // El objetivo sera alcanzar la escalera real con un determinado palo
    val OBJETIVO = listOf(
            10,
            11,
            12,
            13,
            14
    )

    // Digitos [0-9a-c]
    val DIGITOS_BASE_13: List<Char> by lazy {
        val list = mutableListOf<Char>()

        list.addAll(IntRange('0'.toInt(), '9'.toInt()).map { it.toChar() })
        list.addAll(IntRange('a'.toInt(), 'c'.toInt()).map { it.toChar() })

        list
    }
}