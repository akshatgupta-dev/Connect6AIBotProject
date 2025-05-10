# Quantum Connect starter project

Pre-requisites:

- Java 21

## Building 

In order to build your package properly, implement your desired algorithm and change the following
line in `build.gradle`:

```
'Bot-Class': 'ch.qc.starter.MyBot',
```

Make sure you pass in the fully qualified class name of the bot.

Afterward, you can package your code running the command:

```
./gradlew build 
```

Use the jar in `./build/libs/quantumconnect-starter-1.0-SNAPSHOT.jar` and upload it in the game!
