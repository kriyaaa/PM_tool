#!/usr/bin/env sh
set -eu

if [ -z "${SPRING_DATASOURCE_URL:-}" ] && [ -n "${DB_HOST:-}" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
fi

if [ -z "${SPRING_DATASOURCE_USERNAME:-}" ] && [ -n "${DB_USER:-}" ]; then
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
fi

if [ -z "${SPRING_DATASOURCE_PASSWORD:-}" ] && [ -n "${DB_PASSWORD:-}" ]; then
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
fi

exec java -jar app.jar
