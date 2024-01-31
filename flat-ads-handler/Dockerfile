FROM amazoncorretto:17-alpine3.19

ARG CONFIG_DIR=/opt/relohome/conf
ENV CONFIG_DIR=${CONFIG_DIR}

WORKDIR /etc/relohome
COPY build/libs/*-fat-*.jar ./handler.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/etc/relohome/handler.jar"]