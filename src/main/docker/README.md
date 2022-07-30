LimboDNS, officially dockerized!
======
Your nice and simple authoritative Domain Name Server allowing you to run your very own DynDNS service.

Startup (as deamon):

    docker run -d -p 7777:7777 -p 53:53/tcp -p 53:53/udp -v DATA-DIR:/data limbomedia/limbodns

Startup (interactive):

    docker run -i -t -p 7777:7777 -p 53:53/tcp -p 53:53/udp -v DATA-DIR:/data limbomedia/limbodns


## Links:
* [Setup instructions and download](https://limbomedia.net/etc/limbodns)
* [GitHub, Sources](https://github.com/thomaskuh/limbodns)
* [Docker](https://hub.docker.com/r/limbomedia/limbodns/)