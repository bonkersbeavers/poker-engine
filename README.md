###  Hold'em engine implementation in Kotlin. [![Build Status](https://travis-ci.org/bonkersbeavers/shell-poker.svg?branch=master)](https://travis-ci.com/bonkersbeavers/poker-engine.svg?branch=master)

#### Currently engine's functionality is limited to running simple interactive console game

Build fat jar with:
```
mvn package [-DskipTests]
```

Run interactive main:
```
java -jar ./target/FAT_JAR_NAME
```

To run tests:
```
mvn test
```

To run ktlint verification:
```
mvn antrun:run@ktlint
```

To run ktlint auto-formatting:
```
mvn antrun:run@ktlint-format
```
