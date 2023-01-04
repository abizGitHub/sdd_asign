FROM openjdk
COPY target/sdd-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]