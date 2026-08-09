FROM eclipse-temurin:21-jre
VOLUME /tmp
COPY target/wordcounter-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]