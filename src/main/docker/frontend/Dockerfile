# build environment
FROM node:12.4-alpine as build
WORKDIR /app
#ENV PATH /app/node_modules/.bin:$PATH
COPY onap-clamp-frontend/ /app/
RUN npm install --silent
RUN npm run build

FROM nginx:1.17.0-alpine

MAINTAINER "The Onap Team"
LABEL Description="This image contains Clamp frontend"

ARG http_proxy
ARG https_proxy
ENV HTTP_PROXY=$http_proxy
ENV HTTPS_PROXY=$https_proxy
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN addgroup onap && adduser -D -G onap clamp
RUN mkdir /var/log/onap
RUN chmod a+rwx /var/log/onap

COPY --from=build /app/build /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf
COPY onap-clamp-frontend/nginx/nginx.conf /etc/nginx/conf.d
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]