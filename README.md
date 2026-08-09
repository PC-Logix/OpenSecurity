OpenSecurity
============
[![Wiki](http://img.shields.io/badge/wiki--blue.svg)](https://github.com/PC-Logix/OpenSecurity/wiki)
[![Discord](http://img.shields.io/discord/125649403162656768.svg?label=discord&style=popout)](https://discord.gg/bYqKv7h)
[![curseForge Project](http://cf.way2muchnoise.eu/versions/opensecurity_latest.svg)](https://minecraft.curseforge.com/projects/opensecurity)
[![Download](http://cf.way2muchnoise.eu/full_231687_downloads.svg)](https://minecraft.curseforge.com/projects/opensecurity/files)

Security addon for OpenComputers

## Minecraft 1.21.1 port

The `1.21.1` branch is an in-progress NeoForge port for the sibling OpenComputers
1.21.1 port. The modern source set includes the Alarm, Biometric Reader, Card
Writer, Data Block, Door Controller, Entity Detector, Mag Readers, RFID Reader,
RFID and Mag Cards, all Secure Door variants, Keypad, Security Terminal, Roll
Door, Energy Turret and upgrades, and NanoFog Terminal. Their OpenComputers
components, recipes, models, sounds, and survival drops are included. The

Requirements:

* Java 21
* NeoForge 21.1.233
* network access to the OpenComputers and ScalableCatsForce Maven repositories

Run `gradlew build` in this repository. Use `gradlew runClient` for
visual/integration testing and `gradlew runServer` for a dedicated-server smoke
test.

### IntelliJ IDEA

Open the repository's `settings.gradle` as a Gradle project and select a Java
21 SDK. Shared Gradle run configurations are included for building, launching
the Minecraft client or dedicated server, running GameTests, and generating
data. IntelliJ's local `.idea` workspace files and generated `.iml`/`.ipr`/`.iws`
files remain ignored.


## Credits

Flawedspirit for the Block Textures (Serisouly the old ones suuuuuucked.)

gamax92 for the Mag and RFID Card textures, and helping me deal with stupid code.

Kodos for the Mods Name and some Textures (DataBlock side, Sine panel front)

AterIgnis For improvements to the Energy Turret and the Keypad
