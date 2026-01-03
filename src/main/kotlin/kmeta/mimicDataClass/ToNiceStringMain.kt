package kmeta.mimicDataClass

import com.mabd.kmeta.mimicDataClass.toStringProcessor.ToNiceString

@ToNiceString
class Post(
    val name: String,
    val likes: Int,
    val comments: List<String>,
    val user: User,
)

data class Post2(
    val name: String,
    val likes: Int,
    val comments: List<String>,
    val user: User,
)

fun main() {
    val user = User(10, "someone")

    val post = Post("something", 10, listOf("a", "b"), user)
    val post2 = Post2("something", 10, listOf("a", "b"), user)

    println(post.toNiceString())
    println(post2.toString())
}
