FROM alpine:3.18.4
MAINTAINER LimboMedia <admin@limbomedia.net>

RUN apk add --update --no-cache --repository=https://dl-cdn.alpinelinux.org/alpine/edge/testing openjdk21-jre-headless

ADD maven/limbodns-jar-with-dependencies.jar /limbodns.jar
RUN mkdir data

VOLUME /data

EXPOSE 7777
EXPOSE 53/tcp
EXPOSE 53/udp

ENTRYPOINT ["java", "-Ddir=/data", "-jar", "/limbodns.jar"]