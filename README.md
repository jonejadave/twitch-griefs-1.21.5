# TwitchGriefs

**TwitchGriefs** is a basic Minecraft mod that lets your Twitch chat mess with your single-player world by spawning mobs through chat commands.

Still super early in development, but it works.


## What It Does

Viewers in your Twitch chat can type commands to spawn hostile mobs near you. That’s pretty much it for now.

Simple. Fun. Annoying.


## Chat Commands

These are the commands your chat can use:

| Command     | What it does            |
|-------------|--------------------------|
| `!creeper`  | Spawns a Creeper         |
| `!zombie`   | Spawns a Zombie          |
| `!skeleton` | Spawns a Skeleton        |
| `!spider`   | Spawns a Spider          |
| `!jockey`   | Spawns a Chicken Jockey  |

All mobs spawn close to the player. Chaos guaranteed.


## Installation for Fabric

- Minecraft Java v1.21.5 w/ Fabric required
- Download the latest [release](https://github.com/jonejadave/twitch-griefs-1.21.5/releases/latest/download/twitch-griefs-1.0.0.zip)
- drag both the config and mods folders into the .minecraft directory
- paste your TwitchOAuth key in the config file. If you dont have a token you can generate one [here](https://twitchtokengenerator.com/)


## How It Works

- Uses [Twitch4J](https://github.com/twitch4j/twitch4j) to connect to Twitch
- Listens for chat messages
- Spawns mobs based on commands

That’s it for now. No fancy features yet.


## Roadmap / Stuff Coming Soon

- More mob types (maybe Ghasts, Blazes, etc

## Contact

discord: f5soren 
