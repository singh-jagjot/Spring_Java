#FROM alpine:latest
#LABEL authors="jagjotsingh"
#
## Install necessary packages: Git, OpenJDK, Maven
#RUN apk add --no-cache git openjdk21-jdk maven
#
## Set the working directory
#WORKDIR /app
#
## Copy the start script to the working directory
#COPY dockerscript.sh /app/dockerscript.sh
#
## Make the script executable
#RUN chmod +x /app/dockerscript.sh
#
## Expose port 8080, 5432
#EXPOSE 8080
#EXPOSE 5432
#
## Set the entrypoint to run the script
#ENTRYPOINT ["/app/dockerscript.sh"]
#
## Default CMD arguments (can be overridden)
#CMD ["--spring.profiles.active=prod"]

FROM alpine:latest
LABEL authors="jagjotsingh"

RUN apk add --no-cache openjdk21-jdk

# Set the working directory
WORKDIR /app

# Copy the start script to the working directory
COPY project-0.0.1-SNAPSHOT.jar /app/project-0.0.1-SNAPSHOT.jar

# Expose port 8080, 5432
EXPOSE 8080
EXPOSE 5432

# Set the entrypoint to run the script
ENTRYPOINT ["java", "-jar", "/app/project-0.0.1-SNAPSHOT.jar"]

# Default CMD arguments (can be overridden)
CMD ["--spring.profiles.active=prod"]

