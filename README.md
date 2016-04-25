# Quasar Skynet Benchmark

See [here](https://github.com/atemerev/skynet).

## Getting started

Optionally adjust the JVM settings at the end of `build.gradle` and the buffer size constant at the beginning of `Skynet.java`. Then:

```
./gradlew run
```

Or, to perform GC before every execution:

```
./gradle run -PappArgs="['gc']"
```
