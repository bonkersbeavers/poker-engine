FROM maven:poker-engine-ecosystem
WORKDIR /app

COPY ./src ./src

RUN mvn install

# FROM maven
# WORKDIR /app

# COPY ./pom.xml .
# COPY ./src ./src

# RUN mvn install