# Immortality

A Fabric mod that adds two elixirs for controlling player immortality, with escalating penalties when immortality prevents a death.

## Features

- **Elixir of Immortality** makes the drinker survive otherwise-fatal damage. Instead of being healed, the player is left at half a heart.
- Each prevented death applies **Slowness** and **Mining Fatigue** for three minutes. Penalties grow stronger with repeated prevented deaths, up to level III.
- Falling into the void applies the maximum penalty and returns the player to their respawn location.
- **Arrow of Mortality** behaves like a normal arrow and deals normal arrow damage. If its hit would kill an immortal player, the player dies normally; the arrow does not remove their immortality.
- **Elixir of Mortality** removes immortality and clears the active penalty.
- Drinking either elixir plays a distinct sound and creates a small burst of colored particles around the player's body and legs. A private, color-coded chat message confirms the change.
- Immortality and penalty data persist through death and relogging.

The elixirs and Arrow of Mortality are available in the **Immortality** tab in the Creative inventory. They currently have no crafting recipes.

## Requirements

- Minecraft **1.21.10**
- Fabric Loader **0.17.2 or newer**
- Fabric API
- Java **21 or newer**

## Install

1. Install Fabric Loader for Minecraft 1.21.10 and add Fabric API to your mods.
2. Download the `immortality-jar` artifact from a successful run of the **build** workflow in this repository's GitHub Actions.
3. Put the downloaded mod JAR in your Minecraft `mods` folder.
4. Launch Minecraft with the Fabric profile.

## Build

The repository's GitHub Actions workflow builds the mod on every push. To start a build manually, open **Actions**, select **build**, and choose **Run workflow**. Download the `immortality-jar` artifact from the completed run.
