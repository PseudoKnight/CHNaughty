# CHNaughty

These functions are using NMS/OBC. They will probably break every MC version change and possibly even more often than that, so you'll need to update the extension when that happens. There's no guarantee that every single Spigot build will be supported in the future, so code appropriately.

## Sign Editor
### open_sign{[player], signLocation, [lines]}
Opens a sign editor for the given sign location. Lines must be an array with 4 values or null. If not provided, it'll use the lines from the given sign.

## Virtual Book
### open_book{[playerName], pages} 
Sends a virtual book to a player. Accepts an array of pages. All pages must be either raw JSON or strings. If the JSON is not formatted correctly, it will fall back to string output.

## Relative/Smooth Teleports
### relative_teleport([playerName], relativeLocation)
Sets the player location relative to where they are on their client. This can be used for smooth teleportation. The location is not an absolute world location. X would be how many meters along the x coordinate the location is from the player's current location.

## Entity Rotation
### set_entity_rotation(entityID, yaw, [pitch])
Sets an entity's yaw and pitch without teleporting or ejecting.

## Forced Sleeping
### psleep([playerName], bedLocation)
Sets the player sleeping at the specified bed location. Throws an exception when unsuccessful. The following conditions must be met for a player to sleep: the location must be a bed, the player must be near it, it must be night and there must not be hostile mobs nearby.

## Player Ping
### ping([playerName])
Returns the player's ping to the server. This data is stored on the server, so the accuracy of the result is dependent on the server's method.

## Player Animation
### pswing_hand([playerName], [hand])
Swing the player's hand in an attack animation. The hand parameter can be either main_hand (default) or off_hand.

## Sky Colors
### set_psky([playerName], number, number)
Sends a packet to the player to change their sky color.

## Player Arrows
### set_parrow_count([playerName], count)
Sets the player's body arrow count.

## Ray Tracing
### ray_trace([playerName], [location], [range])
Returns an array of result data from a ray trace from the player's eye location or the given location. Result array contains the following keys: 'hitblock' is whether or not a block was hit; 'location' contains the location where the ray trace ends; 'origin' contains the location where the ray trace starts (useful if you don't specify a location manually); 'entities' contains an array of hit entities where each array contains a 'location' key and 'uuid' key.

## Action Messages
### action_msg([playerName], message)
Sends a message to the action bar, located right above the player's hotbar.

``` 
if(function_exists('action_msg')) {
  action_msg('PseudoKnight', colorize('&2You picked up an apple.'));
}
```

## Title Messages
### title_msg([playerName], title, subtitle, [fadein, stay, fadeout])
Deprecated for title() in CommandHelper. Sends a title message to a player. The fadein, stay and fadeout arguments must be integers representing time in ticks (50ms). Defaults are 20, 60, 20 respectively. The title or subtitle can be null. If a new title message is sent while one is in progress for that player, the new function will use the previous title arguments if they're not provided.

## Tab List Headers/Footers
### psend_list_header_footer([playerName], header, footer)
Sends a header and/or footer to a player's tab list. Header or footer can be null.

## Ticks per Second
### tps()
Returns an array of average ticks per second over 5, 10 and 15 minutes. eg. {19.9999999,19.99888567,19.56889299}

## Mob Attributes
### get_attribute(entity UUID, attribute)
Returns a generic attribute's value as a double for the specified mob. Available attributes: attackDamage, followRange, knockbackResistance, movementSpeed, maxHealth, attackSpeed, armor, armorToughness, and luck. Not all mobs will have every attribute, in which case a NullPointerException will be thrown.

### set_attribute(entity UUID, attribute, double)
Sets the generic attribute for the given mob. You can consult the Minecraft wiki for appropriate ranges.

```
bind(player_interact_entity, null, array('clicked': 'HORSE'), @event) {
  set_attribute(@event['id'], 'movementSpeed', 0.3375); # set all horses to max natural speed
}
```
