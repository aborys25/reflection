package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.pool.TypePool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        var bar = new ByteBuddy()
                .redefine(typePool.describe("org.example.MainInteger").resolve(),
                        ClassFileLocator.ForClassLoader.ofSystemLoader())
                .defineMethod("isEven", typePool.describe("java.lang.Boolean").resolve())
                //.intercept(FixedValue.value(Boolean.TRUE))
                .intercept(MethodCall.invoke(isEvenMethod(typePool)).withField("value"))
                .make()
                .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
//System.out.println(bar.getDeclaredMethods());


        //var  dynamiczne ustalanie typu zmiennej

        var barInstance = bar.getConstructor(Integer.class).newInstance(2);

        for (var met : bar.getDeclaredMethods()) {
            System.out.println(met.getName());
            System.out.println(met.invoke(barInstance));
        }

        //var isEven = bar.getDe
    }

//ta metoda sprawdza parzystosc
    private static MethodDescription isEvenMethod(TypePool typePool) {
        return typePool.describe("org.example.IntegerUtils").resolve().getDeclaredMethods().filter(named("isEven")).stream().findFirst().orElseThrow();
    }
}

