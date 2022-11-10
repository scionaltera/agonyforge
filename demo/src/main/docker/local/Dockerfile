FROM amazoncorretto:11-alpine-jdk
MAINTAINER Scion Altera <scion@agonyforge.com>
EXPOSE 8080
COPY demo-*.jar /opt/app/mud.jar
WORKDIR /opt/app
CMD ["/usr/bin/java", \
"--add-modules", "java.se", \
"--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", \
"--add-opens", "java.base/java.lang=ALL-UNNAMED", \
"--add-opens", "java.base/java.nio=ALL-UNNAMED", \
"--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", \
"--add-opens", "java.management/sun.management=ALL-UNNAMED", \
"--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED", \
"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
 "-jar", "mud.jar"]
