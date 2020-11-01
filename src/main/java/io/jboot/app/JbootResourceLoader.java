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
package io.jboot.app;

import io.jboot.app.config.JbootConfigManager;
import io.jboot.utils.FileScanner;
import io.jboot.utils.StrUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class JbootResourceLoader {

    private String resourcePathName;
    private List<FileScanner> scanners = new ArrayList<>();

    public JbootResourceLoader() {
        String pathName = JbootConfigManager.me().getConfigValue("jboot.app.resourcePathName");
        this.resourcePathName = StrUtil.obtainDefault(pathName, "webapp");
    }

    public JbootResourceLoader(String resourcePathName) {
        this.resourcePathName = StrUtil.requireNonBlank(resourcePathName, "Resource path name must not be blank.");
    }


    public void start() {
        try {

            URL url = JbootResourceLoader.class.getClassLoader().getResource("");
            if (url == null) {
                return;
            }

            String classPath = url.toURI().getPath();
            File srcRootPath = new File(classPath, "../..").getCanonicalFile();

            if (new File(srcRootPath.getParent(), "pom.xml").exists()) {
                srcRootPath = srcRootPath.getParentFile();
            }

            List<File> resourcesDirs = new ArrayList<>();
            findResourcesPath(srcRootPath, resourcesDirs);

            for (File resourcesDir : resourcesDirs) {
                startNewScanner(resourcesDir.getCanonicalFile(), classPath);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> JbootResourceLoader.this.stop()));
            System.err.println("JbootResourceLoader started, Watched resource path name : " + resourcePathName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        scanners.forEach(fileScanner -> fileScanner.stop());
        System.out.println("JbootResourceLoader stoped ......");
    }

    private void findResourcesPath(File root, List<File> resourcesDirs) {
        File[] dirs = root.listFiles(pathname -> pathname.isDirectory());
        if (dirs == null || dirs.length == 0) {
            return;
        }
        for (File dir : dirs) {

            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }

            if (dir.getName().equals(resourcePathName)
                    && parentFile.getName().equals("main")) {
                resourcesDirs.add(dir);
            } else {
                findResourcesPath(dir, resourcesDirs);
            }
        }
    }


    private void startNewScanner(File resourcesDir, String classPath) throws IOException {
        // main/webapp/
        String path = "main" + File.separator + resourcePathName + File.separator;
        FileScanner scanner = new FileScanner(resourcesDir.getCanonicalPath(), 5) {
            @Override
            public void onChange(String action, String file) {
                if (FileScanner.ACTION_INIT.equals(action)) {
                    return;
                }

                int indexOf = file.indexOf(path);

                File target = new File(classPath, resourcePathName + File.separator + file.substring(indexOf + path.length()));
                System.err.println("JbootResourceLoader " + action + " : " + target);

                //文件删除
                if (FileScanner.ACTION_DELETE.equals(action)) {
                    target.delete();
                }
                //新增文件 或 修改文件
                else {
                    try {
                        FileUtils.copyFile(new File(file), target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        scanner.start();
        scanners.add(scanner);
    }
}
