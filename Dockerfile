FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/baba-*-SNAPSHOT.jar baba.jar
CMD java $JAVA_OPTIONS -jar baba.jar

