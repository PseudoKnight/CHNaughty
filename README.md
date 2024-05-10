# CHNaughty

These functions are using NMS/OBC. They will probably break every MC version change and possibly even more often than that, so you'll need to update the extension when that happens. There's no guarantee that every single Spigot build will be supported in the future, so code appropriately.

## Downloads
[Spigot 1.20.6](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.15.0) (CommandHelper 3.3.5)  
[Spigot 1.20.4](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.14.0) (CommandHelper 3.3.5)  
[Spigot 1.20.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.13.0) (CommandHelper 3.3.5)  
[Spigot 1.20.1](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.12.0) (CommandHelper 3.3.5)  
[Spigot 1.19.4](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.11.0) (CommandHelper 3.3.5)  
[Spigot 1.19.3](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.10.0) (CommandHelper 3.3.5)  
[Spigot 1.19.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.9.1) (CommandHelper 3.3.5)  
[Spigot 1.19.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.8.0) (CommandHelper 3.3.5)  
[Spigot 1.18.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.7.0) (CommandHelper 3.3.5)  
[Spigot 1.18.1](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.6.1) (CommandHelper 3.3.5)  
[Spigot 1.17.1](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.5.1) (CommandHelper 3.3.5)  
[Spigot 1.16.5](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.4.1) (CommandHelper 3.3.4 - 3.3.5)  
[Spigot 1.16.1](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.2.0) (CommandHelper 3.3.4)  
[Spigot 1.15.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.1.0) (CommandHelper 3.3.4)  
[Spigot 1.14.4](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.11.4b) (CommandHelper 3.3.4)  
[Spigot 1.14.4](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.11.2) (CommandHelper 3.3.4 up to build 3776)  
[Spigot 1.12.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.9.0) (CommandHelper 3.3.2)  
[Spigot 1.11.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.4.2) (CommandHelper 3.3.2)  
[Spigot 1.10.2](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.4.1) (CommandHelper 3.3.2)  
[Spigot 1.9.4](https://github.com/PseudoKnight/CHNaughty/releases/tag/v3.2.0) (CommandHelper 3.3.2)  
[Spigot 1.8.8](https://github.com/PseudoKnight/CHNaughty/releases/tag/v2.0.1) (CommandHelper 3.3.2)

## Functions
### open_sign{[player], signLocation, [side], [lines]}
Opens a sign editor for the given sign location.  
The side is optional, and must be FRONT or BACK. (default FRONT)  
Lines must be an array with up to 4 values or null. If not provided, it'll use the existing lines.  
Throws CastException if not a sign block.

### open_book{[player], pages | [player], hand}
Sends a virtual book to a player.  
Accepts an array of pages or the player hand (MAIN_HAND, OFF_HAND) in which an existing book resides.  
All pages must be either raw JSON or strings. If the JSON is not formatted correctly, it will fall back to string output.  
Throws IllegalArgumentException if no written book resides in the given hand.

### relative_teleport([player], locationArray)
Sets the player location relative to where they are on their client. This can be used for smooth teleportation.

### psleep([player], bedLocation, [force])
Sets the player sleeping at the specified bed location.  
Optionally force sleeping even if player normally wouldn't be able to.  
If not forced, it will throw an exception when unsuccessful.  
The following conditions must be met for a player to sleep: the location must be a bed, the player must be near it,
it must not be obstructed, it must be night and there must not be hostile mobs nearby.

### ping([player])
Returns the player's ping to the server. This data is stored on the server,
so the accuracy of the result is dependent on the server's method.

### pswing_hand([player], [hand])
Swing the player's hand in an attack animation. The hand parameter can be either main_hand (default) or off_hand.

### set_parrow_count(count | player, count, [ticks])
Sets the amount of arrows in a player's model.  
Optional number of ticks the arrow count will persist until arrows start despawning again. (default: 20 * (30 - count))

### set_pstinger_count([player], count)
Sets the amount of bee stingers in a player's model.

### ray_trace([player], [location], [range], [raySize])
Returns an array of result data from a ray trace from the player's eye location or the given location.  
Result array contains the following keys:  
'hitblock' is whether or not a block was hit;  
'hitface' is the block face that was hit (or null);  
'block' is the location of the block that was hit (or null);  
'location' contains the location where the ray trace ends;  
'origin' contains the location where the ray trace starts (useful if you don't specify a location);  
'entities' contains an array of hit entities where each array contains a 'location' key and 'uuid' key.

### action_msg([player], message)
Sends a message to the action bar, located right above the player's hotbar.

``` 
if(function_exists('action_msg')) {
  action_msg('PseudoKnight', colorize('&2You picked up an apple.'));
}
```

### tps()
Returns an array of average ticks per second over 5, 10 and 15 minutes. eg. {19.9999999,19.99888567,19.56889299}
