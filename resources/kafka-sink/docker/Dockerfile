FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY target/*-runner /work/application
#COPY in_container_curl.sh /work/in_container_curl.sh
#RUN chmod +x in_container_curl.sh
RUN chmod 775 /work && chmod 755 /work/application
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
