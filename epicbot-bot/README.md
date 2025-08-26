# EpicBot OSRS Script Starter

This is a minimal Gradle Java project with a basic EpicBot script using the EpicBot API.

## Prerequisites
- JDK 8+ installed (java -version should show 1.8 or newer)
- EpicBot API JAR file

## Setup
1. Place the EpicBot API JAR at:
   - libs/epicbot-api.jar
2. Build the project:
   - gradle build (or ./gradlew build if you add a wrapper)

The compiled jar will be at build/libs/epicbot-bot-1.0.0.jar.

## Load in EpicBot
- Open the EpicBot client and log in.
- Use the Local Scripts feature to add the built JAR, or place the JAR in your EpicBot scripts folder.
- Look for the script named "MyFirstBot" and start it on OSRS.

## Script Entry Point
The example script extends LoopScript and uses @ScriptManifest with GameType.OS. Adjust logic in loop() to perform actions (walking, interacting, banking, etc.).

## Notes
- The build will fail until libs/epicbot-api.jar is present.
- API packages are from the Javadoc (com.epicbot.api.shared.*).