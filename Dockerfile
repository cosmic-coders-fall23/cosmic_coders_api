FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn

COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine

ARG DEPENDENCY=/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 8080

ENV PORT 8080

ENTRYPOINT ["java","-cp","app:app/lib/*","org.cosmiccoders.api.CosmicCodersApiApplication"]