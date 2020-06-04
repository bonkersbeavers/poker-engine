##  Hold'em engine implementation in Kotlin  [![Build Status](https://travis-ci.com/bonkersbeavers/poker-engine.svg?branch=master)](https://travis-ci.com/bonkersbeavers/poker-engine)

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

First install ecosystem image (this step is for development convenience, so that rebuilding final app container after changing the app's code doesn't trigger the download of tons of dependencies files). This image should be rebuilt every time maven dependencies are modified in pom.xml.
```
docker build -t maven:poker-engine-ecosystem -f ecosystem-image-dockerfile .
```

Build proper app image:
```
docker build -t koronapoker:engine .
```

run main class (add "local-game" argument to play simple console 3-players game):
```
docker run -it --rm koronapoker:engine mvn exec:java [-Dexec.args="local-game"]
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