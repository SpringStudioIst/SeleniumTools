import com.angcyo.library.ex.patternList
import com.angcyo.library.ex.subEnd
import com.angcyo.library.ex.subStart
import com.angcyo.selenium.parse.havePartition
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
        /*val text = "l:0.1 t:100 w:-20.1width:99 h:300"
        val regex = "(?<=w:|width:)([-]?[\\d.]*\\d+)".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))*/

        /*val text = "1~-1"
        val regex = "[-]?\\d+".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        println(text.havePartition("~|-"))    */

        val text = "input:$1~:$-1"
        val regex = "\\$[-]?\\d+".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        println(text.havePartition("~|-"))

        println(text.subStart(":"))
        println(text.subEnd(":"))
    }

    /**测试正则表达式匹配*/
    @Test
    fun testMatch() {
        /* val text = "attr[xxx]"
         val regex = "(?<=\\[).+(?=\\])".toRegex()
         println(text.patternList(regex.toPattern()))*/

        val text = "$[xxx]"
        val regex = "(?<=\\$\\[).+(?=\\])".toRegex()
        println(text.patternList(regex.toPattern()))
    }
}