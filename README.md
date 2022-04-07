# Slabbo

<p align="center">
    <a href="https://www.spigotmc.org/resources/slabbo-1-8-x-1-18-x.81368/">
        <img src="https://pluginbadges.glitch.me/api/v1/dl/Slabbo-limegreen-svg?spigot=81368&style=for-the-badge" />
    </a>
</p>

Slab shops for Spigot

Creating: Right click slab with stick

Destroying: Right click existing shop with stick


## Permissions

> This list is sorted in accordance with [`~/modules/Plugin/src/main/resource/plugin.yml`](./modules/Plugin/src/main/resources/plugin.yml).
>
> Exceptions include parent scopes which may encompass a group of permissions.

| Permission Node                        | Description                                                                   |
| -------------------------------------- | ----------------------------------------------------------------------------- |
| `slabbo.use`                           | Lets you use slab shops                                                       |
| `slabbo.create`                        | Lets you create slab shops                                                    |
| `slabbo.limit.*`                       | Gives you unlimited shops                                                     |
| `slabbo.limit.{n}`                     | Gives you {n} shops                                                           |
| `slabbo.destroy.self`                  | Lets you destroy your shops                                                   |
| `slabbo.destroy.others`                | Lets you destroy other peoples shops                                          |
| `slabbo.modify.self.*`                 | Gives you access to all the modification commands                             |
| `slabbo.modify.self.buyprice`          | Lets you modify the buy price of shops with a command                         |
| `slabbo.modify.self.sellprice`         | Lets you modify the sell price of shops with a command                        |
| `slabbo.modify.self.quantity`          | Lets you modify the quantity of shops with a command                          |
| `slabbo.modify.self.note`              | Lets you modify the sellers note of shops with a command                      |
| `slabbo.modify.others.*`               | Gives you access to use all the modification commands for other peoples shops |
| `slabbo.modify.others.buyprice`        | Lets you modify the buy price of other peoples shops                          |
| `slabbo.modify.others.sellprice`       | Lets you modify the sell price of other peoples shops                         |
| `slabbo.modify.others.quantity`        | Lets you modify the quantity of other peoples shops.                          |
| `slabbo.modify.others.note`            | Lets you modify the sellers note of other peoples shops                       |
| `slabbo.modify.*`                      | Gives you access to use all the modification commands for other peoples shops |
| `slabbo.modify.admin.*`                | Gives you access to use all the admin modification commands                   |
| `slabbo.modify.admin.owner`            | Lets you change the owner of a shop                                           |
| `slabbo.modify.admin.stock`            | Lets you set the stock of a shop                                              |
| `slabbo.admin.*`                       | Gives you access to all admin commands                                        |
| `slabbo.admin.toggle`                  | Lets you toggle shops as being admin shops                                    |
| `slabbo.admin.limit.*`                 | Gives you access to all the limiting stock commands                           |
| `slabbo.admin.limit.toggle`            | Lets you toggle admin shops to have limited stock                             |
| `slabbo.admin.limit.time`              | Lets you set the time between restocks for limited shops                      |
| `slabbo.admin.limit.stock.*`           | Lets you set the stock of limited shops                                       |
| `slabbo.admin.limit.stock.sell`        | Lets you set the sell stock of limited shops                                  |
| `slabbo.admin.limit.stock.buy`         | Lets you set the buy stock of limited shops                                   |
| `slabbo.link`                          | Lets you link a shop to a chest                                               |
| `slabbo.notifyupdate`                  | Shows you the update notification when you join                               |
| `slabbo.importshops`                   | Imports shop from another plugin                                              |
| `slabbo.shopcommands.edit.self.buy`    | Lets you edit the buy commands of your own shops                              |
| `slabbo.shopcommands.edit.self.sell`   | Lets you edit the sell commands of your own shops                             |
| `slabbo.shopcommands.list.self.buy`    | Lets you list the buy commands of your own shops                              |
| `slabbo.shopcommands.list.self.sell`   | Lets you list the sell commands of your own shops                             |
| `slabbo.shopcommands.edit.others.buy`  | Lets you edit the buy commands of other peoples shops                         |
| `slabbo.shopcommands.edit.others.sell` | Lets you edit the sell commands of other peoples shops                        |
| `slabbo.shopcommands.list.others.buy`  | Lets you list the buy commands of other peoples shops                         |
| `slabbo.shopcommands.list.others.sell` | Lets you list the sell commands of other peoples shops                        |
| `slabbo.save`                          | Saves the slabbo shops to disk                                                |
| `slabbo.list.all`                      | Lets a user list all shops on the server                                      |
| `slabbo.list.self`                     | Lets a user list all their shops                                              |

## Commands

- `/slabbo`
- `/slabbo admin`
- `/slabbo admin toggle`
- `/slabbo admin limit`
- `/slabbo admin limit toggle`
- `/slabbo admin limit stock buy {n}`
- `/slabbo admin limit stock sell {n}`
- `/slabbo destroy`
- `/slabbo import`
- `/slabbo modify`
- `/slabbo modify buyprice`
- `/slabbo modify sellprice`
- `/slabbo modify quantity`
- `/slabbo modify note`
- `/slabbo modify owner`
- `/slabbo modify stock`
- `/slabbo save`
- `/slabbo reload`
- `/slabbo info`
- `/slabbo list`
- `/slabbo list all`
- `/slabbo list all radius`
- `/slabbo list mine`
- `/slabbo list mine radius`
- `/slabbo shopcommands add buy`
- `/slabbo shopcommands add sell`
- `/slabbo shopcommands remove buy`
- `/slabbo shopcommands remove sell`
- `/slabbo shopcommands list buy`
- `/slabbo shopcommands list sell`

## Legal

Slabbo is licensed under the EUPL-1.2-or-later.

## Developing

In order to develop Slabbo, you first need to setup the development environment.

1. Download [BuildTools](https://www.spigotmc.org/wiki/buildtools/#what-is-it)
2. Build a jar for your NMS revision
[[1.8+](https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-legacy/)]
[[1.10 to 1.15](https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-10-1-15/)]
[[1.16+](https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/)]
2.1. In order to install dependencies for 1.18+, please run buildtools with the `--remapped` option to get the remapped revision jar.
2.2. Be sure to compile craftbukkit using the `--compile craftbukkit` option
3. Install the compiled JAR files to your local maven using `mvn install:install-file -Dfile="spigot-version.jar" -DgroupId=org.spigotmc -DartifactId=spigot -Dversion=version-R0.1-SNAPSHOT -Dpackaging=jar`
4. For final distribution, use the `Slabbo-dist-*-remapped-obf.jar` file.