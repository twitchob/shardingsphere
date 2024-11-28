#!/bin/bash

# 确保脚本出错时终止
set -e

# 检查输入参数
if [ "$#" -lt 1 ]; then
  echo "Usage: $0 {help|pack|docker|build}"
  exit 1
fi

# 解析命令
COMMAND=$1

# 定义目标目录
BUILD_DIR="distribution/proxy"

# 函数定义

# 显示帮助信息
function show_help() {
    echo "Usage: $0 {help|pack|docker|build}"
    echo
    echo "Commands:"
    echo "  help      Show this help message and exit"
    echo "  pack      Build the project (mvn clean install) with specific options"
    echo "  docker    Build the Docker image for the project"
    echo "  build     Perform the full build process: update code, pack, and build Docker"
    echo
    echo "Examples:"
    echo "  $0 help            Display this help message"
    echo "  $0 pack            Execute the Maven build process"
    echo "  $0 docker          Build the Docker image"
    echo "  $0 build           Pull the latest code, build the project, and create the Docker image"
}

# 拉取最新代码
function update_code() {
    echo "Pulling latest code..."
    gl || git pull # 使用 gl 别名或 git pull
    echo "Code update completed."
}

# 打包命令
function run_pack() {
    echo "Running pack command..."
    ./mvnw clean install -Prelease -T1C -DskipTests -Djacoco.skip=true -Dcheckstyle.skip=true -Drat.skip=true -Dmaven.javadoc.skip=true -B
    echo "Pack completed successfully."
}

# Docker 镜像构建命令
function run_docker() {
    echo "Running docker command..."
    # 检查目标目录是否存在
    if [ ! -d "$BUILD_DIR" ]; then
        echo "Error: Directory $BUILD_DIR does not exist."
        exit 1
    fi
    # 切换到目标目录并执行 Maven 命令
    cd "$BUILD_DIR"
    ../../mvnw clean package -Prelease,docker
    echo "Docker build completed successfully in $BUILD_DIR."
    cd - > /dev/null
}

# 新增的 build 命令，依次拉取代码、打包和构建 Docker
function run_build() {
    update_code
    run_pack
    run_docker
}

# 根据命令执行不同的操作
case $COMMAND in
  help)
    show_help
    ;;
  pack)
    run_pack
    ;;
  docker)
    run_docker
    ;;
  build)
    echo "Running build command (update code + pack + docker)..."
    run_build
    ;;
  *)
    echo "Invalid command: $COMMAND"
    echo "Use '$0 help' for more information."
    exit 1
    ;;
esac