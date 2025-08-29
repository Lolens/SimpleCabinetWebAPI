FROM azul/zulu-openjdk-debian:24-latest AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew && ./gradlew assemble --no-daemon
FROM azul/zulu-openjdk-debian:24-latest AS prod
WORKDIR /app/data
VOLUME /app/data
EXPOSE 9274
COPY --from=build /app/build/libs/web-*-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]