package modelo

import com.github.ajalt.mordant.TermColors

enum class Palo(val rango: IntRange) {
    Corazones(IntRange(0,2)),
    Picas(IntRange(3,5)),
    Rombos(IntRange(6,8)),
    Treboles(IntRange(9,11));


    companion object {
        private val color = TermColors()

        fun getPaloConId(id: Int): Palo? {
            return Palo.values().firstOrNull { id in it.rango }
        }
    }

    override fun toString(): String {

        var estilos = color.inverse
        var imagen: String = ""

        when {
            this.equals(Picas) -> {
                imagen = "\u2660"
            }

            this.equals(Treboles) -> {
                imagen = "\u2663"
            }

            this.equals(Rombos) -> {
                estilos = (color.red on color.gray)
                imagen = "\u2666"
            }

            this.equals(Corazones) -> {
                estilos = (color.red on color.gray)
                imagen = "\u2665"
            }
        }

        return estilos(imagen)
    }
}