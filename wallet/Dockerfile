FROM openjdk:17-jdk-oracle
WORKDIR /app
COPY target/wallet-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "wallet-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
