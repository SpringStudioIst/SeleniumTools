import com.angcyo.library.ex.patternList
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

        val text = "1~-1"
        val regex = "[-]?\\d+".toRegex()
        println(text.contains(regex))
        println(text.patternList(regex.toPattern()))
        println(text.havePartition("~|-"))
    }

    /**测试正则表达式匹配*/
    @Test
    fun testMatch() {
        println("...")
    }
}