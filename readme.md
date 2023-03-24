# OpenAI GPT Chat Completion client Spring Boot starter library

This library provides Spring beans and methods to query OpenAI GPT Chat Completion API.
It can stream the results as they are received from the API so that you
get them as soon as possible but also provides you with the complete response
at the end.

Autoconfiguration is provided for the following beans:

- ```OpenaiService``` - the main service bean that provides the query method, see the example below.
- ```OpenaiClient``` - the client bean that provides the low level API calls if you need them somehow.

## Including the library in your project

In your gradle project add the following jitpack.io dependency:

```
com.github.propush:openaigptclient-spring-boot-starter:x.y.z
```

For example in your build.gradle.kts:

```kotlin
// ...
repositories {
// ...
    maven("https://jitpack.io")
}

dependencies {
// ...
    implementation("com.github.propush:openaigptclient-spring-boot-starter:x.y.z")
}

```

## Usage

```kotlin
@Autowired
private lateinit var openaiService: OpenaiService
///
val fullCompletionText = openaiService.query("Hello, ChatGPT", 10) { rs ->
    logger.debug { "Chunk received: $rs" }
}
logger.debug { "Full completion text: $fullCompletionText" }
```

## Building from source

```
./gradlew build
```

## Testing

Integration tests to be run as:

```
./gradlew test -Dspring.profiles.active=inttest -DOPENAI_API_KEY=<YOUR_OPENAI_API_KEY> 
```

The rest of the tests to be run as usual:

```
./gradlew test
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
