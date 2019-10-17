# https://github.com/elastic/elasticsearch-docker
FROM docker.elastic.co/elasticsearch/elasticsearch-oss:6.6.2

COPY config/ config/
COPY bin/ bin/
COPY my-entrypoint.sh /usr/local/bin/my-entrypoint.sh
RUN chmod a+x /usr/local/bin/my-entrypoint.sh

# Add your elasticsearch plugins setup here
# Example: RUN elasticsearch-plugin install analysis-icu

#  This custom entrypoint script is used instead of 
# the original's /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["bash","-c","my-entrypoint.sh"]