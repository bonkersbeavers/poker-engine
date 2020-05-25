##  Hold'em engine implementation in Kotlin  [![Build Status](https://travis-ci.com/bonkersbeavers/poker-engine.svg?branch=master)](https://travis-ci.com/bonkersbeavers/poker-engine)

#### Currently engine's functionality is limited to running simple interactive console game

Install dependencies:
```
mvn clean install
```

Run engine grpc service:
```
mvn exec:java
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
