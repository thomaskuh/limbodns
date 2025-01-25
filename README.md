# LimboDNS

LimboDNS is a nice and simple authoritative Domain Name Server allowing you to run your very own DynDNS service for your domain(s).

## Features
* No complicated installation required. Either download and run the one-file-server or use docker.
* Webserver with WebUI included for easy configuration and dynamic DNS updates via HTTP.
* Support for IPv4 and IPv6.
* Support for A, AAAA, TXT, MX and CNAME records.
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


## Client APIs
Beside editing DNS records via admin interface LimboDNS provides 2 APIs (maybe more in future) to update DNS record values with different clients. Those could be used for so-called DynDNS requests updating dynamic IPs whenever changed.

### LimboDNS Simple API
A client needs to send a simple HTTP request to update it's record. Therefore the DynDNS token given in the admin ui must be known. Note that one token can be used for multiple records of the same type to update at once. Here're some examples how to update. For sure you substitute localhost:7777 according to your installation and 123 with the token configured on the record to update.

```
# IP autodetected (Note that this may cause trouble on dual stack IPv4+IPv6 setups):
http://localhost:7777/api/simple/TOKEN/

# IP explicitly specified:
http://localhost:7777/api/simple/TOKEN/127.0.0.1

# IPv4+IPv6 "combo-update" (A and AAAA record with same token required):
http://localhost:7777/api/simple/TOKEN/127.0.0.1,2001:0db8:85a3:0000:0000:8a2e:0370:7334

# Here's an example using wget/curl with IPv4 explicitly set:
wget --inet4-only -qO - http://localhost:7777/api/simple/TOKEN/
curl -4 http://localhost:7777/api/simple/TOKEN/

# As a result you'll get a response listing updated records in JSON format:
[
{"zone" : "example.com.", "record" : "www1", "type" : "A", "changed" : false, "value" : "127.0.0.1"},
{"zone" : "example.com.", "record" : "www2", "type" : "A", "changed" : false, "value" : "127.0.0.1"}
]
```


### LeGo HttpReq API
This API is specified by the [LeGo Library/CliTool](https://go-acme.github.io/lego/dns/httpreq/). It may be used for any kind of record updates but is used especially to update `_acme-challenge` TXT records to solve [LetsEncrypt/ACME DNS challenges](https://letsencrypt.org/docs/challenge-types/#dns-01-challenge) with clients using this library like [Traefik Proxy](https://go-acme.github.io/lego/dns/httpreq/). Check all those links for more information and detailed setup instructions.


Here's an example on how it could look like to get a `*.example.com` wildcard certificate with LimboDNS, Traefik and Lets Encrypt, expecting LimboDNS itself and everything else set up properly before ;) :

```
# Create the zone/record in LimboDNS:
Zone: example.com.
Record name: _acme-challenge
Record type: TXT
Record ttl: 0
Record token: secrettoken

# Configure LeGo (in Traefik via environment variables):
HTTPREQ_ENDPOINT=https://your.limbodns.domain/api/lego
HTTPREQ_USERNAME=token       <-- This is always the keyword "token"
HTTPREQ_PASSWORD=secrettoken <-- This is the token defined above in LimboDNS

# Configure Traefik cert resolver:
[certificatesResolvers.ledns.acme]
  email = "my@email.com"
  storage = "/acme-dns.json"
  [certificatesResolvers.ledns.acme.dnsChallenge]
    provider = "httpreq"

# Configure a Traefik service (done here via docker compose labels):
  myservice:
    image: docker-image-simple-webserver:latest
    labels:
      - traefik.enable=true
      - traefik.http.services.myservice.loadbalancer.server.port=80
      - traefik.http.routers.myservice-https.rule=Host(`www.example.com`)
      - traefik.http.routers.myservice-https.entrypoints=mySecureEntryPoint
      - traefik.http.routers.myservice-https.tls=true
      - traefik.http.routers.myservice-https.tls.certresolver=ledns
      - traefik.http.routers.myservice-https.tls.domains[0].main=*.example.com
      - traefik.http.routers.myservice-https.tls.domains[0].sans=www.example.com
```



## Links:
* [GitHub, Sources](https://github.com/thomaskuh/limbodns)
* [Docker](https://hub.docker.com/r/limbomedia/limbodns/)

## Thanks:
Thanks to the great libraries used in this project:
* [http://www.dnsjava.org/](http://www.dnsjava.org/)
* [https://www.eclipse.org/jetty/](https://www.eclipse.org/jetty/)
