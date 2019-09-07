# LimboDNS

LimboDNS is a nice and simple authoritative Domain Name Server allowing you to run your very own DynDNS service for your domain(s).

## Features
* No complicated installation required. Either download and run the one-file-server or use docker.
* Webserver with WebUI included for easy configuration and dynamic DNS updates via HTTP.
* Support for IPv4 and IPv6.
* Support for A, AAAA and CNAME records.
* Easy auto generation of required NS and SOA records.
* Runs on Docker, Linux, Windows and other Java capable OS.
* I know you're tired of messing around with zone files, zone transfers, bind and crazy scripts just to run a simple DynDNS for some devices in your home network.

## Setup and run the one-file-server
```
# Java 8+ is required, so depending on you distribution type something like this:
sudo apt-get install default-jre-headless
pacman -S jre-openjdk-headless

# Get it
wget http://repo.kuhlins.org/artifactory/public/net/limbomedia/limbodns/5.1/limbodns-5.1-jar-with-dependencies.jar

# Run it
# -Ddir specifies where to storee config and data. If left, execution directory is used.
java -Ddir=/path/where/data/should/be/stored -jar limbodns-5.1-jar-with-dependencies.jar

# Visit admin interface: http://YOUR-SERVER:7777
```

## Setup and run with docker
```
docker run -d -p 7777:7777 -p 53:53/tcp -p 53:53/udp -v DATA-DIR:/data limbomedia/limbodns
```

## Configuration
Upon the first start and if not already existing, LimboDNS creates the configuration file `[DATA_DIR]/config.json`. Edit to set:
* Ports (DNS TCP/UDP, Admin interface HTTP)
* Admin password
* Forward header if you run LimboDNS behind a reverse proxy

## Links:
* [Setup instructions and download](https://limbomedia.net/etc/limbodns)
* [GitHub, Sources](https://github.com/thomaskuh/limbodns)
* [Docker](https://hub.docker.com/r/limbomedia/limbodns/)

## Thanks:
Thanks to the great libraries I used in this project:
* [http://www.dnsjava.org/](http://www.dnsjava.org/)
* [https://www.eclipse.org/jetty/](https://www.eclipse.org/jetty/)
