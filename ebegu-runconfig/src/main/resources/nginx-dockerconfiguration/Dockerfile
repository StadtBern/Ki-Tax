FROM debian
RUN apt-get update && apt-get install -y nginx
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log
RUN rm -rf /etc/nginx/sites-enabled/default

#Applikation einfuegen
COPY ebegu-web.tgz /usr/share/nginx/stage/ebegu-web.tgz
RUN mkdir /usr/share/nginx/html/web
RUN tar -xzvf /usr/share/nginx/stage/ebegu-web.tgz  --directory /usr/share/nginx/html/web && \
rm -r /usr/share/nginx/stage/
COPY /conf/missingapp.html  /usr/share/nginx/html/web/missingapp.html

EXPOSE 80 443
COPY conf/ngnix.conf /etc/nginx/sites-enabled/ebeguapp.ch
CMD ["nginx", "-g", "daemon off;"]






