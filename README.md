# Immortality

A Fabric mod that adds elixirs, weapons, a redstone-powered nullifier, and Viltrumite powers activated by a chestplate.

## Features

- **Viltrumite Chestplate** grants flight, Strength II, Resistance I, Regeneration II, Speed II, Jump Boost II, and fall-damage immunity while worn. Flight speed has five server-controlled steps: hover, cruise, fast, supersonic, and redline.
- Press **R** to step up a flight speed tier; hold **sneak** and press **R** to step down. Double-tap jump to take off. High speed leaves a particle wake; crossing into supersonic speed triggers a sonic boom.
- At **redline**, hold sprint while flying to smash a narrow path through ordinary solid blocks. The charge does not break unbreakable blocks or block entities such as chests, machines, and spawners; cleared blocks do not drop items. Removing the chestplate disables flight, resets its speed, and lets the timed buffs expire quickly.
- **Elixir of Immortality** makes the drinker survive otherwise-fatal damage. Instead of being healed, the player is left at half a heart.
- Each prevented death shows the immortal player a private golden message and applies **Slowness** and **Mining Fatigue** for three minutes. Penalties grow stronger with repeated prevented deaths, up to level III.
- Falling into the void applies the maximum penalty and returns the player to their respawn location.
- **Arrow of Mortality** behaves like a normal arrow and deals normal arrow damage. It can be fired from bows and crossbows or used for a melee hit. If its damage would kill an immortal player, the player dies normally without losing immortality.
- **Immortal's Bane** is an enchanted-glint golden sword with diamond sword damage and durability. Its lethal hit can kill an immortal player without removing immortality.
- **Immortality Nullifier Conduit** is activated by redstone. It emits a red particle ring with a 10-block radius; lethal damage of any kind can kill an immortal player inside the field, while their immortality remains enabled.
- A real death caused by Arrow of Mortality, Immortal's Bane, or the nullifier clears the accumulated Slowness and Mining Fatigue penalties before respawning.
- **Elixir of Mortality** removes immortality and clears the active penalty.
- Drinking either elixir plays a distinct sound, shows a private color-coded chat message, and creates a brief shell of colored particles around the player's body.
- Immortality and penalty data persist through death and relogging.

The elixirs, chestplate, weapons, and nullifier are available in the **Immortality** tab in the Creative inventory. Items currently have no crafting recipes.

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
