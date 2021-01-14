import com.angcyo.library.ex.count
import com.angcyo.library.ex.decode
import com.angcyo.library.ex.encode
import com.angcyo.library.ex.patternList
import com.angcyo.selenium.parse.ValueParse
import com.angcyo.selenium.parse.args
import org.junit.jupiter.api.Test

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/29
 */
class ExampleUnitTest {

    /**测试正则表达式*/
    @Test
    fun testRegex() {
        val text = "l:>0.1 t:100 w:-20.1width:>=99width:abc99 h:300"
        val regex = "(?<=w:|width:)(?:[><=]*)([-]?[\\d.]*\\d+)".toRegex()
//        val regex = "(?<=w:|width:)([><=]*)(?=[-]?[\\d.]*\\d+)".toRegex()
        //val regex = "([><=]*)(?=\\d+)".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))

        /*val text = "1~-1"
        val regex = "[-]?\\d+".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        println(text.havePartition("~|-"))    */

        /*val text = "input:$1~:$-1"
        val regex = "\\$[-]?\\d+".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        println(text.havePartition("~|-"))

        println(text.subStart(":"))
        println(text.subEnd(":"))*/

        val valueParse = ValueParse()
        //println(valueParse.parseExpression(text, "w:|width:", 100f))
        println(valueParse.parseExpression(text, "", 1f))
    }

    /**测试正则表达式匹配*/
    @Test
    fun testMatch() {
        /* val text = "attr[xxx]"
         val regex = "(?<=\\[).+(?=\\])".toRegex()
         println(text.patternList(regex.toPattern()))*/

        /*val text = "$[xxx 10ab表]"
        val regex = "(?<=\\$\\[).+(?=\\])".toRegex()
        println(text.patternList(regex.toPattern()))*/

        /*val text = "angcyo:xxx type:10ab表 arg:xxx"
        val regex = "(?<=type:)(\\S+)".toRegex()
        println(text.patternList(regex.toPattern()))*/

        /*val text = "input:$[abc] clear:true "
        val regex = "(\\S+)(?=:)".toRegex()
        val regex2 = "(?<=:)(\\S*)".toRegex()
        println(text.patternList(regex.toPattern()))
        println(text.patternList(regex2.toPattern()))
        println(text.toKeyValue())*/

        val text = "[enable:100 -120 200 2.20]"
        val regex = "(\\d+)".toRegex()
        println(text.patternList(regex.toPattern()))
    }

    @Test
    fun testString() {
        val text = "code:$[ke+y+] k:key type:30400 \b last line"
        val regex = "(?<=code:)(\\S+)".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        text.args { index, arg ->
            println("$index->$arg")
        }
        println(text.encode())
        println("a:last+line b%3Alast+line".decode())
        println(" a:last+line b%3Alast+line ".count(' '))
    }

    @Test
    fun testExpression() {
        //val text = "l:>0.1 t:100 w:-20.1 h:%3.30<=2 t:1x2"
        val text = "l:>0.1 t:100 w:-20.1 h:>3.30=2 t:1x2"

        //第一步: 获取关键文本
        val key = ":"
        val r1 = "(?<=$key)(\\S+)".toRegex()
        println(text.patternList(r1.toPattern()))

        text.patternList(r1.toPattern()).forEach { keyText ->
            //第二步: 获取判断表达式 >=4 %4
            val r2 = "([><=%]*[-]?[\\d.]*\\d+)".toRegex()
            println(keyText.patternList(r2.toPattern()))

            keyText.patternList(r2.toPattern()).forEach { expText ->
                print("${expText} 解析-> ")

                //第三步: 获取比较符
                val r3 = "([><=%]*)".toRegex()
                print(expText.patternList(r3.toPattern()))

                //第四步: 获取比较值
                val r4 = "([-]?[\\d.]*\\d+)".toRegex()
                print(expText.patternList(r4.toPattern()))

                println()
            }
        }


        /*val left = "(?<=h:)[><=%]*([-]?[\\d.]*\\d+)".toRegex()
        val exp = "(?<=h:)([><=%]*)(?=[-]?[\\d.]*\\d+)".toRegex()
        println(text.patternList(left.toPattern()))
        println(text.patternList(exp.toPattern()))*/
    }
}