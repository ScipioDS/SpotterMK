FROM openjdk:17-jdk-slim AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY scripts scripts

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y \
    postgresql-client \
    libpq-dev \
    python3.9 \
    python3.9-distutils \
    python3-pip \
    build-essential \
    curl \
    && curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt /tmp/
RUN python3.9 -m pip install --upgrade pip \
    && python3.9 -m pip install --no-cache-dir -r /tmp/requirements.txt \
    && rm -f /tmp/requirements.txt

ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

COPY --from=build /workspace/app/scripts /workspace/app/scripts
RUN chmod +x /workspace/app/scripts/*

EXPOSE 8080
ENTRYPOINT ["java","-jar","-XX:+ExitOnOutOfMemoryError", "/app.jar"]
