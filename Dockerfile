FROM openjdk:11.0.3-jre

EXPOSE 8080

ADD target/covid-tracker.jar /usr/local/server.jar

CMD ["java", "-jar", "/usr/local/server.jar"]
