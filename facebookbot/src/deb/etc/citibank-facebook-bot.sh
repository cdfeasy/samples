#! /bin/sh
MAIN_CLASS=facebookbot.HttpServer
APP_CONFIG=file:/etc/citibank-facebook-bot/config.xml
LOG_CONFIG=/etc/citibank-facebook-bot/logback.xml
BIN_DIR=/usr/share/citibank-facebook-bot/lib/

# ***********************************************
# ***********************************************

ARGS="-DBotConfig=${APP_CONFIG} -Dlogback.configurationFile=${LOG_CONFIG}"

exec java $ARGS -cp "$BIN_DIR/*" $MAIN_CLASS
