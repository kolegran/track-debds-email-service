FROM openjdk:11-jre

RUN mkdir /app
COPY build/libs/track-debts-email-service-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java", "-jar", "/app/track-debts-email-service-0.0.1-SNAPSHOT.jar"]