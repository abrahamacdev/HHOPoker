import com.sun.jdi.IntegerType
import java.math.BigInteger

object Utils {

    fun cambiarDeBase(numero: Long, base: Int): Long{
        return BigInteger(numero.toString(base), base).toLong()
    }

    fun cambiarDeBase(numero: String, base: Int): Long{
        return BigInteger(numero, base).toLong()
    }

    fun cambiarDeBaseString(numero: Long, base: Int): String {
        return numero.toString(base)
    }

    fun trecToDecimal(trezalNum: String): Long {
        var trezNum = trezalNum
        val hstring = "0123456789ABC"
        trezNum = trezNum.toUpperCase()
        var num = 0
        for (element in trezNum) {
            val ch = element
            val n = hstring.indexOf(ch)
            num = 16 * num + n
        }
        return num.toLong()
    }

    fun decToTrezal(decimal: Int): Long {
        return decimal.toString(13).toLong()
    }

    fun decToTrezalString(decimal: Long): String {
        return decimal.toString(13)
    }

    fun randomEntre(min: Int, max: Int): Int{
        return min + (Math.random() * ((max - min) + 1)).toInt()
    }

    fun randomEntre(min: Long, max: Long): Long{
        return min + (Math.random() * ((max - min) + 1)).toLong()
    }
}