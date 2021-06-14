package com.spring;

import javax.swing.plaf.SliderUI;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class CccwzApplicationContext {

    private Class configClass;

    //单例池
    //key---单例名字    value---单例对象
    private ConcurrentHashMap<String ,Object> singletonMap=new ConcurrentHashMap<>();

    //beanDefinition
    private ConcurrentHashMap<String ,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>();

    public CccwzApplicationContext(Class configClass) {
        //用一个配置类作为初始化参数
        this.configClass = configClass;
        scan(configClass);

        //把所有单例bean创建好
        for (String beanName:beanDefinitionMap.keySet()){
            BeanDefinition beanDefinition=beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanDefinition);  //单例bean
                singletonMap.put(beanName,bean);
            }
        }

    }

    public Object createBean(BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //依赖注入
            for(Field declaredField:clazz.getDeclaredFields()){
                if (declaredField.isAnnotationPresent(Autowired.class)){
                    //实现byName注入
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass) {
        //解析配置类
        // @Bean   开启aop   开启事务
        //@Component注解--->扫描路径---->扫描
        //判断有没有注解@ComponentScan
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();

        //扫描加了@Component注解的类
        //根据包名 得到类
        //获取App类加载器
        ClassLoader classLoader = CccwzApplicationContext.class.getClassLoader();
        //获取根目录下的资源
        URL resource = classLoader.getResource("com/cccwz/service");
        File file = new File(resource.getFile());
        //判断file十个目录
        if(file.isDirectory()){
            //返回目录下所有的目录和文件名，并且遍历他们
            File[] files = file.listFiles();
            for(File f:files){
                //C:\Users\23655\Desktop\springByHand\target\classes\com\cccwz\service\UserService.class
                String fileName = f.getAbsolutePath();
                //判断是不是".class"结尾的类文件
                if (fileName.endsWith(".class")){
                    String className=fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class"));
                    //com.cccwz.service.UserService
                    className=className.replace("\\",".");
                    //得到className为类文件的路径

                    //扫描类
                    //加载类
                    try {
                        //类加载器通过类名加载
                        Class<?> clazz = classLoader.loadClass(className);
                        //判断类上有没有@component注解，有的话说明是个Bean，spring才会去加载他
                        if(clazz.isAnnotationPresent(Component.class)){
                            //表明这是一个bean
                            //class---->bean？
                            //如果没有scope注解，默认是单例
                            //解析当前的bean是单例bean，或者是原型bean
                            //BeanDefinition

                            //获取component注解中的value为beanName
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            //获得作用域，判断是不是null
                            Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                            if(scopeAnnotation!=null){
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName,beanDefinition);


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }



            }
        }
    }

    public Object getBean(String beanName)  {


        if (beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                return singletonMap.get(beanName);
            }else {
                //创建一个bean对象
               return createBean(beanDefinition);
            }
        }else{
            //没有对应的bean
            throw new NullPointerException();
        }

    }
}
