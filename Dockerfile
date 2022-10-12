FROM eclipse-temurin:17.0.4_8-jre-alpine
RUN apk add --no-cache tini
WORKDIR /deployments
COPY target/baba-*-SNAPSHOT.jar baba.jar
RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser
CMD [ "/sbin/tini", "--", "java", "-jar", "baba.jar" ]
