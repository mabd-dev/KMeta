
<p align="center">
    <img width="460" src="resources/logo.svg" />
</p>


<p align="center">
  <b>Kotlin Metaprogramming & Code Generation Toolkit</b>
</p>

<!-- <p align="center"> -->
<!--   <a href="https://search.maven.org/search?q=g:dev.mabd.kmeta"> -->
<!--     <img src="https://img.shields.io/maven-central/v/dev.mabd.kmeta/kmeta" alt="Maven Central"> -->
<!--   </a> -->
<!--   <a href="https://search.maven.org/artifact/dev.mabd.kmeta/kmeta"> -->
<!--     <img src="https://img.shields.io/badge/version-latest-blue.svg" alt="Latest Version"> -->
<!--   </a> -->
<!-- </p> -->


# Table Of Content:
- [Annotations](#annotations)
    - [@Loggable](#1-loggable)
    - [@Copy](#2-copy)
    - [@ToNiceString](#3-tonicestring)
- [Learning Resources](#-learning-resources)
- [Contribution](#-contributing--ideas)

***

## Annotations

### 1. **@Loggable**
Automatically generate a decorator implementation for any interface annotated with `@Loggable`.

```kotlin
@Loggable // <- add this
interface ApiService {
    fun fetchUserNames(): List<String>
}

// Generated code
public class ApiService2LoggerImpl(
    private val `delegate`: ApiService2,
): ApiService {
    
    override fun fetchUserNames(): List<String> {
        val result = delegate.fetchUserNames()
        println("ApiService2LoggerImpl: fetchUserNames()->$result")
        return result
    }
    
}
```
view full `@Loggable` docs [here](docs/Loggable-README.md)

### 2. **@Copy**

Adds a copy extension function to any regular (non-data) class annotated with @Copy.

```kotlin
@Copy // <- add this
class Person(
    val name: String,
    val age: Int
)

// generated code
fun Person.copy(
    name = this.name,
    age = this.age
): Person = Person(name, age)
```
view full `@Copy` docs [here](docs/Mimic-Data-Class-README.md#copy-processor)


### 3. **@ToNiceString**
Adds a `toNiceString()` extension function to any regular (non-data) class annotated with `@ToNiceString`.

```kotlin
@ToNiceString // <- add this
class Person(
    val name: String,
    val age: Int
)

// generated code
fun Person.toNiceString(): String {
    return "Person(name=$name, age=$age)"
}
```
view full `@ToNiceString` docs [here](docs/Mimic-Data-Class-README.md#copy-processor)

***

## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
