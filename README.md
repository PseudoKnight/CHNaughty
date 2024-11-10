# CHNaughty

These functions are using NMS/OBC. They will probably break every MC version change and possibly even more often than that, so you'll need to update the extension when that happens. There's no guarantee that every single Spigot build will be supported in the future, so code appropriately.

## Releases
Latest releases require CommandHelper 3.3.5.

[CHNaughty 5.0.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v5.0.0) for Paper 1.20.6 - 1.21.3 and Spigot 1.21.2 - 1.21.3  
[CHNaughty 4.16.1](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.16.1) for Paper 1.20.6 - 1.21.3 and Spigot 1.21.0 - 1.21.1  
[CHNaughty 4.15.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.15.0) for Paper 1.20.6 - 1.21.3 and Spigot 1.20.5 - 1.20.6  
[CHNaughty 4.14.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.14.0) for Paper and Spigot 1.20.3 - 1.20.4  
[CHNaughty 4.13.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.13.0) for Paper and Spigot 1.20.2  
[CHNaughty 4.12.0](https://github.com/PseudoKnight/CHNaughty/releases/tag/v4.12.0) for Paper and Spigot 1.20.1

[Older Releases](https://github.com/PseudoKnight/CHNaughty/releases)

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

### psleep([player], bedLocation, [force])
Sets the player sleeping at the specified bed location.  
Optionally force sleeping even if player normally wouldn't be able to.  
If not forced, it will throw an exception when unsuccessful.  
The following conditions must be met for a player to sleep: the location must be a bed, the player must be near it,
it must not be obstructed, it must be night and there must not be hostile mobs nearby.

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
