import junit.framework.TestCase
import modelo.Carta
import modelo.Palo
import org.junit.Test

class TestsCartas {

    @Test
    fun la_carta_se_crea_usando_como_constructor_el_palo_y_el_numero_de_la_carta(){

        // Creamos la carta con el metodo normal (especificando el numero de la carta y el palo)
        val carta1 = Carta(4, Palo.Corazones)
        val coincide1 = carta1.idTrec.equals("02")

        val carta2 = Carta(14, Palo.Rombos)
        val coincide2 = carta2.idTrec.equals("2c")

        TestCase.assertEquals(true, coincide1 && coincide2)
    }

    @Test
    fun la_carta_se_crea_usando_como_constructor_el_id_en_base_13(){

        val coincidentes = mutableListOf<Boolean>()

        // Id de la menor carta posible
        val carta1 = Carta("00")
        coincidentes.add(carta1.palo == Palo.Corazones && carta1.numeroCarta == 2)

        // Id de la mayor carta posible (el palo no es valido)
        val carta2 = Carta("cc")
        coincidentes.add(carta2.palo == null && carta2.numeroCarta == 14)

        val carta3 = Carta("ab")
        coincidentes.add(carta3.palo == Palo.Treboles && carta3.numeroCarta == 13)

        val carta4 = Carta("c")
        coincidentes.add(carta4.palo == Palo.Corazones && carta4.numeroCarta == 14)

        // Contamos cuantos errores ha habido
        TestCase.assertEquals(0, coincidentes.filterNot { it }.count())
    }

    /*@Test
    fun la_carta_se_crea_usando_como_constructor_un_hexadecimal(){

        // Creamos la carta pasandole el valor hexadecimal
        val carta1 = Carta(2)
        val coincide1 = carta1.numeroCarta == 4 && carta1.palo == Palo.Corazones

        val carta2 = Carta(28)
        val coincide2 = carta2.numeroCarta == 14 && carta2.palo == Palo.Picas



        TestCase.assertEquals(true, coincide1 &&  coincide2)
    }*/
}