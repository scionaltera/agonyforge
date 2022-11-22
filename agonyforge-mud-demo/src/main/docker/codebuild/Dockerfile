FROM amazoncorretto:11-alpine-jdk as build
MAINTAINER Scion Altera <scion@agonyforge.com>
WORKDIR /opt/build
COPY . /opt/build
RUN cd /opt/build \
    && apk add --no-cache bash \
    && ./gradlew --console=plain -PexcludeFragileTests clean build -x docker

FROM amazoncorretto:11-alpine-jdk
MAINTAINER Scion Altera <scion@agonyforge.com>
EXPOSE 8080
COPY --from=build /opt/build/agonyforge-mud-demo/build/libs/agonyforge-mud-demo-*.jar /opt/mud/mud.jar
CMD ["/usr/bin/java", \
"--add-modules", "java.se", \
"--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", \
"--add-opens", "java.base/java.lang=ALL-UNNAMED", \
"--add-opens", "java.base/java.nio=ALL-UNNAMED", \
"--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", \
"--add-opens", "java.management/sun.management=ALL-UNNAMED", \
"--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED", \
"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
"-jar", "/opt/mud/mud.jar"]
