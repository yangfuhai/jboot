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
package io.jboot.codegen.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jboot.Jboot;
import io.jboot.config.JbootProperties;
import io.jboot.db.datasource.DatasourceConfig;

import java.util.List;

public class JbootModelGenerator {


    public static void main(String[] args) {

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jbootdemo");
        Jboot.setBootArg("jboot.datasource.user", "root");


        String modelPackage = "io.jboot.codegen.test";
        run(modelPackage);
    }


    public static void run(String modelPackage) {
        new JbootModelGenerator(modelPackage).doGenerate();
    }


    private final String basePackage;


    public JbootModelGenerator(String basePackage) {
        this.basePackage = basePackage;
    }


    public void doGenerate() {

        String modelPackage = basePackage + ".model";
        String baseModelPackage = basePackage + ".model.base";

        String modelDir = PathKit.getWebRootPath() + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...");
        System.out.println("generate dir:" + modelDir);

        DatasourceConfig datasourceConfig = JbootProperties.get("jboot.datasource", DatasourceConfig.class);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(datasourceConfig.getUrl());
        config.setUsername(datasourceConfig.getUser());
        config.setPassword(datasourceConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("com.mysql.jdbc.Driver");

        HikariDataSource dataSource = new HikariDataSource(config);


        List<TableMeta> tableMetaList = new MetaBuilder(dataSource).build();

        new JbootBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new JbootModelnfoGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        System.out.println("generate finished !!!");

    }


}
