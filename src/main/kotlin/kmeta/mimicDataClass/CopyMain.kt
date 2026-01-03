package kmeta.mimicDataClass

import com.mabd.kmeta.mimicDataClass.copyProcessor.Copy
import com.mabd.kmeta.mimicDataClass.toStringProcessor.ToNiceString

@Copy
@ToNiceString
class User(
    val age: Int,
    val name: String,
)

fun main() {
    val user = User(1, "someone")
    val user2 = user.copy(age = 10)

    println("User(name=${user.name}, age=${user.age})")
    println("User(name=${user2.name}, age=${user2.age})")
}
