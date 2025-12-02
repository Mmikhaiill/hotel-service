# WildFly Dockerfile with MariaDB driver and datasource configuration
FROM quay.io/wildfly/wildfly:30.0.1.Final-jdk17

# Environment variables
ENV WILDFLY_USER=admin
ENV WILDFLY_PASSWORD=admin123
ENV DB_HOST=mariadb
ENV DB_PORT=3306
ENV DB_NAME=hoteldb
ENV DB_USER=hotel
ENV DB_PASSWORD=hotel123

# Copy the MariaDB driver module
COPY --chown=jboss:jboss module.xml /opt/jboss/wildfly/modules/system/layers/base/org/mariadb/jdbc/main/
COPY --chown=jboss:jboss mariadb-java-client-3.3.2.jar /opt/jboss/wildfly/modules/system/layers/base/org/mariadb/jdbc/main/

# Copy configuration script
COPY --chown=jboss:jboss configure-wildfly.cli /opt/jboss/wildfly/

# Add management user
RUN /opt/jboss/wildfly/bin/add-user.sh ${WILDFLY_USER} ${WILDFLY_PASSWORD} --silent

# Configure WildFly using CLI
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/configure-wildfly.cli

# Expose ports
# 8080 - HTTP
# 9990 - Management
# 8787 - Debug
EXPOSE 8080 9990 8787

# Start WildFly in standalone mode with full profile
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml"]
