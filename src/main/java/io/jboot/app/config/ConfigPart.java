/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.app.config;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/24
 */
public class ConfigPart {

    private int start = 0;
    private int end = 0;
    private int keyValueIndexOf = 0;
    private StringBuilder sb = null;

    public String getKey(){
        if (sb == null){
            return null;
        }
        return keyValueIndexOf > 0 ? sb.substring(0,keyValueIndexOf - 1) : sb.toString();
    }

    public String getDefaultValue(){
        if (sb == null){
            return "";
        }
        return keyValueIndexOf > 0 ? sb.substring(keyValueIndexOf).trim(): "";
    }

    public boolean isOk(){
        return start > end && sb != null;
    }

    public void append(char c){
        if(sb == null){
            sb = new StringBuilder();
        }
        sb.append(c);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getKeyValueIndexOf() {
        return keyValueIndexOf;
    }

    public void setKeyValueIndexOf(int keyValueIndexOf) {
        this.keyValueIndexOf = keyValueIndexOf;
    }

    public String getPartString(){
        return sb == null ? "${}" : "${" + sb.toString() +"}";
    }
}
