##
## Copyright 2019 WeBank
##
## Licensed under the Apache License, Version 2.0 (the "License");
##  you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
## http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##

#!/usr/bin/env bash
#
# @author howeye
# @createtime 2018-12-25
#

# Verify that JAVA_HOME set - does not verify that it's set to a meaningful
# value.
verify_java_home() {
  if [ -z "$JAVA_HOME" ]; then
    cat 1>&2 <<EOF
+======================================================================+
|      Error: JAVA_HOME is not set and Java could not be found         |
+----------------------------------------------------------------------+
EOF
    exit 1
  fi

  echo "JAVA_HOME=$JAVA_HOME"
}

bin=`dirname ${BASH_SOURCE-$0}`
bin=`cd "$bin"; pwd`

verify_java_home

# find correct directory
if [ ! -d "${bin}/../logs" ]; then
    mkdir ${bin}/../logs
fi

# 获取系统总内存（单位：MB）
TOTAL_MEM=$(free -m | awk '/Mem:/ {print $2}')
# 计算推荐配置（可调整比例）
XMX_PERCENT=25  # 最大内存占比
XMS_PERCENT=50  # 初始内存占最大内存的比例
# 计算具体数值（保持MB单位）
XMX_MB=$(( TOTAL_MEM * XMX_PERCENT / 100 ))
XMS_MB=$(( XMX_MB * XMS_PERCENT / 100 ))
XMX_MB=$(( XMX_MB < 2048 ? 2048 : XMX_MB ))  # 保证至少2048MB
XMS_MB=$(( XMS_MB < 256 ? 256 : XMS_MB ))  # 保证至少256MB

# 单位转换函数
format_memory() {
  local value=$1
  if [ $value -ge 1024 ]; then
    echo "$((value / 1024))G"
  else
    echo "${value}M"
  fi
}

# 格式化参数
XMX=$(format_memory $XMX_MB)
XMS=$(format_memory $XMS_MB)

echo "总内存: ${TOTAL_MEM}MB"
echo "原始计算：XMX=${XMX_MB}MB, XMS=${XMS_MB}MB"
echo "格式化为：Xms${XMS} Xmx${XMX}"

weapmEnabled=$1
envName=$2
nohup ${bin}/qualitis ${weapmEnabled} ${envName} ${XMS} ${XMX} > /dev/null 2>&1 &

if [ $? != 0 ]; then
    echo "Failed to start Qualitis System" 1>&2
    exit 1
else
    echo "Succeed to start Qualitis System"
fi



