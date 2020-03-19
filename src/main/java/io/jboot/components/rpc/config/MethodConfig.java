/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.components.rpc.config;

import org.apache.dubbo.config.ArgumentConfig;

import java.io.Serializable;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/19
 */
public class MethodConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The method name
     */
    private String name;

    /**
     * Stat
     */
    private Integer stat;

    /**
     * Whether to retry
     */
    private Boolean retry;

    /**
     * If it's reliable
     */
    private Boolean reliable;

    /**
     * Thread limits for method invocations
     */
    private Integer executes;

    /**
     * If it's deprecated
     */
    private Boolean deprecated;

    /**
     * Whether to enable sticky
     */
    private Boolean sticky;

    /**
     * Whether need to return
     */
    private Boolean isReturn;

    /**
     * Callback instance when async-call is invoked
     */
    private Object oninvoke;

    /**
     * Callback method when async-call is invoked
     */
    private String oninvokeMethod;

    /**
     * Callback instance when async-call is returned
     */
    private Object onreturn;

    /**
     * Callback method when async-call is returned
     */
    private String onreturnMethod;

    /**
     * Callback instance when async-call has exception thrown
     */
    private Object onthrow;

    /**
     * Callback method when async-call has exception thrown
     */
    private String onthrowMethod;

    /**
     * The method arguments
     */
    private List<ArgumentConfig> arguments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public Boolean getRetry() {
        return retry;
    }

    public void setRetry(Boolean retry) {
        this.retry = retry;
    }

    public Boolean getReliable() {
        return reliable;
    }

    public void setReliable(Boolean reliable) {
        this.reliable = reliable;
    }

    public Integer getExecutes() {
        return executes;
    }

    public void setExecutes(Integer executes) {
        this.executes = executes;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public Boolean getReturn() {
        return isReturn;
    }

    public void setReturn(Boolean aReturn) {
        isReturn = aReturn;
    }

    public Object getOninvoke() {
        return oninvoke;
    }

    public void setOninvoke(Object oninvoke) {
        this.oninvoke = oninvoke;
    }

    public String getOninvokeMethod() {
        return oninvokeMethod;
    }

    public void setOninvokeMethod(String oninvokeMethod) {
        this.oninvokeMethod = oninvokeMethod;
    }

    public Object getOnreturn() {
        return onreturn;
    }

    public void setOnreturn(Object onreturn) {
        this.onreturn = onreturn;
    }

    public String getOnreturnMethod() {
        return onreturnMethod;
    }

    public void setOnreturnMethod(String onreturnMethod) {
        this.onreturnMethod = onreturnMethod;
    }

    public Object getOnthrow() {
        return onthrow;
    }

    public void setOnthrow(Object onthrow) {
        this.onthrow = onthrow;
    }

    public String getOnthrowMethod() {
        return onthrowMethod;
    }

    public void setOnthrowMethod(String onthrowMethod) {
        this.onthrowMethod = onthrowMethod;
    }

    public List<ArgumentConfig> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentConfig> arguments) {
        this.arguments = arguments;
    }
}
