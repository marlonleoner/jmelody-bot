FROM maven:3.9.11-eclipse-temurin-17 AS maven

WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./src ./src

RUN mvn clean install

FROM eclipse-temurin:17-jdk

COPY application.properties /app/application.properties
COPY --from=maven /app/target/*-shaded.jar /app/jmelody.jar
COPY --from=maven /root/.m2 /root/.m2

CMD ["java", "-Dconfig.file=/app/application.properties", "-jar", "/app/jmelody.jar"]