FROM alpine:3.19
MAINTAINER LimboMedia <admin@limbomedia.net>

RUN apk add --update --no-cache openjdk21-jre-headless

ADD maven/limbodns-jar-with-dependencies.jar /limbodns.jar
RUN mkdir data

VOLUME /data

EXPOSE 7777
EXPOSE 53/tcp
EXPOSE 53/udp

ENTRYPOINT ["java", "-Ddir=/data", "-jar", "/limbodns.jar"]