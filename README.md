# Minecraft Immortality Mod

A Fabric mod about immortality, mortality, and the places and weapons that can challenge eternal life.

## Features

### Immortality and elixirs

- **Elixir of Immortality** makes the drinker immortal. Lethal damage leaves them at half a heart instead of restoring their health.
- Immortality grants hidden Strength III, Luck III, Regeneration III, Speed II, Night Vision, and Jump Boost II effects.
- Every prevented death sends the immortal player a private golden message: “You died... or did you?”
- Prevented deaths apply escalating Slowness and Mining Fatigue penalties for three minutes. Falling into the void applies the maximum penalty and returns the player to their respawn location.
- **Elixir of Mortality** removes immortality.
- Drinking either elixir plays a distinct sound, sends a private colored chat message, and surrounds the drinker with a short transformation particle effect.

### Weapons and nullification

- **Arrow of Mortality** behaves like a normal arrow, including normal damage and use with bows and crossbows. It can kill an immortal player when its hit would otherwise be lethal; immortality remains active.
- **Immortal’s Bane** is an enchanted golden sword with diamond-sword damage and durability. It can kill an immortal player without removing immortality.
- **Nullifier Conduit** is a redstone-activated block that creates a red particle ring with an approximately 10-block radius. Immortality is suppressed for immortal players inside the field, allowing lethal damage from any source to kill them. Their immortality remains active after death.

### Elder Oak biome and flowers

- **Elder Oak** is a pale, desaturated woodland with pale grass, birch and oak trees, and two flowers.
- **White Rose** grants mild Regeneration while a player stands on it.
- **Red Devil** causes mild Nausea while a player stands on it.
- The biome’s landmarks are configured to generate at about 20% of chunks.

### Pocket Doors and dimension

- The **Pocket Door** opens into an endless woodland dimension based on Elder Oak.
- Each door is owned by the player who places it. Only that player can use it; breaking it returns a door item to the owner when they are online.

### Enchantment

- **Live by the Sword** is a sword-only enchantment that adds 7 damage and ignites the target. It has a 5% chance to appear as an enchanted book in abandoned mineshaft chests.

The elixirs, weapons, blocks, flowers, and Pocket Door are available in the **Immortality** Creative inventory tab. They currently have no crafting recipes.

## Requirements

- Minecraft **1.21.10**
- Fabric Loader **0.17.2 or newer**
- Fabric API
- Java **21 or newer**

## Install

1. Install Fabric Loader for Minecraft 1.21.10 and add Fabric API to your mods.
2. Download the immortality-jar artifact from a successful run of this repository’s **build** workflow on GitHub Actions.
3. Put the downloaded mod JAR in your Minecraft mods folder.
4. Launch the game with the Fabric profile.

## Build

The GitHub Actions **build** workflow builds the mod on every push. To start a build manually, open **Actions**, select **build**, and choose **Run workflow**. Download the immortality-jar artifact from the completed run.
