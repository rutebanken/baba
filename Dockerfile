FROM openjdk:11-jre
ADD target/baba-0.0.1-SNAPSHOT.jar baba.jar

EXPOSE 8080 8776
CMD ["java","-jar", "/baba.jar", "$JAVA_OPTIONS"]
