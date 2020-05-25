FROM maven

COPY ./server/pom.xml /home/poker-engine/
COPY ./server/src /home/poker-engine/src

WORKDIR /home/poker-engine/

RUN mvn package -DskipTests


EXPOSE 5050
