package modelo

import java.math.BigInteger
import java.util.HashSet

/*
    Esta clase representa una carta. Cuando creemos una carta, esta tendra una representacion hexadecimal
    que la identifica inequivocamente. Esta representacion se crea de la siguiente forma:
    (Id Palo, NumeroCarta-1). Ej: 4 de corazones = 02 | As de rombos = 2C

 */
class Carta: Comparable<Carta>{

    var numeroCarta: Int = Int.MIN_VALUE
    var palo: Palo? = null

    lateinit var idTrec: String                 // Representacion de la carta como un numero "trezal"
    var idTrecEnDec: Long = Long.MIN_VALUE      // Anterior numero hexadecimal convertido a decimal

    companion object {

        val LB = Utils.trecToDecimal("00")  // 2 de corazones
        val UB = Utils.trecToDecimal("CC")  // As de "desconocido"

        val cartasPorPalo by lazy {
            val todas = mutableMapOf<Palo, List<Carta>>()

            for (palo in Palo.values()){
                todas.put(palo, IntRange(2,14).map { Carta(it, palo) }.toList())
            }

            todas
        }

        val todasCartas = cartasPorPalo.values.flatten().toHashSet()

        fun generarCartaAleatoria(palo: Palo? = null, noRepetir: Set<Carta> = HashSet(0)): Carta{

            var sinRepetidas: List<Carta>
            if (palo != null){
                sinRepetidas = cartasPorPalo.get(palo)!!.filterNot { it in noRepetir }
            }
            else {
                sinRepetidas = todasCartas.filterNot { it in noRepetir }
            }

            return sinRepetidas[(Math.random() * sinRepetidas.size).toInt()]
        }
    }

    constructor(numeroCarta: Int, palo: Palo){
        crearCartaNormal(numeroCarta, palo)
    }

    constructor(idTrec: String){
        crearCartaIdTrec(idTrec)
    }


    private fun crearCartaNormal(numeroCarta: Int, palo: Palo){

        if (numeroCarta < 2 || numeroCarta > 14){
            throw InstantiationError("El numero de la carta sobrepasa los limites")
            return
        }

        this.numeroCarta = numeroCarta
        this.palo = palo
        this.idTrec = Utils.decToTrezalString(palo.rango.last.toLong()) + Utils.decToTrezalString((numeroCarta-2).toLong())
        this.idTrecEnDec = Utils.cambiarDeBase(idTrec, 13)
    }

    private fun crearCartaIdTrec(idTrec: String){

        var tempIdTrec = idTrec.toLowerCase()

        val idTrecEnDecimal = Utils.trecToDecimal(tempIdTrec)

        // Comprobamos que el id tenga {1,2} digitos
        if (tempIdTrec.length > 2){
            throw InstantiationError("El id proporcionado no es valido")
        }

        // Comprobamos que la carta este dentro de los limites
        if(idTrecEnDecimal < LB || idTrecEnDecimal > UB){
            throw InstantiationError("El id proporcionado no es valido")
        }

        // Si el id proporcionado tiene un solo digito, añadiremos un 0 al comienzo
        if (tempIdTrec.length == 1){
            tempIdTrec = "0$tempIdTrec"
        }

        val digitoPalo = tempIdTrec[0]
        val digitoNumCarta = tempIdTrec[1]

        // Comprobamos si la carta tiene un palo no valido (aquel con letra 'c')
        if(digitoPalo == 'c'){
            this.palo = null
            this.numeroCarta = Utils.cambiarDeBase(digitoNumCarta.toString(), 13).toInt() + 2
            this.idTrec = idTrec
            this.idTrecEnDec = Utils.trecToDecimal(idTrec)
            return
        }


        // Creamos la carta
        this.palo = Palo.getPaloConId(Utils.cambiarDeBase(digitoPalo.toString(), 13).toInt())!!
        this.numeroCarta = Utils.cambiarDeBase(digitoNumCarta.toString(), 13).toInt() + 2
        this.idTrec = idTrec
        this.idTrecEnDec = Utils.trecToDecimal(idTrec)
    }




    override fun toString(): String {

        if(palo != null) {

            var num: String = numeroCarta.toString()

            when {

                numeroCarta == 11 -> { num = "J"}
                numeroCarta == 12 -> { num = "Q"}
                numeroCarta == 13 -> { num = "K"}
                numeroCarta == 14 -> { num = "AS"}
            }

            return "$num$palo"
        }

        return "Carta no valida"
    }

    override fun compareTo(other: Carta): Int {
        val i = numeroCarta.compareTo(other.numeroCarta)
        if (i != 0) return i

        val tengoPalos = this.palo != null
        val otroTienePalos = other.palo != null

        when {
            !tengoPalos && !otroTienePalos -> { return 0 }
            tengoPalos && !otroTienePalos -> { return 1 }
            !tengoPalos && otroTienePalos -> { return -1 }
            tengoPalos && otroTienePalos -> { return palo!!.rango.sum().compareTo(other.palo!!.rango.sum())}
        }

        return 0
    }
}