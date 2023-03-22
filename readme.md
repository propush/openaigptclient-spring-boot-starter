# OpenAI GPT Chat Completion client Spring library

## Building
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
