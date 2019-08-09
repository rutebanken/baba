FROM openjdk:11-jre
ADD target/baba-0.0.1-SNAPSHOT.jar baba.jar

EXPOSE 8080
CMD java $JAVA_OPTIONS -jar /baba.jar
