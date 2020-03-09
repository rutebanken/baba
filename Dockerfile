FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /deployments
COPY target/baba-*-SNAPSHOT.jar baba.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD java $JAVA_OPTIONS -jar baba.jar

