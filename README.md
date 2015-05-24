# CHNaughty

These functions are using NMS. They will probably break every MC version change, so you'll need to update the extension when that happens.

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
Sends a title message to a player. The fadein, stay and fadeout arguments must be integers representing time in ticks. Defaults are 20, 60, 20 respectively. The title or subtitle can be null. If a new title message is sent while one is in progress for that player, the new function will use the previous title arguments if they're not provided. 

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
Returns an array of average ticks per second over 5, 10 and 15 minutes.
