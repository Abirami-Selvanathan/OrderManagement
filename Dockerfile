FROM adoptopenjdk:11-jre-hotspot
VOLUME /main-app
ADD build/libs/order_management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]