#!/bin/bash

# ================================================================= #
# WCMS Backend Deployment Script
# ================================================================= #

APP_NAME=wcms-backend-0.0.1-SNAPSHOT.jar
LOG_FILE=app.log
PID_FILE=app.pid

usage() {
    echo "Usage: sh run.sh [start|stop|restart|status]"
    exit 1
}

# Check if application is running
is_exist() {
    if [ -f "$PID_FILE" ]; then
        pid=$(cat "$PID_FILE")
        # Check if process is actually running
        if ps -p $pid > /dev/null; then
            return 0
        fi
    fi
    return 1
}

# Start application
start() {
    if is_exist; then
        echo ">>> ${APP_NAME} is already running PID=${pid} <<<"
    else
        echo ">>> Starting ${APP_NAME} <<<"
        nohup java -Xms512m -Xmx1024m -jar $APP_NAME > $LOG_FILE 2>&1 &
        echo $! > $PID_FILE
        echo ">>> Start Success! PID=$! <<<"
        echo ">>> Tail log: tail -f $LOG_FILE <<<"
    fi
}

# Stop application
stop() {
    if is_exist; then
        echo ">>> Stopping ${APP_NAME} PID=${pid} <<<"
        kill $pid
        sleep 2
        if is_exist; then
            echo ">>> Force killing PID=${pid} <<<"
            kill -9 $pid
        fi
        rm -f $PID_FILE
        echo ">>> Stop Success! <<<"
    else
        echo ">>> ${APP_NAME} is not running <<<"
    fi
}

# Check status
status() {
    if is_exist; then
        echo ">>> ${APP_NAME} is running. PID is ${pid} <<<"
    else
        echo ">>> ${APP_NAME} is NOT running. <<<"
    fi
}

# Restart application
restart() {
    stop
    sleep 2
    start
}

# CLI routing
case "$1" in
    "start")
        start
        ;;
    "stop")
        stop
        ;;
    "status")
        status
        ;;
    "restart")
        restart
        ;;
    *)
        usage
        ;;
esac
