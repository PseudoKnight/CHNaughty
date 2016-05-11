# CHNaughty

These functions are using NMS/OBC. They will probably break every MC version change and possibly even more often than that, so you'll need to update the extension when that happens. There's no guarantee that every single Spigot build will be supported in the future, so code appropriately.

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
Sends a title message to a player. The fadein, stay and fadeout arguments must be integers representing time in ticks (50ms). Defaults are 20, 60, 20 respectively. The title or subtitle can be null. If a new title message is sent while one is in progress for that player, the new function will use the previous title arguments if they're not provided. 

```
if(function_exists('title_msg')) {
  title_msg('PseudoKnight', null, 'Spleef', 20, 60, 20);
}
@countdown = array(3);
set_interval(1000, closure(){
  if(@countdown[0] > 0) {
    if(function_exists('title_msg')) {
      title_msg('PseudoKnight', @countdown[0], null, 0, 60, 20);
    }
    @countdown[0] -= 1;
  } else {
    if(function_exists('title_msg')) {
      title_msg('PseudoKnight', color('green').'GO!', null, 0, 0, 20);
    }
    clear_task();
  }
});
```

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
