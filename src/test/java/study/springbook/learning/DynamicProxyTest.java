package study.springbook.learning;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.*;

public class DynamicProxyTest {

    @Test
    public void simpleProxy() {
        Hello proxyHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertThat(proxyHello.sayHello("name")).isEqualTo("HELLO NAME");
        assertThat(proxyHello.sayHi("name")).isEqualTo("HI NAME");
        assertThat(proxyHello.sayThankYou("name")).isEqualTo("THANK YOU NAME");
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());
        proxyFactoryBean.addAdvice(new UppercaseAdvice());

        Hello proxyHello = (Hello) proxyFactoryBean.getObject();
        assertThat(proxyHello.sayHello("name")).isEqualTo("HELLO NAME");
        assertThat(proxyHello.sayHi("name")).isEqualTo("HI NAME");
        assertThat(proxyHello.sayThankYou("name")).isEqualTo("THANK YOU NAME");
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxyHello = (Hello) proxyFactoryBean.getObject();

        assertThat(proxyHello.sayHello("name")).isEqualTo("HELLO NAME");
        assertThat(proxyHello.sayHi("name")).isEqualTo("HI NAME");
        assertThat(proxyHello.sayThankYou("name")).isEqualTo("Thank you name");
    }

    @Test
    public void classNamePointcutAdvisor() {
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> clazz.getSimpleName().startsWith("HelloT");
            }
        };
        classMethodPointcut.setMappedName("sayH*");

        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {
        }
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloTeddy extends HelloTarget {
        }
        checkAdviced(new HelloTeddy(), classMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(target);
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxyHello = (Hello) proxyFactoryBean.getObject();

        if (adviced) {
            assertThat(proxyHello.sayHello("name")).isEqualTo("HELLO NAME");
            assertThat(proxyHello.sayHi("name")).isEqualTo("HI NAME");
            assertThat(proxyHello.sayThankYou("name")).isEqualTo("Thank you name");
        } else {
            assertThat(proxyHello.sayHello("name")).isEqualTo("Hello name");
            assertThat(proxyHello.sayHi("name")).isEqualTo("Hi name");
            assertThat(proxyHello.sayThankYou("name")).isEqualTo("Thank you name");
        }
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }

    static class UppercaseHandler implements InvocationHandler {

        private Hello target;

        public UppercaseHandler(Hello target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String ret = (String) method.invoke(target, args);
            return ret.toUpperCase();
        }
    }

    interface Hello {
        String sayHello(String name);

        String sayHi(String name);

        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank you " + name;
        }
    }
}
