package modelo

import jdk.jshell.execution.Util
import java.lang.Error
import java.math.BigInteger


class Mano {

    var cartas = mutableListOf<Carta>().toList()
    var idTrezal = ""
    var idTrezalEnDecimal: Long = Long.MIN_VALUE

    var aptitud: Double = Double.NaN


    companion object {

        // Resultado de convertir "00-00-00-00-00" "trezal" en decimal
        val LB: Long = BigInteger("0000000000",13).toLong()     // 0

        // Resultado de convertir "cc-cc-cc-cc-cc" "trezal" en decimal
        val UB: Long = BigInteger("cccccccccc",13).toLong()     // 137858491848

        val PEOR_APTITUD: Double = 23.0
        val MEJOR_APTITUD: Double = 0.0

        fun generarBarajaAleatoria(palo: Palo? = null): List<Carta>{

            val cartas = hashSetOf<Carta>()
            IntRange(0,4).forEach { cartas.add(Carta.generarCartaAleatoria(palo, cartas)) }

            return cartas.toList()
        }
    }



    constructor(cartas: List<Carta> = generarBarajaAleatoria()){
        construirConCartas(cartas)
    }

    constructor(idTrezalEnDecimal: Long){
        construirConId(idTrezalEnDecimal)
    }



    private fun construirConCartas(cartas: List<Carta>){

        // NO tenemos el numero de cartas necesarias para construir la mano
        if (cartas.size != 5){
            throw InstantiationError("Se necesitan 5 cartas para construir una mano")
        }

        // Guardamos las cartas
        terminarCreacionMano(cartas)
    }

    private fun construirConId(idTrezalEnDecimal: Long){

        var tempIdTrezal = Utils.cambiarDeBaseString(idTrezalEnDecimal, 13)

        if (idTrezalEnDecimal < LB || idTrezalEnDecimal > UB) {
            this.cartas = mutableListOf()
            this.aptitud = calcularAptitudV3()
            this.idTrezal = tempIdTrezal
            this.idTrezalEnDecimal = idTrezalEnDecimal
            return
        }

        /*if (idTrezalEnDecimal < LB){
            tempIdTrezal = Utils.cambiarDeBaseString(LB, 13)
        }
        if (idTrezalEnDecimal > UB) {
            tempIdTrezal = Utils.cambiarDeBaseString(UB, 13)
        }*/


        // Comprobamos que todos los digitos del id sean validos en la base 13
        if (tempIdTrezal.filterNot { it in Constantes.DIGITOS_BASE_13 }.count() > 0){
            throw InstantiationError("Hay digitos no permitidos en el id pasado por parametros")
        }

        // Añadimos los digitos que faltan hasta crear un id con 10 digitos
        if (tempIdTrezal.length < 10){
            var ceros = IntRange(1,10-tempIdTrezal.length).map { "0" }.reduce { final, s -> final + s }
            tempIdTrezal = ceros + tempIdTrezal
        }

        var pares = tempIdTrezal.chunked(2)
        var tempCartas = pares.map { Carta(it) }
        terminarCreacionMano(tempCartas)
    }


    private fun terminarCreacionMano(cartas: List<Carta>){
        this.cartas = cartas
        this.aptitud = PEOR_APTITUD

        // Si hemos pasado una lista de cartas, calcularemos la aptitud de estas y guardaremos el id
        // tanto en trezal como en decimal
        this.idTrezal = ""
        this.idTrezalEnDecimal = -1L
        if (cartas.isNotEmpty()){
            var tempIdCarta = ""
            for (carta in cartas){
                tempIdCarta += carta.idTrec
            }
            val tempIdCartaDecimal = Utils.cambiarDeBase(tempIdCarta, 13)
            this.idTrezal = tempIdCarta
            this.idTrezalEnDecimal = tempIdCartaDecimal
            this.aptitud = calcularAptitudV3()
        }


    }



    /**
     * Esta funcion comprueba que el listadoa de cartas del individuo contenga las 5 cartas que forman la escalera
     * real, siendo todas del mismo palo.
     * Usando esta funcion, los mejores individuos seran aquellos que tengan un valor mas cercano a 0, en
     * cambio aquellos individuos que tengan un valor mas cercano a 23, seran los peores.
     * Los valores de salida perteneceran al rango [0-23].
     */
    private fun calcularAptitudV3(): Double {

        var fitness = 0

        if(idTrezalEnDecimal < LB || idTrezalEnDecimal > UB){
            return PEOR_APTITUD
        }

        // Si hay varias cartas iguales se penalizara con la peor aptitud posible
        if (cartas.groupBy { it.idTrec }.entries.filter { it.value.size > 1 }.count() > 0){
            return PEOR_APTITUD
        }

        // Si hay alguna carta con palo invalido penalizaremos con la peor aptitud
        if (cartas.filter { it.palo == null }.count() > 0){
            return PEOR_APTITUD
        }

        val necesarias = Constantes.OBJETIVO.toMutableList()

        // Sumamos 1 al error por cada numero que nos falte para completar la escalera real
        for (i  in 0 until cartas.size){
            var num = cartas[i].numeroCarta

            // Comprobamos si la carta esta en la lista de necesarios, en caso de estarlo eliminaremos
            // de la lista el numero ya comprobado para evitar duplicidades
            if (num in necesarias){
                necesarias.remove(num)
            }
            else {
                fitness++
            }
        }

        // Sumamos 10 al error por cada carta que sea diferente del palo mayor
        val grupos = cartas.groupBy { it.palo }
        val cartasDelGrupoMayor = grupos.entries.maxBy { it.value.size }!!.value.size
        fitness += (5 - cartasDelGrupoMayor) * 10

        // Convertimos el error para que sea un numero del 0 al 23
        if (fitness >= 10){
            val base = fitness.toString()[0].toString().toInt() * 6
            val sumatorio = base + fitness.toString()[1].toString().toInt()

            fitness = sumatorio
        }

        return fitness.toDouble()
    }

    fun clip(min: Long = LB, max: Long = UB) {

        if (idTrezalEnDecimal < min){
            construirConId(LB)
        }

        if(idTrezalEnDecimal > max){
            construirConId(UB)
        }
    }

    override fun toString(): String {
        return cartas.toString()
    }
}

fun List<Mano>.media(): Double {
    return (this.map { it.idTrezalEnDecimal }.sumByDouble { it.toDouble() } / this.size)
}