# Slabbo
Slab shops for Spigot

Creating: Right click slab with stick

Destroying: Right click existing shop with stick

## Permissions

> This list is sorted in accordance with [`~/modules/Plugin/src/main/resource/plugin.yml`](./modules/Plugin/src/main/resources/plugin.yml).
>
> Exceptions include parent scopes which may encompass a group of permissions.

| Permission Node                  | Description                                                                   |
| -------------------------------- | ----------------------------------------------------------------------------- |
| `slabbo.use`                     | Lets you use slab shops                                                       |
| `slabbo.create`                  | Lets you create slab shops                                                    |
| `slabbo.limit.*`                 | Gives you unlimited shops                                                     |
| `slabbo.limit.{n}`               | Gives you {n} shops                                                           |
| `slabbo.destroy`                 | Lets you destroy your shops                                                   |
| `slabbo.destroy.others`          | Lets you destroy other peoples shops                                          |
| `slabbo.modify.self.*`           | Gives you access to all the modification commands                             |
| `slabbo.modify.self.buyprice`    | Lets you modify the buy price of shops with a command                         |
| `slabbo.modify.self.sellprice`   | Lets you modify the sell price of shops with a command                        |
| `slabbo.modify.self.quantity`    | Lets you modify the quantity of shops with a command                          |
| `slabbo.modify.self.note`        | Lets you modify the sellers note of shops with a command                      |
| `slabbo.modify.others.*`         | Gives you access to use all the modification commands for other peoples shops |
| `slabbo.modify.others.buyprice`  | Lets you modify the buy price of other peoples shops                          |
| `slabbo.modify.others.sellprice` | Lets you modify the sell price of other peoples shops                         |
| `slabbo.modify.others.quantity`  | Lets you modify the quantity of other peoples shops.                          |
| `slabbo.modify.others.note`      | Lets you modify the sellers note of other peoples shops                       |
| `slabbo.modify.*`                | Gives you access to use all the modification commands for other peoples shops |
| `slabbo.modify.admin.owner`      | Lets you change the owner of a shop                                           |
| `slabbo.modify.admin.stock`      | Lets you set the stock of a shop                                              |
| `slabbo.admin.*`                 | Gives you access to all admin commands                                        |
| `slabbo.admin.toggle`            | Lets you toggle shops as being admin shops                                    |
| `slabbo.admin.limit.*`           | Gives you access to all the limiting stock commands                           |
| `slabbo.admin.limit.toggle`      | Lets you toggle admin shops to have limited stock                             |
| `slabbo.admin.limit.time`        | Lets you set the time between restocks for limited shops                      |
| `slabbo.admin.limit.stock.*`     | Lets you set the stock of limited shops                                       |
| `slabbo.admin.limit.stock.sell`  | Lets you set the sell stock of limited shops                                  |
| `slabbo.admin.limit.stock.buy`   | Lets you set the buy stock of limited shops                                   |
| `slabbo.link`                    | Lets you link a shop to a chest                                               |
| `slabbo.notifyupdate`            | Shows you the update notification when you join                               |
| `slabbo.save`                    | Saves the slabbo shops to disk                                                |
| `slabbo.importshops`             | Imports shop from another plugin                                              |

## Commands

- /slabbo
- /slabbo destroy
- /slabbo import
- /slabbo toggleadmin
- /slabbo modify
- /slabbo modify buyprice
- /slabbo modify sellprice
- /slabbo modify quantity
- /slabbo modify note
- /slabbo modify owner
- /slabbo modify stock
- /slabbo save
- /slabbo reload
- /slabbo info
