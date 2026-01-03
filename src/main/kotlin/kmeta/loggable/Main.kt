package kmeta.loggable

import com.mabd.kmeta.loggable.Loggable
import com.mabd.kmeta.loggable.NoLog


@Loggable(
    tag = "MyLogTag",
)
interface ApiService<T> {
    var isAuth: Boolean?

    @Deprecated(
        message = "just for testing",
        replaceWith = ReplaceWith("test2(1)"),
    )
    fun testAnnotationsArePreserved()

    @NoLog
    fun testNoLogGenerated(a: Int)

    /**
     * Testing docs on function with params and return
     * @param b something
     * @return some number
     */
    fun testDocsArePreserved(b: Int): Int

    fun <T : Number, R> testGenericsArePossible()

    fun testVararg(
        a: Int,
        vararg f: Float,
    )
}

fun main() {
    val apiService = ApiServiceLoggerImpl(RealApiService())

    println("isAuth=${apiService.isAuth}")
    apiService.isAuth = true

    apiService.testAnnotationsArePreserved()
    apiService.testNoLogGenerated(1)
    apiService.testDocsArePreserved(3)
    apiService.testGenericsArePossible<Int, Int>()
    apiService.testVararg(1, .1f, .2f)
}
