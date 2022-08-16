# VoShooter 🔫

A very original top-down arena shooter

Taking inspiration from games like Quake, Counter-Strike and others we bring you a new, never before seen, totally original arena shooter. Fight in an arena against all of your closest friends or enemies, collect items and blow up others in this fast paced shooter. Or if you prefer fighting against a machine, go up against some of the most intelligent bots the world has ever seen.

# Downloads 🚀 
Get the latest game client from the [release section](https://github.com/gekoke/voshooter/releases). Both Windows executables and .jar files are available.

# Building ⚙️

## Server
To run the server:
```sh
./gradlew core:run --args="<SERVER_PORT>"  # Default port is 5001
```

To build a server jar file:
```sh
./gradlew core:dist
```
The jar file should appear in `core/build/libs/VoShooterServer.jar` with no options specified.

To build a Windows executable file:
```sh
./gradlew core:createExe
```
The file should appear in `core/build/launch4j/voshooter-server.exe` with no options specified.

## Client
To run the client:
```sh
./gradlew core:run
```

To build a client jar file:
```sh
./gradlew desktop:dist
```
The jar file should appear in `core/build/libs/VoShooter.jar` with no options specified.

To build a Windows executable file:
```sh
./gradlew desktop:createExe
```
The file should appear in `desktop/build/launch4j/voshooter.exe` with no options specified.

