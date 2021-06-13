package com.spring;

/**
 * 对于bean的定义   定义了类型
 * scope---单例还是原型
 */
public class BeanDefinition {

    //当前某个bean的类型
    private Class clazz;

    //bean的作用域
    private String scope;

    public BeanDefinition() {
    }



    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
