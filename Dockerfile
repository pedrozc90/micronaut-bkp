# PHASE 1
FROM maven:3.8.6-amazoncorretto-11 AS build

# create and change directory
WORKDIR /app

# copy all files to the image (.dockerignore will filter unwanted files)
COPY . .

RUN mvn -DskipTests --file ./pom.xml clean compile package

# PHASE 2
FROM amazoncorretto:11.0.17-alpine3.16 AS stage

ARG APP_VERSION="0.0.1"

ENV DB_HOST="127.0.01"
ENV DB_PORT="5432"
ENV DB_NAME="blank"
ENV DB_USER="postgres"
ENV DB_PASS="postgres"

# create and change directory
WORKDIR /app

COPY --from=build "/app/target/blank-${APP_VERSION}.jar" "/app/blank.jar"

EXPOSE 4000

ENTRYPOINT [ "java", "-jar", "/app/blank.jar" ]
