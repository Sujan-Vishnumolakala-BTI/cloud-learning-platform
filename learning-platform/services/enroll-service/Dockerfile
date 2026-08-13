FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "target/enroll-service-0.0.1-SNAPSHOT.jar"]