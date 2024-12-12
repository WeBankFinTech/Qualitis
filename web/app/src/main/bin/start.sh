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
export QUALITIS_LOG_DIR=${bin}/../logs

nohup ${bin}/qualitis >> ${QUALITIS_LOG_DIR}/qualitis.out.$(date +%Y%m%d%H%M%S) 2>&1 &

if [ $? != 0 ]; then
    echo "Failed to start Qualitis System" 1>&2
    exit 1
else
    echo "Succeed to start Qualitis System"
fi
