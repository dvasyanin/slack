FROM maven:3.3-jdk-8
COPY . /usr/src/myapp
COPY .env.production .env
WORKDIR /usr/src/myapp
RUN mvn clean compile assembly:single
CMD ["java", "-jar", "target/slack-1.0-SNAPSHOT.jar"]