
# Rusherhack Web Plugin

This plugin provides a Chromium-based web-browser accessible through the Window API.

## Usage
- Download and install the mod [MCEF](https://modrinth.com/mod/mcef)
- Download the plugin from the Releases tab into your `.minecraft/Rusherhack/Plugins` folder (create the folder if it doesnt exist)
- Edit the launch arguments for the instance of Minecraft you are loading RusherHack from to include the entry
`-Drusherhack.enablePlugins=true` (make sure to set it for jvm and not mc in some launchers)
- Launch your RusherHack instance and open the Windows menu. A new window called Browser should appear

## TODO:
- Persistent cookie storage
- Configurable homepage
- Tabs

## Credits
- [Lokfid](https://github.com/Lokfid) (making basic foundation and idea)
- [Doogie](https://github.com/doogie13) (rewriting it for rusher's window API and fixing most of the bugs)
