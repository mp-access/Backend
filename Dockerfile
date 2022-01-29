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
RUN apt-get update
RUN apt-get -y --allow-unauthenticated upgrade
RUN apt-get -y install openssh-client

ARG DEPENDENCY=/workspace/app/build/dependency
ARG DIR=/app/access
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib ${DIR}/lib
COPY --from=build ${DEPENDENCY}/META-INF ${DIR}/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes ${DIR}
COPY --from=build /workspace/app/src/main/resources/application.properties ${DIR}/application.properties

# creates a system user (-r), with no password, no home directory set, and no shell
#RUN groupadd -r backend && useradd -r -s /bin/false -g backend backend
#RUN chown -R backend:backend /app

#USER backend

WORKDIR /app

ENTRYPOINT ["java","-Djdk.tls.client.protocols=TLSv1.2", "-cp","access:access/lib/*","ch.uzh.ifi.access.AccessApplication"]
