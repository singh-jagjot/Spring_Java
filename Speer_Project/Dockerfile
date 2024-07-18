#FROM eclipse-temurin:21
#LABEL authors="jagjotsingh"
#WORKDIR ~/dev
#RUN apt update && apt install -y --no-install-recommends git maven && rm -rf /var/lib/apt/lists/*
#RUN git clone https://github.com/singh-jagjot/Spring_Java.git
#WORKDIR Spring_Java/Speer_Project
#RUN mvn clean package -DskipTests && rm -rf /root/.m2/repository
#EXPOSE 8080
#CMD ["java", "-jar", "~/dev/Spring_Java/Speer_Project/target/project-0.0.1-SNAPSHOT.jar"]

#ENTRYPOINT ["top", "-b"]

FROM alpine:latest
LABEL authors="jagjotsingh"
WORKDIR ~/dev
RUN apk add --no-cache git openjdk21-jdk maven
RUN git clone https://github.com/singh-jagjot/Spring_Java.git && apk del -r git
WORKDIR Spring_Java/Speer_Project
RUN mvn clean package -DskipTests && rm -rf ~/.m2
EXPOSE 8080
CMD ["java", "-jar", "~/dev/Spring_Java/Speer_Project/target/project-0.0.1-SNAPSHOT.jar"]

