# visuallaik

The IDP TUM project to visualize the performance of the cpu grid.

## Getting Started

To open and modify project use any IDE(Idea was used). Attach tomcat run/debug tomcat configuration to current source.

To deploy a project use "maven war". In idea Maven Projects -> Plugins -> war -> war:war 
Move .war file from /target to tomcat_home/webapps/ROOT.war.
Run tomcat_home/bin/startup script.
Now you can observe a website on localhost:8080 
### Prerequisites

Apache tomcat 9
Mysql database with user, which has full rights on database visuallaik for user visuallaik.

Build on sap openui5 (integrated inside code)

```
Give the example
```

And repeat

```
until finished
```
## Website settings

Project has configuration files.
resources/hibernate.cfg.xml - contains database user, used by program. <br>
resources/nodeStaticData.json - static database nodes, that are automatically filled to database. You can change this file to change node row, column and ip text. <br>
resources/serverSettings.json - database dump parameters. <br>
    After filling over maxDbSizeMb database dumps all data until recent minDbSizeMb (in MBytes)<br> 
    After having records later then maxDbLogDays in days database is dumped until minDbLogDays<br>
    Dumps are stored in dumpPath, which can be absolute or relative (to tomcat_home).
## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

Sergey Podanev

## License
