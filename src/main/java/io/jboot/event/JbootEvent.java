/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.event;

import java.io.Serializable;

public class JbootEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long timestamp;
    private String action;
    private Object data;

    public JbootEvent(String action, Object data) {
        this.action = action;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    public <M> M getData() {
        return (M) data;
    }

    public String getAction() {
        return action;
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    @Override
    public String toString() {
        return "JbootEvent [timestamp=" + timestamp + ", action=" + action + ", data=" + data + "]";
    }

}
