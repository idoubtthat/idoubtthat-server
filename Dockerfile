FROM gradle:8-jdk21 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble --no-daemon

#FROM amazoncorretto:21
#EXPOSE 8080:8080
#RUN mkdir /app
#COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/idoubtthat-server.jar
#ENTRYPOINT ["java","-jar","/app/idoubtthat-server.jar"]
