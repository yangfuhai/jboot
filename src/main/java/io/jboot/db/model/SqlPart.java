/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.db.model;


class SqlPart extends Column {

    private String sql;
    private Object para;
    private boolean withoutLink = false;

    public SqlPart(String sql) {
        this.sql = sql;
    }

    public SqlPart(String sql, Object para) {
        this.sql = sql;
        this.para = para;
    }

    public SqlPart(String sql, boolean withoutLink) {
        this.sql = sql;
        this.withoutLink = withoutLink;
    }

    public SqlPart(String sql, Object para, boolean withoutLink) {
        this.sql = sql;
        this.para = para;
        this.withoutLink = withoutLink;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object getPara() {
        return para;
    }

    public void setPara(Object para) {
        this.para = para;
    }

    public boolean isWithoutLink() {
        return withoutLink;
    }

    public void setWithoutLink(boolean withoutLink) {
        this.withoutLink = withoutLink;
    }

    @Override
    public boolean hasPara() {
        return para != null;
    }

    @Override
    public Object getValue() {
        return para;
    }
}
