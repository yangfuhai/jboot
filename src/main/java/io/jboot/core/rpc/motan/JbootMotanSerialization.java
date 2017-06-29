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
package io.jboot.core.rpc.motan;

import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.core.extension.SpiMeta;
import io.jboot.Jboot;

import java.io.IOException;


@SpiMeta(name = "jboot")
public class JbootMotanSerialization implements Serialization {
    @Override
    public byte[] serialize(Object obj) throws IOException {
        return Jboot.getSerializer().serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
        return (T) Jboot.getSerializer().deserialize(bytes);
    }
}
