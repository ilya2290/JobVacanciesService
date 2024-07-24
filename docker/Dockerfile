FROM openjdk:22
WORKDIR /app
COPY ${APP_JAR} app.jar
ARG APP_JAR=*jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080

