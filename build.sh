#!/bin/bash

# Build script for Hotel Service
set -e

echo "=== Hotel Service Build Script ==="

# Check for required tools
command -v mvn >/dev/null 2>&1 || { echo "Maven is required but not installed. Aborting." >&2; exit 1; }
command -v java >/dev/null 2>&1 || { echo "Java is required but not installed. Aborting." >&2; exit 1; }

# Download MariaDB driver for WildFly if not exists
MARIADB_DRIVER="docker/wildfly/mariadb-java-client-3.3.2.jar"
if [ ! -f "$MARIADB_DRIVER" ]; then
    echo "Downloading MariaDB JDBC driver..."
    curl -L -o "$MARIADB_DRIVER" \
        "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.3.2/mariadb-java-client-3.3.2.jar"
    echo "MariaDB driver downloaded."
fi

# Build all modules
echo "Building all modules..."
mvn clean package -DskipTests

echo ""
echo "=== Build completed successfully ==="
echo ""
echo "Artifacts:"
echo "  - EAR: hotel-ear/target/hotel-app.ear"
echo "  - Quarkus: hotel-rest/target/quarkus-app/"
echo ""
echo "To start with Docker Compose:"
echo "  docker-compose up --build"
echo ""
echo "To start in dev mode:"
echo "  1. docker-compose -f docker-compose.dev.yml up -d"
echo "  2. Deploy hotel-app.ear to WildFly"
echo "  3. cd hotel-rest && mvn quarkus:dev"
