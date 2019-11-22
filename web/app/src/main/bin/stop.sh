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
# @createtime 2018-12-26
#

PIDS=$(ps -ef | grep -v grep | grep "com.webank.wedatasphere.qualitis.QualitisServer" | awk '{print $2}')

if [ -z "$PIDS" ]; then
    echo "No Qualitis System to stop" 1>&2
    exit 1
else
    kill -s TERM $PIDS

    echo "Succeed to send signal [SIGTERM] to Qualitis System"
fi