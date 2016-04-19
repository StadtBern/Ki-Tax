FROM nginx:1.9.14

#Applikation einfuegen
COPY ebegu-web.tgz /usr/share/nginx/stage/ebegu-web.tgz

RUN mkdir /usr/share/nginx/html/web
RUN tar -xzvf /usr/share/nginx/stage/ebegu-web.tgz  --directory /usr/share/nginx/html/web && \
rm -r /usr/share/nginx/stage/


#Konfigurationsfile kopieren
COPY nginx.conf /etc/nginx/nginx.conf





