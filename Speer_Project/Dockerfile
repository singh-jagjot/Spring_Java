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

#FROM alpine:latest
#LABEL authors="jagjotsingh"
#WORKDIR ~/dev
#RUN apk add --no-cache git openjdk21-jdk maven
#RUN git clone https://github.com/singh-jagjot/Spring_Java.git
#RUN apk del -r git
#WORKDIR Spring_Java/Speer_Project
#RUN mvn clean package -DskipTests && rm -rf ~/.m2
#EXPOSE 8080
#CMD ["java", "-jar", "~/dev/Spring_Java/Speer_Project/target/project-0.0.1-SNAPSHOT.jar"]

FROM alpine:latest
LABEL authors="jagjotsingh"

# Install necessary packages: Git, OpenJDK, Maven
RUN apk add --no-cache git openjdk21-jdk maven

# Set the working directory
WORKDIR /app

# Copy the start script to the working directory
COPY dockerscript.sh /app/dockerscript.sh

# Make the script executable
RUN chmod +x /app/dockerscript.sh

# Expose port 8080, 5432
EXPOSE 8080
EXPOSE 5432

# Set the entrypoint to run the script
ENTRYPOINT ["/app/dockerscript.sh"]

# Default CMD arguments (can be overridden)
CMD ["--spring.profiles.active=prod"]

