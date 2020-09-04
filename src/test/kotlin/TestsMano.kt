import junit.framework.TestCase
import modelo.Mano
import org.junit.Test

class TestsMano {

    @Test
    fun la_mano_se_crea_usando_un_id_trezal_en_decimal_valido() {

        val validezManos = mutableListOf<Boolean>()

        // 2 corazones, 2 picas, 2 rombos, 2 treboles, 3 corazones
        val mano1 = Mano(190493083)
        validezManos.add(mano1.manoValida)

        // As de corazones, K de corazones, Q de corazones, J de corazones, 10 de corazones
        val mano2 = Mano(9842150690)
        validezManos.add(mano2.manoValida)

        // Contamos cuantas manos son validas
        TestCase.assertEquals(0, validezManos.filterNot { it }.count())
    }

    @Test
    fun se_crean_manos_invalidas_al_usar_ids_invalidos() {

        val validezManos = mutableListOf<Boolean>()

        // Id menor
        val mano1 = Mano(0)
        validezManos.add(mano1.manoValida)

        // Id mayor
        val mano2 = Mano(137858491848)
        validezManos.add(mano2.manoValida)

        // Id menor que el limite inferior
        val mano3 = Mano(-1)
        validezManos.add(mano3.manoValida)

        // Id menor que el limite superior
        val mano4 = Mano(137858491849)
        validezManos.add(mano4.manoValida)

        // Id de una mano con una carta de un palo invalido
        val mano5 = Mano(190493239)
        validezManos.add(mano5.manoValida)

        // Contamos cuantas manos son validas
        TestCase.assertEquals(0, validezManos.filter { it }.count())
    }
}