FROM openjdk:11.0.3-jdk-stretch as build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM openjdk:11.0.3-jre-slim-stretch
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=build /workspace/app/src/main/resources/application-prod.properties /app/application.properties
ENTRYPOINT ["java","-cp","app:app/lib/*","ch.uzh.ifi.access.AccessApplication"]