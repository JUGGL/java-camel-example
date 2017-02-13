# An Apache Camel Application Written In Java

## Running
```bash
mvn exec:java
```

## Requirements

* [Maven](http://maven.apache.org)
* A Twitter developer account and the appropriate keys/secrets
* Java 8

twitter.properties
```
api.key=XXXXXXXXXXXXXXXXXXXX
api.secret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
access.token=XXXXXXXXXXXXXXXXXXXXXXXX
access.secret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
db.url=jdbc:postgresql://127.0.0.1:5432/lykely
db.user=dbuser
db.pass=secret
db.pool=20
```