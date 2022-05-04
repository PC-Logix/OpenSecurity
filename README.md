OpenSecurity
============
[![Wiki](http://img.shields.io/badge/wiki--blue.svg)](https://github.com/PC-Logix/OpenSecurity/wiki)
[![Discord](http://img.shields.io/discord/125649403162656768.svg?label=discord&style=popout)](https://discord.gg/bYqKv7h)
[![curseForge Project](http://cf.way2muchnoise.eu/versions/opensecurity_latest.svg)](https://minecraft.curseforge.com/projects/opensecurity)
[![Download](http://cf.way2muchnoise.eu/full_231687_downloads.svg)](https://minecraft.curseforge.com/projects/opensecurity/files)

Security addon for OpenComputers

## What's Different?

This fork is a modified version for my own needs. So far, it only has 2 differences:
1. The Mag Reader can now have the automatic lights enabled/disabled (normally when swiped, it blinks green)
2. If automatic lights are disabled on magreader, you can select which lights to enable (0=off,1=red,2=yellow,3=green)
3. Another security door, which is going to have a scp door texture (for my modpack)

## build notes

before building run the gradle task `syncGitWiki` once to sync the Wiki to your project

### how to build
clone the project to your local machine with `git clone https://github.com/PC-Logix/OpenSecurity.git`

setup workspace with `gradlew setupDecompWorkspace` and build with `gradlew build`


## Credits

Flawedspirit for the Block Textures (Serisouly the old ones suuuuuucked.)

gamax92 for the Mag and RFID Card textures, and helping me deal with stupid code.

Kodos for the Mods Name and some Textures (DataBlock side, Sine panel front)

AterIgnis For improvements to the Energy Turret and the Keypad
