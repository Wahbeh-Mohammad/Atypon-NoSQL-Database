#!/bin/bash

build_js_app () {
    echo "Building Node App { $1 } .."
    cd "$(pwd)/$1"
    npm run build
    cd ".."
}

build_java_app () {
    echo "Building Java App { $1 } .."
    cd "$(pwd)/$1"
    mvn clean install
    cd ".."
}

# Directories
ADMIN_PANEL_DIR="admin_panel/"
BLOG_APP_DIR="demo_app/"
ADMIN_CONTROLLER_DIR="admin_controller/"
READ_CONTROLLER_DIR="read_controller/"

build_js_app $ADMIN_PANEL_DIR
build_js_app $BLOG_APP_DIR
build_java_app $ADMIN_CONTROLLER_DIR
build_java_app $READ_CONTROLLER_DIR