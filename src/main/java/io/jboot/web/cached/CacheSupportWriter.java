/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.web.cached;

import java.io.*;
import java.util.Formatter;
import java.util.Locale;

public class CacheSupportWriter extends PrintWriter {

    final PrintWriter proxy;
    final StringWriter cached;

    private Formatter formatter;

    public CacheSupportWriter(PrintWriter proxy) {
        super(proxy);
        this.proxy = proxy;
        this.cached = new StringWriter();
    }

    @Override
    public void flush() {
        proxy.flush();
        cached.flush();
    }

    @Override
    public void close() {
        proxy.close();
        try {
            cached.close();
        } catch (IOException e) {
        }
    }

    @Override
    public boolean checkError() {
        return proxy.checkError();
    }


    @Override
    public void write(int c) {
        proxy.write(c);
        cached.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        proxy.write(buf, off, len);
        cached.write(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        proxy.write(buf);
        try {
            cached.write(buf);
        } catch (IOException e) {
        }
    }

    @Override
    public void write(String s, int off, int len) {
        proxy.write(s, off, len);
        cached.write(s, off, len);
    }

    @Override
    public void write(String s) {
        proxy.write(s);
        cached.write(s);
    }

    @Override
    public void print(boolean b) {
        proxy.print(b);
        cached.write(b ? "true" : "false");
    }

    @Override
    public void print(char c) {
        proxy.print(c);
        cached.write(c);
    }

    @Override
    public void print(int i) {
        proxy.print(i);
        cached.write(i);
    }

    @Override
    public void print(long l) {
        proxy.print(l);
        cached.write(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        proxy.print(f);
        cached.write(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        proxy.print(d);
        cached.write(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        proxy.print(s);
        try {
            cached.write(s);
        } catch (IOException e) {
        }
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        proxy.print(s);
        cached.write(s);
    }

    @Override
    public void print(Object obj) {
        proxy.print(obj);
        cached.write(String.valueOf(obj));
    }

    @Override
    public void println() {
        proxy.println();
        cached.write("\n");
    }

    @Override
    public void println(boolean x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(char x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(int x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(long x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(float x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(double x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(char[] x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(String x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public void println(Object x) {
        proxy.println(x);
        cached.write(x + "\n");
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
//        proxy.printf(format, args);
        format(format, args);
        return this;
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
//        proxy.printf(l, format, args);
        format(l, format, args);
        return this;
    }

    @Override
    public PrintWriter format(String format, Object... args) {
        proxy.format(format, args);

        if ((formatter == null)
                || (formatter.locale() != Locale.getDefault())) {
            formatter = new Formatter(cached);
        }
        formatter.format(Locale.getDefault(), format, args);
        return this;
    }

    @Override
    public PrintWriter format(Locale l, String format, Object... args) {
        proxy.format(l, format, args);

        if ((formatter == null) || (formatter.locale() != l)) {
            formatter = new Formatter(cached, l);
        }
        formatter.format(l, format, args);
        return this;
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        proxy.append(csq);
        cached.append(csq);
        return this;
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        proxy.append(csq, start, end);
        cached.append(csq, start, end);
        return this;
    }

    @Override
    public PrintWriter append(char c) {
        proxy.append(c);
        cached.append(c);
        return this;
    }

    public String getWriterString() {
        return cached.toString();
    }
}
