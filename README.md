##  Hold'em engine implementation in Kotlin  [![Build Status](https://travis-ci.com/bonkersbeavers/poker-engine.svg?branch=master)](https://travis-ci.com/bonkersbeavers/poker-engine)

##### Currently engine's functionality is limited to running simple interactive console game

### Running the service locally:

#### 1 - with maven

install dependencies:
```
mvn clean install
```

run main class:
```
mvn exec:java
```

#### 2 - with docker

build docker image:
```
docker build -t poker-engine .
```

run main class (add "local-game" argument to play simple console 3-players game):
```
docker run -it --rm poker-engine mvn exec:java [-Dexec.args="local-game"]
```

### Testing:

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