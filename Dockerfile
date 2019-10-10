FROM openjdk:11-jre
ADD target/baba-*-SNAPSHOT.jar baba.jar

EXPOSE 8080
CMD java $JAVA_OPTIONS -jar /baba.jar
