#!/bin/bash

# 确保脚本出错时终止
set -e

# 检查输入参数
if [ "$#" -lt 1 ]; then
  echo "Usage: $0 {pack|build}"
  exit 1
fi

# 解析命令
COMMAND=$1

# 定义目标目录
BUILD_DIR="distribution/proxy"

# 根据命令执行不同的操作
case $COMMAND in
  pack)
    echo "Running pack command..."
    ./mvnw clean install -Prelease -T1C -DskipTests -Djacoco.skip=true -Dcheckstyle.skip=true -Drat.skip=true -Dmaven.javadoc.skip=true -B
    echo "Pack completed successfully."
    ;;
  build)
    echo "Running build command..."
    # 检查目标目录是否存在
    if [ ! -d "$BUILD_DIR" ]; then
      echo "Error: Directory $BUILD_DIR does not exist."
      exit 1
    fi
    # 切换到目标目录并执行 Maven 命令
    cd "$BUILD_DIR"
    ../../mvnw clean package -Prelease,docker
    echo "Build completed successfully in $BUILD_DIR."
    ;;
  *)
    echo "Invalid command: $COMMAND"
    echo "Usage: $0 {pack|build}"
    exit 1
    ;;
esac