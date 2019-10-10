FROM openjdk:11-jre
WORKDIR /deployments
COPY target/baba-*-SNAPSHOT.jar baba.jar
CMD java $JAVA_OPTIONS -jar baba.jar
