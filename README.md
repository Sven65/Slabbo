# Slabbo

![Spiget Download Size](https://img.shields.io/spiget/download-size/81368)
![Spiget Downloads](https://img.shields.io/spiget/downloads/81368)
![Spiget Version](https://img.shields.io/spiget/version/81368)
![Spiget Stars](https://img.shields.io/spiget/stars/81368)
![Spiget Rating](https://img.shields.io/spiget/rating/81368)
![Spiget Tested Server Versions](https://img.shields.io/spiget/tested-versions/81368)
![Discord](https://img.shields.io/discord/732260527048491119)

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

| Permission Node                           | Description                                                                      |
|-------------------------------------------|----------------------------------------------------------------------------------|
| `slabbo.use`                              | Lets you use slab shops                                                          |
| `slabbo.create`                           | Lets you create slab shops                                                       |
| `slabbo.limit.*`                          | Gives you unlimited shops                                                        |
| `slabbo.limit.{n}`                        | Gives you {n} shops                                                              |
| `slabbo.destroy.self`                     | Lets you destroy your shops                                                      |
| `slabbo.destroy.others`                   | Lets you destroy other peoples shops                                             |
| `slabbo.modify.self.*`                    | Gives you access to all the modification commands                                |
| `slabbo.modify.self.buyprice`             | Lets you modify the buy price of shops with a command                            |
| `slabbo.modify.self.sellprice`            | Lets you modify the sell price of shops with a command                           |
| `slabbo.modify.self.quantity`             | Lets you modify the quantity of shops with a command                             |
| `slabbo.modify.self.note`                 | Lets you modify the sellers note of shops with a command                         |
| `slabbo.modify.self.itemdisplay`          | Lets you modify the item display settings of shops with a command                |
| `slabbo.modify.self.itemdisplay.toggle`   | Lets you toggle the item display of shops with a command                         |
| `slabbo.modify.self.itemdisplay.name`     | Lets you set the item display name of shops with a command                       |
| `slabbo.modify.others.*`                  | Gives you access to use all the modification commands for other peoples shops    |
| `slabbo.modify.others.buyprice`           | Lets you modify the buy price of other peoples shops                             |
| `slabbo.modify.others.sellprice`          | Lets you modify the sell price of other peoples shops                            |
| `slabbo.modify.others.quantity`           | Lets you modify the quantity of other peoples shops.                             |
| `slabbo.modify.others.note`               | Lets you modify the sellers note of other peoples shops                          |
| `slabbo.modify.others.itemdisplay`        | Lets you modify the item display settings of other peoples shops                 |
| `slabbo.modify.others.itemdisplay.toggle` | Lets you toggle the item display of other peoples shops                          |
| `slabbo.modify.others.itemdisplay.name`   | Lets you set the item display name of other peoples shops                        |
| `slabbo.modify.*`                         | Gives you access to use all the modification commands for other peoples shops    |
| `slabbo.modify.admin.*`                   | Gives you access to use all the admin modification commands                      |
| `slabbo.modify.admin.owner`               | Lets you change the owner of a shop                                              |
| `slabbo.modify.admin.stock`               | Lets you set the stock of a shop                                                 |
| `slabbo.admin.*`                          | Gives you access to all admin commands                                           |
| `slabbo.admin.toggle`                     | Lets you toggle shops as being admin shops                                       |
| `slabbo.admin.limit.*`                    | Gives you access to all the limiting stock commands                              |
| `slabbo.admin.limit.gui`                  | Lets you open the limit stock GUI for admin shops                                |
| `slabbo.admin.limit.toggle`               | Lets you toggle admin shops to have limited stock                                |
| `slabbo.admin.limit.time`                 | Lets you set the time between restocks for limited shops                         |
| `slabbo.admin.limit.stock.*`              | Lets you set the stock of limited shops                                          |
| `slabbo.admin.limit.stock.sell`           | Lets you set the sell stock of limited shops                                     |
| `slabbo.admin.limit.stock.buy`            | Lets you set the buy stock of limited shops                                      |
| `slabbo.link`                             | Lets you link a shop to a chest                                                  |
| `slabbo.notifyupdate`                     | Shows you the update notification when you join                                  |
| `slabbo.importshops`                      | Imports shop from another plugin                                                 |
| `slabbo.shopcommands.edit.self.buy`       | Lets you edit the buy commands of your own shops                                 |
| `slabbo.shopcommands.edit.self.sell`      | Lets you edit the sell commands of your own shops                                |
| `slabbo.shopcommands.list.self.buy`       | Lets you list the buy commands of your own shops                                 |
| `slabbo.shopcommands.list.self.sell`      | Lets you list the sell commands of your own shops                                |
| `slabbo.shopcommands.edit.others.buy`     | Lets you edit the buy commands of other peoples shops                            |
| `slabbo.shopcommands.edit.others.sell`    | Lets you edit the sell commands of other peoples shops                           |
| `slabbo.shopcommands.list.others.buy`     | Lets you list the buy commands of other peoples shops                            |
| `slabbo.shopcommands.list.others.sell`    | Lets you list the sell commands of other peoples shops                           |
| `slabbo.save`                             | Saves the slabbo shops to disk                                                   |
| `slabbo.list.all`                         | Lets a user list all shops on the server                                         |
| `slabbo.list.self`                        | Lets a user list all their shops                                                 |
| `slabbo.unlink.self`                      | Lets you unlink chests of your shops with a command                              |
| `slabbo.unlink.others`                    | Lets you unlink chests from other peoples shops with a command                   |
| `slabbo.admin.set.owner_name`             | Lets you set the displayed owner name of an admin shop                           |
| `slabbo.admin.set.*`                      | Gives you access to set all properties on an admin shop                          |
| `slabbo.shop.commandopen`                 | Lets you open a shop by command                                                  |
| `slabbo.shop.virtual.create`              | Lets you create a virtual shop                                                   |
| `slabbo.shop.virtual.open`                | Lets you open a virtual shop                                                     |
| `slabbo.shop.virtual.edit`                | Lets you edit a virtual shop                                                     |
| `slabbo.shop.virtual.delete`              | Lets you delete a virtual shop                                                   |
| `slabbo.admin.toggle.virtual`             | Lets you toggle if a virtual shop is an admin shop                               |
| `slabbo.admin.limit.virtual.*`            | Gives you access to all the limiting stock commands for virtual admin shops      |
| `slabbo.admin.limit.virtual.gui`          | Lets you open the limit stock GUI for virtual admin shops                        |
| `slabbo.admin.limit.virtual.toggle`       | Lets you toggle the stock limit on a virtual admin shop                          |
| `slabbo.admin.limit.virtual.stock.buy`    | Lets you set the buy stock limit on a virtual admin shop                         |
| `slabbo.admin.limit.virtual.stock.sell`   | Lets you set the sell stock limit on a virtual admin shop                        |
| `slabbo.admin.limit.virtual.time`         | Lets you set the restock time on a virtual admin shop                            |
| `slabbo.admin.set.virtual.owner_name`     | Lets you set the owner name on a virtual admin shop                              |

## Commands

- `/slabbo`
- `/slabbo admin`
- `/slabbo admin toggle`
- `/slabbo admin limit`
- `/slabbo admin limit toggle`
- `/slabbo admin limit stock buy {n}`
- `/slabbo admin limit stock sell {n}`
- `/slabbo admin limit gui`
- `/slabbo destroy`
- `/slabbo import`
- `/slabbo modify`
- `/slabbo modify buyprice`
- `/slabbo modify sellprice`
- `/slabbo modify quantity`
- `/slabbo modify note`
- `/slabbo modify owner`
- `/slabbo modify stock`
- `/slabbo modify itemdisplay toggle`
- `/slabbo modify itemdisplay name [name]`
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
- `/slabbo shop open <x> <y> <z> [world]`
- `/slabbo shop create virtual <shopName>`
- `/slabbo shop open virtual <shopName>`
- `/slabbo shop edit virtual <shopName>`
- `/slabbo shop delete virtual <shopName>`
- `/slabbo admin toggle virtual <shopName>`
- `/slabbo admin limit virtual gui`
- `/slabbo admin limit virtual toggle <shopName>`
- `/slabbo admin limit virtual stock buy <shopName> <stock>`
- `/slabbo admin limit virtual stock sell <shopName> <stock>`
- `/slabbo admin set virtual owner_name <shopName> [name]`
- `/slabbo admin set virtual time <shopName> <time>`
- 

## SQLite Support

Slabbo supports using SQLite as a storage engine for shop data. SQLite is a lightweight, file-based database that does not require a separate server.

### Enabling SQLite

To use SQLite, set the `storageEngine` option in your configuration file (usually `config.yml`) to `sqlite`:

```yaml
storageEngine: sqlite
```

### Migrating Existing Data to SQLite

If you are currently using another storage engine (such as YAML or MySQL), you can migrate your data to SQLite using the following workflow:

1. **Run the Migration Command**

   In your server console, execute:

   ```
   /slabbo migrate sqlite
   ```

   This command will export your current shop data and import it into a new SQLite database file (typically `slabbo.db` in your plugin data folder).

2. **Update the Configuration**

   Open your `config.yml` and change the `storageEngine` value to `sqlite`:

   ```yaml
   storageEngine: sqlite
   ```

3. **Restart the Server**

   Restart your Minecraft server to apply the changes and start using SQLite as the storage backend.

### Notes

- Always back up your data before performing a migration.
- After migration, verify that all shop data has been transferred correctly.
- If you encounter issues, check the server logs for error messages.

**SQLite database file location:**  
The SQLite database file is created as `slabbo.db` in your plugin's data folder (usually `plugins/Slabbo/slabbo.db`).


## Suggested Prices and allowCents

Slabbo provides suggested buy/sell prices and quantities for items when creating shops. These are defined in `modules/Plugin/src/main/resources/suggested.yml`.

- **allowCents config option:**
  - The `allowCents` option in `config.yml` controls whether decimals are allowed for prices.
  - If `allowCents` is set to `false`, all suggested buy/sell prices will be rounded to the nearest integer automatically, even if the values in `suggested.yml` contain decimals.
  - If `allowCents` is `true`, decimals in suggested prices will be used as defined.

You can override or add suggestions in `suggested.yml`. Item names must match the Bukkit Material enum names.

Example entry:

```yaml
DIAMOND:
  buy: 100.0
  sell: 50.5
  quantity: 1
```

See the comments in `suggested.yml` for more details.

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

### Common issues with NMS Compile

#### Error with applyPatches.sh

If you get an error with `\r` and alike when running applyPatches, make sure you're running with git bash and not WSL bash.

#### Can't find bungeecoord-chat

Run
```sh
mvn dependency:get -DrepoUrl=https://oss.sonatype.org/content/repositories/snapshots -DgroupId=net.md-5 -DartifactId=bungeecord-chat -Dversion=<version>-SNAPSHOT
```
