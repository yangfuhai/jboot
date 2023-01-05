package io.jboot.test.utils;

import io.jboot.utils.FileUtil;
import org.junit.Test;

public class FileUtilTest {

    @Test
    public void testGetSuffix(){
        String suffix = FileUtil.getSuffix("aaa/bbb/ccc.jpg");
        System.out.println(suffix);
    }
}
