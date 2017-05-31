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
package io.jboot.db.model;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;


public class JbootVoModel extends HashMap {

    public JbootVoModel(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public JbootVoModel(int initialCapacity) {
        super(initialCapacity);
    }

    public JbootVoModel() {
    }

    public JbootVoModel(Map m) {
        super(m);
    }

    /**
     * Put key value pair to the model without check attribute name.
     */
    public JbootVoModel set(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * Put map to the model without check attribute name.
     */
    public JbootVoModel set(Map<String, Object> map) {
        super.putAll(map);
        return this;
    }

    /**
     * Put record to the model without check attribute name.
     */
    public JbootVoModel set(Record record) {
        super.putAll(record.getColumns());
        return this;
    }

    /**
     * Convert model to record.
     */
    public Record toRecord() {
        return new Record().setColumns(this);
    }

    /**
     * Get attribute of any mysql type
     */
    public <T> T get(String attr) {
        return (T) (super.get(attr));
    }

    /**
     * Get attribute of any mysql type. Returns defaultValue if null.
     */
    public <T> T get(String attr, Object defaultValue) {
        Object result = super.get(attr);
        return (T) (result != null ? result : defaultValue);
    }

    /**
     * Get attribute of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
     */
    public String getStr(String attr) {
        return (String) super.get(attr);
    }

    /**
     * Get attribute of mysql type: int, integer, tinyint(n) n than 1, smallint, mediumint
     */
    public Integer getInt(String attr) {
        return (Integer) super.get(attr);
    }

    /**
     * Get attribute of mysql type: bigint, unsign int
     */
    public Long getLong(String attr) {
        return (Long) super.get(attr);
    }

    /**
     * Get attribute of mysql type: unsigned bigint
     */
    public java.math.BigInteger getBigInteger(String attr) {
        return (java.math.BigInteger) super.get(attr);
    }

    /**
     * Get attribute of mysql type: date, year
     */
    public java.util.Date getDate(String attr) {
        return (java.util.Date) super.get(attr);
    }

    /**
     * Get attribute of mysql type: time
     */
    public java.sql.Time getTime(String attr) {
        return (java.sql.Time) super.get(attr);
    }

    /**
     * Get attribute of mysql type: timestamp, datetime
     */
    public java.sql.Timestamp getTimestamp(String attr) {
        return (java.sql.Timestamp) super.get(attr);
    }

    /**
     * Get attribute of mysql type: real, double
     */
    public Double getDouble(String attr) {
        return (Double) super.get(attr);
    }

    /**
     * Get attribute of mysql type: float
     */
    public Float getFloat(String attr) {
        return (Float) super.get(attr);
    }

    /**
     * Get attribute of mysql type: bit, tinyint(1)
     */
    public Boolean getBoolean(String attr) {
        return (Boolean) super.get(attr);
    }

    /**
     * Get attribute of mysql type: decimal, numeric
     */
    public java.math.BigDecimal getBigDecimal(String attr) {
        return (java.math.BigDecimal) super.get(attr);
    }

    /**
     * Get attribute of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
     */
    public byte[] getBytes(String attr) {
        return (byte[]) super.get(attr);
    }

    /**
     * Get attribute of any type that extends from Number
     */
    public Number getNumber(String attr) {
        return (Number) super.get(attr);
    }





}
