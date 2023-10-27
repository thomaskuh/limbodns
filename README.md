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

## Server Setup - Option 1: One-file-server
```
# Java 21+ is required, so depending on you distribution type something like this and make sure you have a proper version:
sudo apt-get install default-jre-headless
pacman -S jre-openjdk-headless

# Get it
wget https://nexus.kuhlins.org/repository/maven-public/net/limbomedia/limbodns/5.7/limbodns-5.7-jar-with-dependencies.jar

# Run it
# -Ddir specifies where to store config and data. If left, execution directory is used.
java -Ddir=/path/where/data/should/be/stored -jar limbodns-5.7-jar-with-dependencies.jar

# Visit admin interface: http://YOUR-SERVER:7777
```

## Server Setup - Option 2: Docker
```
docker run -d -p 7777:7777 -p 53:53/tcp -p 53:53/udp -v DATA-DIR:/data limbomedia/limbodns:stable
```

## Server Configuration
Upon the first start and if not already existing, LimboDNS creates the configuration file `[DATA_DIR]/config.json`. Edit and restart to customize ports, admin pass, log settings and forward header name for those running LimboDNS behind a reverse proxy. This is how it looks with default values:
```
{
  "portUDP" : 53,
  "portTCP" : 53,
  "portHTTP" : 7777,
  "timeout" : 5000,
  "remoteAddressHeader" : "X-Forwarded-For",
  "password" : "admin",
  "logQuery" : false,
  "logUpdate" : false
}
```


## Client
A client needs to send a simple HTTP request to update it's record. Therefore the DynDNS token given in the admin ui must be known. Note that one token can be used for multiple records of the same type to update at once. Here're some examples how to update. For sure you substitute localhost:7777 according to your installation and 123 with the token configured on the record to update.

```
# IP explicitly specified
http://localhost:7777/update/123/127.0.0.1

# IP autodetect
http://localhost:7777/update/123

# Here's an example using wget with IPv4 explicitly set, what's useful on IP dual stack setups.
wget --inet4-only -qO - http://localhost:7777/update/123
# As a result you'll get a response listing updated records in JSON format:
[
{"zone" : "example.com.", "record" : "www1", "type" : "A", "changed" : false, "value" : "127.0.0.1"},
{"zone" : "example.com.", "record" : "www2", "type" : "A", "changed" : false, "value" : "127.0.0.1"}
]
```

## Links:
* [Setup instructions and download](https://limbomedia.net/etc/limbodns)
* [GitHub, Sources](https://github.com/thomaskuh/limbodns)
* [Docker](https://hub.docker.com/r/limbomedia/limbodns/)

## Thanks:
Thanks to the great libraries I used in this project:
* [http://www.dnsjava.org/](http://www.dnsjava.org/)
* [https://www.eclipse.org/jetty/](https://www.eclipse.org/jetty/)
