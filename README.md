# soccer-booking

Un utilitaire qui te permet de voir en un coup d'oeil les prochains crenaux disponibles pour ton urban en IDF.

Améliorations:
* Ajouter d'autres provider (LeFive, EliteSoccer...)
* Ajouter d'autres terrains..
* Ajouter des fonctions de filtres, quelques exemples:
    * --filterDayOfWeek SATURDAY,SUNDAY
    * --filterRangeHour 16:00-21:00
    * --filterCenter ASNIERES
    * --mulipleTry (mode qui fait plusieurs tentatives pour vous)

### Build a FatJar

Compiler un FatJar via la custom task
```shell
$ gradle customFatJar
```

### Show usage
```shell
$ java -jar build/libs/soccer-booking-fatjar-1.0-SNAPSHOT.jar -h
    usage: soccer booking helper
     -at,--atDate <arg>    Expected format date and time like < dd/MM/yyyy
                           HH:mm>
     -c,--center <arg>     Values are [ PUTEAUX, ASNIERES, LADEFENSE ]
     -d,--duration <arg>   Values are [ 60, 90, 120 ]
     -h,--help             Show usage information
     -p,--password <arg>   Platform user's password
     -u,--user <arg>       Platform username
```

### Sample usage

```shell
$ java -jar build/libs/soccer-booking-fatjar-1.0-SNAPSHOT.jar \
    -u "myemail@mailprovider.com" \
    -p "bestPassword" \
    -at "31/12/2019 23:59" \
    -d 90 -c PUTEAUX
```