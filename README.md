
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
- [Installation](#-installation)
- [Annotations](#annotations)
    - [@Loggable](#1-loggable)
    - [@Copy](#2-copy)
    - [@ToNiceString](#3-tonicestring)
- [Usage Examples](#-usage-examples)
- [Limitations](#-limitations)
- [Learning Resources](#-learning-resources)
- [Contribution](#-contributing--ideas)

***

## 📦 **Installation**

KMeta is available via [JitPack](https://jitpack.io). Follow these steps to add it to your project:

### Step 1: Add JitPack Repository

Add the JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```


### Step 2: Apply KSP Plugin

In your module's `build.gradle.kts`, apply the KSP plugin:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}
```

### Step 3: Add Dependencies

Add KMeta to your dependencies:

```kotlin
dependencies {
    // KMeta processor
    implementation("com.github.mabd-dev:KMeta:@latest")
    ksp("com.github.mabd-dev:KMeta:@latest")
}
```

### Step 4: Configure Source Sets

Tell Kotlin where to find the generated code:

```kotlin
kotlin {
    sourceSets.main {
        kotlin.srcDirs("build/generated/ksp/main/kotlin")
    }
}
```

### Complete Example

Here's a complete `build.gradle.kts` example:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.M-Abd-Elmawla:KMeta:@latest")
    ksp("com.github.M-Abd-Elmawla:KMeta:@latest")
}

kotlin {
    sourceSets.main {
        kotlin.srcDirs("build/generated/ksp/main/kotlin")
    }
}
```

After syncing Gradle, you're ready to use KMeta annotations!

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
public class ApiServiceLoggerImpl(
    private val `delegate`: ApiService,
): ApiService {
    
    override fun fetchUserNames(): List<String> {
        val result = delegate.fetchUserNames()
        println("ApiServiceLoggerImpl: fetchUserNames()->$result")
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

## 💡 **Usage Examples**

### Using @Loggable in Practice

The `@Loggable` annotation generates a decorator class that wraps your interface implementation:

```kotlin
// 1. Define your interface with @Loggable
@Loggable(tag = "API")
interface ApiService {
    suspend fun login(username: String, password: String): User
    fun getProfile(userId: Int): Profile
}

// 2. Create your actual implementation
class RealApiService : ApiService {
    override suspend fun login(username: String, password: String): User {
        // actual implementation
        return User(username)
    }

    override fun getProfile(userId: Int): Profile {
        // actual implementation
        return Profile(userId, "John")
    }
}

// 3. Build your project - KSP generates ApiServiceLoggerImpl

// 4. Use the generated logger wrapper
fun main() {
    val realService = RealApiService()
    val loggedService = ApiServiceLoggerImpl(realService)

    // All calls are now logged automatically
    loggedService.login("user123", "pass")
    // Output: API: login(username=user123, password=pass)->User(user123)

    loggedService.getProfile(42)
    // Output: API: getProfile(userId=42)->Profile(42, John)
}
```

**Common patterns:**
- **Dependency Injection**: Inject the logged wrapper instead of the real implementation
- **Testing**: Use `@NoLog` on sensitive methods during production
- **Custom tags**: Use different tags for different modules (`@Loggable(tag = "Network")`, `@Loggable(tag = "DB")`)

### Using @Copy and @ToNiceString

These annotations work great together to make regular classes behave like data classes:

```kotlin
@Copy
@ToNiceString
class User(
    val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean = true
)

fun main() {
    val user = User(1, "Alice", "alice@example.com")

    // Use the generated toNiceString() function
    println(user.toNiceString())
    // Output: User(id=1, name=Alice, email=alice@example.com, isActive=true)

    // Use the generated copy() function
    val inactiveUser = user.copy(isActive = false)
    println(inactiveUser.toNiceString())
    // Output: User(id=1, name=Alice, email=alice@example.com, isActive=false)

    // Only change what you need
    val renamedUser = user.copy(name = "Alice Smith")
}
```

**Common patterns:**
- **Immutable updates**: Use `copy()` for functional programming patterns
- **Debugging**: Use `toNiceString()` for better logging and debugging
- **Value objects**: Use both annotations for domain objects that shouldn't be data classes

### Combining Multiple Annotations

You can mix and match annotations based on your needs:

```kotlin
@Loggable
interface UserRepository {
    fun save(user: User): User
    fun findById(id: Int): User?
}

@Copy
@ToNiceString
class User(val id: Int, val name: String)

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<Int, User>()

    override fun save(user: User): User {
        users[user.id] = user
        return user
    }

    override fun findById(id: Int): User? = users[id]
}

fun main() {
    val repo = UserRepositoryLoggerImpl(InMemoryUserRepository())

    val user = User(1, "Bob")
    repo.save(user)
    // Output: UserRepositoryLoggerImpl: save(user=User(id=1, name=Bob))->User(id=1, name=Bob)

    val updated = user.copy(name = "Robert")
    repo.save(updated)
    // Output: UserRepositoryLoggerImpl: save(user=User(id=1, name=Robert))->User(id=1, name=Robert)
}
```

***

## ⚠️ **Limitations**

### @Loggable
- **Only works with interfaces**: The annotation must be applied to an interface, not a class or abstract class
- **No private interfaces**: Interfaces annotated with `@Loggable` cannot be private
- **Logging format is fixed**: Currently uses `println()` with a predefined format (future versions may support custom loggers)

### @Copy
- **Only works with regular classes**: Cannot be applied to `data class`, `enum class`, `sealed class`, or interfaces
- **No support for default parameter values**: Classes with default constructor parameters are not yet supported
- **Requires primary constructor**: The class must have a primary constructor with all properties defined
- **No private classes**: Classes annotated with `@Copy` cannot be private

### @ToNiceString
- **Only works with regular classes**: Cannot be applied to `data class`, `enum class`, `sealed class`, or interfaces
- **Requires primary constructor**: The class must have a primary constructor with properties
- **No private classes**: Classes annotated with `@ToNiceString` cannot be private
- **String format is fixed**: Currently generates a data-class-like format (future versions may support customization)

### General Limitations
- **KSP version compatibility**: Make sure your KSP version matches your Kotlin version
- **Build required**: You must build the project after adding/modifying annotations for code generation to occur
- **IDE support**: Your IDE may show errors until you build the project for the first time

***

## 📚 **Learning Resources**

- [KSP Official Docs](https://kotlinlang.org/docs/ksp-overview.html#symbolprocessorprovider-the-entry-point)
- [KotlinPoet](https://square.github.io/kotlinpoet/)

---

## 🤝 **Contributing / Ideas**

**issues and PRs for interesting KSP patterns are welcome**.
