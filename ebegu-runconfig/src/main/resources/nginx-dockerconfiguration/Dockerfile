# Achtung: Es waere eigentlich vorzuziehen das ngnix image von  FROM nginx:1.9.14 zu verwenden. Dieses funktioniert aber
# leider nicht mit proxy_pass. Der genaue Grund dafuer konnte auch nach laegerer suche nicht ermittelt werden.
# Stattdessen wird nun auf dem image von debian aufgesetzt und ein ngnix installiert
# Ein gutes post dazu ist hier http://stackoverflow.com/questions/27912917/how-to-configure-docker-port-mapping-to-use-nginx-as-an-upstream-proxy/29201705#29201705

FROM debian
RUN apt-get update && apt-get install -y nginx
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log
RUN rm -rf /etc/nginx/sites-enabled/default

# Define mountable directories.
VOLUME ["/etc/nginx/sites-enabled", "/etc/nginx/certs", "/etc/nginx/conf.d", "/var/log/nginx"]


#Applikation einfuegen
COPY ebegu-web.tgz /usr/share/nginx/stage/ebegu-web.tgz
RUN mkdir /usr/share/nginx/html/web
RUN tar -xzvf /usr/share/nginx/stage/ebegu-web.tgz  --directory /usr/share/nginx/html/web && \
rm -r /usr/share/nginx/stage/
COPY /conf/missingapp.html  /usr/share/nginx/html/web/missingapp.html

# Define working directory.
WORKDIR /etc/nginx
EXPOSE 80 443
COPY conf/ngnix.conf /etc/nginx/sites-enabled/ebeguapp.ch
CMD ["nginx", "-g", "daemon off;"]






