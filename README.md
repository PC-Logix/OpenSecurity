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

Versioning uses `2.0.0` as the release line. A tag such as `v2.0.0` produces
`2.0.0`, CI builds produce `2.0.0-dev.<build number>`, and local builds produce
`2.0.0-dev.local`. The build number can also be supplied with
`-Pbuild_number=<number>`.

### Configuration and custom alarm sounds

OpenSecurity writes its shared settings to `config/opensecurity-common.toml` and
client-only settings to `config/opensecurity-client.toml`.

To add an alarm sound, place a lowercase Ogg Vorbis file such as `evacuation.ogg`
in this directory on the server:

```
mods/OpenSecurity/assets/opensecurity/sounds/alarms
```

Then add its name (without `.ogg`) to the `customAlarms` array in the `general`
section of `opensecurity-common.toml`. Keep `klaxon1` and `klaxon2` in that array
if the bundled alarms should remain available.

With `streamCustomAlarms = true` (the default), OpenSecurity starts a small,
read-only HTTP server only when at least one external custom alarm exists. It
serves allowlisted OGG files on `alarmStreamPort` (default `8765`) with a random
per-start access token. Modded clients receive a SHA-256 manifest when they log
in, download and verify the files into `config/opensecurity/alarm-cache`, reload
the sound pack, and play them through the normal positional alarm system. The
client no longer needs a manually copied OGG.

The port must be reachable anywhere the Minecraft port is reachable. Direct
connections automatically reuse the Minecraft server hostname. Behind NAT or a
reverse proxy, set `alarmStreamPublicUrl` to the externally reachable URL ending
in `/opensecurity`, such as `https://minecraft.example.com/opensecurity`. Restart
the server after changing the array, files, bind address, port, or public URL.
The alarm component's `listSounds()` method reports only configured names whose
files are available on the server.

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
