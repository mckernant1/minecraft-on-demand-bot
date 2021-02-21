# Minecraft Server on Demand Bot
This is a discord bot that creates minecraft servers. It relies on AWS Cloudformation and DynamoDB

### Commands
`$help` -> lists this menu
`$list` -> lists the servers you have created
`$create <serverName> [serverProperties]` -> creates a new server see creation options
`$start <serverName>` -> starts a server
`$stop <serverName>` -> stops a server
`$update <serverName> [serverProperties]` -> updates the config of an existing server`$destroy <serverName>` -> destroys a server and its world
`$destroy <serverName>` -> destroys a server and its world

### Creation Options
cpu -> 1024 = 1CPU [reference](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html)
memory -> in MiB [reference](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html)
type -> default is vanilla [reference](https://github.com/itzg/docker-minecraft-server)
version -> default is latest [reference](https://github.com/itzg/docker-minecraft-server)
ops -> a string of comma delimited ops 'TheeAlbinoTree,TheBearPenguin'


### Creation Examples
Should be comma delimited like this NO SPACES:
`$create myserver memory=2048,version=1.16.5,cpu=1024`
