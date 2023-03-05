<img src="https://sun9-80.userapi.com/impg/JZaZj7BfrN33x8NtB1bH_WBFCyIlgkXwswMaXA/OWtM8sdRiCo.jpg?size=2000x40&quality=96&sign=633ed80d7c13defcddec353e11f79d5b&type=album" />
<h1 align="center">UMapper</h1>
<div align="center">A convenient mapper for automatically transferring constructor parameters from one Data Class to a similar one or with similar fields</div>

## Using in your projects

- Make sure your classes should be data classes
- There should be no nested classes and lists (in the future it will be possible to use)
- The fields passed must not be null if the other class field is Non-Nullable

### Example:
We have a "Person First" data class from which we want to transfer data to another data class
```kotlin
data class PersonFirst(
    val name: String,
    val lastname: String,
    val password: Int,
    val isAdmin: Boolean
)
```
The second class, for example, will not have the `password` field
```kotlin
data class PersonSecond(
    val name: String,
    val password: Int,
    val isAdmin: Boolean
)
```
PersonFirst must have data, for the sake of the test they will be like this:
```kotlin
val data = PersonFirst(
    name = "Ivan",
    lastname = "Ivanov",
    password = 123456,
    isAdmin = true
)
```

And let's do data mapping in our second class

```kotlin
val result: PersonSecond = data.map()
```
or:
```kotlin
val result = data.map<PersonSecond>()
```

After mapping, we get this result:
```kotlin
PersonSecond("Ivan", 123456, true)
```

## Installation in your project

### Gradle
Add dependencies:
```kotlin
dependencies {
    implementation 'com.github.espmisti:umapper:0.1.0'
}
```
Add it in your root build.gradle at the end of repositories:
```kotlin
allprojects {
  repositories {
      maven { url 'https://jitpack.io' }
    }
}
```

### Maven
Add the JitPack repository to your build file
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
    
Add the dependency
```xml
	<dependency>
	    <groupId>com.github.espmisti</groupId>
	    <artifactId>umapper</artifactId>
	    <version>0.1.0</version>
	</dependency>    
```

<img src="https://sun9-80.userapi.com/impg/JZaZj7BfrN33x8NtB1bH_WBFCyIlgkXwswMaXA/OWtM8sdRiCo.jpg?size=2000x40&quality=96&sign=633ed80d7c13defcddec353e11f79d5b&type=album" />
