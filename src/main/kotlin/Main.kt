import modelo.Carta
import modelo.Mano
import modelo.Palo
import modelo.media
import org.apache.commons.math3.special.Gamma
import java.lang.Math.pow
import java.lang.Math.random
import kotlin.math.abs
import kotlin.math.sin

fun main() {

    HHO(100, 100000)

}

fun HHO(numeroAguilas: Int = 50, iteraciones: Int = 200){

    var localizacionConejo: Mano = Mano()
    var energiaConejo: Double = Double.MAX_VALUE        // -Double.MIN_VALUE para problemas de maximizacion

    val lb = Mano.LB    // Valor de la mano mas baja
    val ub = Mano.UB    // Valor de la mano mas alta

    // Creamos la posicion aleatoria de nuestros aguilas (manos aleatorias)
    val aguilas = IntRange(1,numeroAguilas).map { Mano() }.toMutableList()

    val curvaConvergencia = mutableListOf<Double>()

    var mejor = aguilas.map { Pair(it.aptitud, it) }.minBy { it.first }!!
    println("Mejor aptitud inicial: " + mejor.first)
    println("Mejores cartas iniciales: " + mejor.second.cartas)
    println()

    for (i in 0 until iteraciones){

        // DEBUG
        var mejorAptitudDeLaRonda = Double.MAX_VALUE
        var mejoresCartasDeLaRonda = Mano()
        var aptitudInicialDeLaRonda = energiaConejo
        var localizacionInicialDelConejo = localizacionConejo
        // ----------


        // Calculamos la aptitud de cada aguila
        for (posicionAguila in aguilas){

            // Hacemos clip
            posicionAguila.clip()

            val aptitud = posicionAguila.aptitud

            // Actualizamos la localizacion del conejo a la que tiene el aguila con mejor aptitud
            if (aptitud < energiaConejo){
                localizacionConejo = posicionAguila
                energiaConejo = aptitud

                // DEBUG
                if (energiaConejo < mejorAptitudDeLaRonda) {
                    mejorAptitudDeLaRonda = energiaConejo
                    mejoresCartasDeLaRonda = localizacionConejo
                }
            }
        }

        // DEBUG
        if (mejorAptitudDeLaRonda != Double.MAX_VALUE && i != 0){
            println("Ha ocurrido una mejora (ronda ${i+1})")
            println("ANTES ($aptitudInicialDeLaRonda) - $localizacionInicialDelConejo")
            println("DESPUES ($mejorAptitudDeLaRonda) - $mejoresCartasDeLaRonda")
            println()
        }

        // Evitamos seguir iterando si hallo la solucion
        if (energiaConejo == 0.0){
            println("Hallamos la solucion!!!!!")
            break
        }


        // Factor que muestra la energia decreciente del conejo
        val E1 = 2 * (1 - (i / iteraciones))

        // Pasamos a realizar la accion que corresponda (exploracion o explotacion) en base
        // a la energia de escape del conejo y un numero aleatorio
        for (j in aguilas.indices){

            val E0 = 2 * Math.random() - 1      // -1 < E0 < 1
            val energiaDeEscape = E1 * E0       // Energia de escape del conejo


            // --- Exploracion ---
            if (abs(energiaDeEscape) >= 1){

                // Cambiamos la posicion del aguila en base a dos posibles estrategias
                val q = Math.random()

                val indiceAguilaRandom = (Math.random() * numeroAguilas).toInt()
                val posicionAguilaRandom = aguilas.get(indiceAguilaRandom).idTrezalEnDecimal

                var nuevaPos: Long

                if (q < 0.5){

                    val posicionActual = aguilas[j].idTrezalEnDecimal

                    // Nos posamos en un arbol alto (uno aleatorio dentro del rango del grupo)
                    val temp = posicionAguilaRandom - random() * abs(posicionAguilaRandom - 2 * random() * posicionActual)
                    nuevaPos = temp.toLong()
                }

                else{

                    // Nos posamos en base a otros miembros de la familia
                    val temp = (localizacionConejo.idTrezalEnDecimal - aguilas.media()) - random() * Utils.randomEntre(lb,ub)
                    nuevaPos = temp.toLong()
                }

                // Actualizamos la posicion del aguila
                aguilas[j] = Mano(nuevaPos)

                //println("Nueva pos = $nuevaPos - " + aguilas[j] + " - " + aguilas[j].aptitud)
            }

            // --- Explotacion ---
            else if (Math.abs(energiaDeEscape) < 1){


                val r = Math.random()

                // Fase 1: Saltos sorpresa (multiples diveos cortos y rapidos realizados por diferentes aguilas)
                if (r >= 0.5 && abs(energiaDeEscape) < 0.5){  // Asedio fuerte
                    val temp = localizacionConejo.idTrezalEnDecimal - energiaDeEscape * abs(localizacionConejo.idTrezalEnDecimal - aguilas[j].idTrezalEnDecimal)
                    aguilas[j] = Mano(temp.toLong())
                }

                if (r >= 0.5 && abs(energiaDeEscape) >= 0.5){ // Asedio suave
                    val fuerzaDelSalto = 2 * (1 - random()) // Fuerza de salto del conejo aleatorio
                    val temp = (localizacionConejo.idTrezalEnDecimal - aguilas[j].idTrezalEnDecimal) - energiaDeEscape * abs(fuerzaDelSalto * localizacionConejo.idTrezalEnDecimal - aguilas[j].idTrezalEnDecimal)
                    aguilas[j] = Mano(temp.toLong())
                }




                // Fase 2: Se realizan diveos rapidos en equipo (movimiento de salto de rana)
                if (r < 0.5 && abs(energiaDeEscape) >= 0.5){ // Asedio suave
                    // El conejo intenta escapar haciendo movimientos en zig-zag
                    val fuerzaDelSalto = 2 * (1 - random()) // Fuerza de salto del conejo aleatorio

                    val X1 = localizacionConejo.idTrezalEnDecimal - energiaDeEscape * abs(fuerzaDelSalto * localizacionConejo.idTrezalEnDecimal - aguilas[j].idTrezalEnDecimal)
                    val manoX1 = Mano(X1.toLong()).apply { clip() }

                    // Comprobamos si la aptitud de la nueva mano es mejor que la usada para crearle
                    if (manoX1.aptitud < aguilas[j].aptitud){
                        aguilas[j] = manoX1
                    }

                    // Realizan diveos rapidos y cortos alrededor del conejo
                    else {
                        val X2 = localizacionConejo.idTrezalEnDecimal - energiaDeEscape * abs(fuerzaDelSalto * localizacionConejo.idTrezalEnDecimal - aguilas[j].idTrezalEnDecimal) + random() * levy()
                        val manoX2 = Mano(X2.toLong()).apply { clip() }

                        if (manoX2.aptitud < aguilas[j].aptitud){
                            aguilas[j] = manoX2
                        }
                    }
                }

                if (r < 0.5 && abs(energiaDeEscape) < 0.5){ // Asedio fuerte
                    val fuerzaDelSalto = 2 * (1 - random()) // Fuerza de salto del conejo aleatorio

                    val X1 = localizacionConejo.idTrezalEnDecimal - energiaDeEscape * abs(fuerzaDelSalto * localizacionConejo.idTrezalEnDecimal - aguilas.media())
                    val manoX1 = Mano(X1.toLong()).apply { clip() }

                    // Comprobamos si la aptitud de la nueva mano es mejor que la usada para crearle
                    if (manoX1.aptitud < aguilas[j].aptitud){
                        aguilas[j] = manoX1
                    }

                    // Realizan diveos rapidos y cortos alrededor del conejo
                    else {

                        val X2 = localizacionConejo.idTrezalEnDecimal - energiaDeEscape * abs(fuerzaDelSalto * localizacionConejo.idTrezalEnDecimal - aguilas.media()) + random() * levy()
                        val manoX2 = Mano(X2.toLong()).apply { clip() }

                        if (manoX2.aptitud < aguilas[j].aptitud){
                            aguilas[j] = manoX2
                        }
                    }
                }
            }
        }

        // DEBUG
        curvaConvergencia.add(energiaConejo)
    }

    //println("Curva de convergencia: $curvaConvergencia")
    println("Aptitud final: ${energiaConejo}")
    println(localizacionConejo)
}

fun levy(): Double {
    val beta = 1.5

    val potenciaInterior = pow(2.0, ((beta - 1.0) / 2.0))
    val interior = (Gamma.gamma(1+beta) * sin( Math.PI * beta / 2.0 ) / (Gamma.gamma((1.0 + beta) / 2.0) * beta * potenciaInterior))

    val sigma = pow(interior, (1.0 / beta))

    val u = 0.01 * random() * sigma

    val v = random()

    val zz = pow(abs(v), (1.0 / beta))

    val step = u / zz

    return step
}