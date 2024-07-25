FROM openjdk:22
WORKDIR /app
ARG APP_JAR=*jar
COPY ${APP_JAR} app.jar
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
EXPOSE 8080

