package io.jboot.test;

import io.jboot.app.JbootApplication;
import io.jboot.core.listener.JbootAppListener;
import io.jboot.utils.ClassScanner;

public class ClassScannerTester implements JbootAppListener {

    public static void main(String[] args) {

//        ClassScanner.addUnscanJarPrefix("encoder-");
//        ClassScanner.addUnscanJarPrefix("reflections-");

//        JbootApplication.setBootArg("jboot.app.scanner.unScanJarPrefix","encoder-,reflections-");
        ClassScanner.setPrintScannerInfoEnable(true);
        JbootApplication.run(args);
    }

    @Override
    public void onStart() {

    }
}
