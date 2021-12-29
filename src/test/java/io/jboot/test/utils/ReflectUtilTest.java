package io.jboot.test.utils;

import io.jboot.utils.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

public class ReflectUtilTest {


    @Test
    public void testSetFieldValue(){
        ReflectBean bean = new ReflectBean();
        bean.setAge(18);
        bean.setId("myid");

        Assert.assertEquals((int)ReflectUtil.getFieldValue(bean,"age"),18);
        Assert.assertEquals(ReflectUtil.getFieldValue(bean,"id"),"myid");
        Assert.assertEquals(ReflectUtil.getStaticFieldValue(ReflectBean.class,"someStaticValue"),"staticvalue");

        ReflectUtil.setFieldValue(bean,"id","123");
        ReflectUtil.setFieldValue(bean,"age",45);
        Assert.assertEquals("123",bean.getId());
        Assert.assertEquals(45,bean.getAge());

        ReflectUtil.invokeMethod(bean,"doSomeThing");

        System.out.println(bean);

        ReflectUtil.setStaticFieldValue(ReflectBean.class,"someStaticValue","vvvv1111");

        System.out.println(bean);
    }
}
