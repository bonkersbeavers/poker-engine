FROM maven

COPY ./pom.xml /home/engine/
COPY ./src/ /home/engine/src

WORKDIR /home/engine/

RUN mvn install
