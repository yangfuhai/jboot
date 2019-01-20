/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public abstract class FileScanner {

    public static final String ACTION_INIT = "init";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_UPDATE = "update";

    private Timer timer;
    private TimerTask task;
    private String rootDir;
    private int interval;
    private boolean running = false;
    private boolean inited = false;

    private final Map<String, TimeSize> preScan = new HashMap<>();
    private final Map<String, TimeSize> curScan = new HashMap<>();

    public FileScanner(String rootDir, int interval) {
        if (rootDir == null)
            throw new IllegalArgumentException("The parameter rootDir can not be null.");
        this.rootDir = rootDir;
        if (interval <= 0)
            throw new IllegalArgumentException("The parameter interval must more than zero.");
        this.interval = interval;
    }

    public abstract void onChange(String action, String file);

    protected void working() {
        if (!rootDir.contains(";")) {
            scan(new File(rootDir));
        } else {
            String[] paths = rootDir.split(";");
            for (String path : paths) {
                scan(new File(path));
            }
        }

        compare();

        preScan.clear();
        preScan.putAll(curScan);
        curScan.clear();
    }

    protected void scan(File file) {
        if (file == null || !file.exists())
            return;

        if (file.isFile()) {
            try {
                curScan.put(file.getCanonicalPath(), new TimeSize(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                for (File f : fs) {
                    scan(f);
                }
            }
        }
    }

    protected void compare() {

        if (!inited) {
            for (Map.Entry<String, TimeSize> entry : curScan.entrySet()) {
                onChange(ACTION_INIT, entry.getKey());
            }
            inited = true;
            return;
        }

        for (Map.Entry<String, TimeSize> entry : curScan.entrySet()) {
            if (preScan.get(entry.getKey()) == null)
                onChange(ACTION_ADD, entry.getKey());
        }

        for (Map.Entry<String, TimeSize> entry : preScan.entrySet()) {
            if (curScan.get(entry.getKey()) == null)
                onChange(ACTION_DELETE, entry.getKey());
        }


        for (Map.Entry<String, TimeSize> entry : curScan.entrySet()) {
            TimeSize pre = preScan.get(entry.getKey());
            if (pre != null && !pre.equals(entry.getValue()))
                onChange(ACTION_UPDATE, entry.getKey());
        }

    }

    public void start() {
        if (!running) {
            timer = new Timer("Jboot-File-Scanner", true);
            task = new TimerTask() {
                public void run() {
                    working();
                }
            };
            timer.schedule(task, 0, 1000L * interval);
            running = true;
        }
    }

    public void stop() {
        if (running) {
            timer.cancel();
            task.cancel();
            running = false;
        }
    }

    class TimeSize {

        final long time;
        final long size;

        public TimeSize(File file) {
            this.time = file.lastModified();
            this.size = file.length();
        }


        public int hashCode() {
            return (int) (time ^ size);
        }

        public boolean equals(Object o) {
            if (o instanceof TimeSize) {
                TimeSize ts = (TimeSize) o;
                return ts.time == this.time && ts.size == this.size;
            }
            return false;
        }

        @Override
        public String toString() {
            return "TimeSize{" +
                    "time=" + time +
                    ", size=" + size +
                    '}';
        }
    }
}



