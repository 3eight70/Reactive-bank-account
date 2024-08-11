FROM openjdk:17-jdk-oracle
WORKDIR /app
COPY target/atm-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "atm-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
