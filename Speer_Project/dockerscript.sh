#!/bin/sh

# Exit immediately if a command exits with a non-zero status
set -e

git clone https://github.com/singh-jagjot/Spring_Java.git
cd Spring_Java/Speer_Project
mvn clean package -DskipTests

# Remove the .m2 folder to save space
#rm -rf /root/.m2
mv /app/Spring_Java/Speer_Project/target/project-0.0.1-SNAPSHOT.jar /app
cd /app
rm -rf Spring_Java
java -jar project-0.0.1-SNAPSHOT.jar "$@"