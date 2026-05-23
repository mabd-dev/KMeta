package kmeta.loggable

import com.mabd.kmeta.loggable.Loggable

sealed interface Shape {
    @Loggable
    interface Polygon : Shape {
        fun calculateArea(): Double
    }
}

sealed interface Interface1 {
    sealed interface Interface2 : Interface1 {
        @Loggable
        interface Test : Interface2 {
            fun doSomething()
        }
    }
}

fun main() {
    // test creating loggable impl for a sealed interface subtype
}
