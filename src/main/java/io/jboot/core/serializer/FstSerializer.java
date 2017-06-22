///**
// * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
// * <p>
// * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.jboot.core.serializer;
//
//import de.ruedigermoeller.serialization.FSTObjectInput;
//import de.ruedigermoeller.serialization.FSTObjectOutput;
//import net.sf.ehcache.CacheException;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//
//public class FstSerializer implements ISerializer {
//
//    @Override
//    public byte[] serialize(Object obj) {
//        if (obj == null)
//            return null;
//        ByteArrayOutputStream out = null;
//        FSTObjectOutput fout = null;
//        try {
//            out = new ByteArrayOutputStream();
//            fout = new FSTObjectOutput(out);
//            fout.writeObject(obj);
//            return out.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fout != null)
//                try {
//                    fout.close();
//                } catch (IOException e) {
//                }
//        }
//        return null;
//    }
//
//
//    @Override
//    public Object deserialize(byte[] bytes) {
//        if (bytes == null || bytes.length == 0)
//            return null;
//        FSTObjectInput in = null;
//        try {
//            in = new FSTObjectInput(new ByteArrayInputStream(bytes));
//            return in.readObject();
//        } catch (ClassNotFoundException e) {
//            throw new CacheException(e);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null)
//                try {
//                    in.close();
//                } catch (IOException e) {
//                }
//        }
//        return null;
//    }
//
//
//}
