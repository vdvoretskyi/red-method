FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8081
COPY target/todo-backend.jar todo-backend.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /todo-backend.jar ${0} ${@}"]