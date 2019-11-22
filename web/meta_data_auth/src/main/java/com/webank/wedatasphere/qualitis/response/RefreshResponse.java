/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.response;

import java.util.List;

/**
 * @author howeye
 */
public class RefreshResponse {

    private List<String> add;
    private List<String> remove;

    public RefreshResponse(List<String> add, List<String> remove) {
        this.add = add;
        this.remove = remove;
    }

    public List<String> getAdd() {
        return add;
    }

    public void setAdd(List<String> add) {
        this.add = add;
    }

    public List<String> getRemove() {
        return remove;
    }

    public void setRemove(List<String> remove) {
        this.remove = remove;
    }
}
