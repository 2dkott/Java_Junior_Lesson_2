package TestFramework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.reflect.AccessibleObject.setAccessible;

public class TestProccessor {

    public static void runTest(Class<?> testClass) {
        try {
            Constructor<?> constructor = testClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object testObject = constructor.newInstance();

            List<Method> methods = List.of(testClass.getMethods());

            Optional<Method> beforeAllMethod = methods.stream().filter(method -> method.isAnnotationPresent(BeforeAll.class)).findFirst();
            Optional<Method> beforeEach = methods.stream().filter(method -> method.isAnnotationPresent(BeforeEach.class)).findFirst();
            Optional<Method> afterAll = methods.stream().filter(method -> method.isAnnotationPresent(AfterAll.class)).findFirst();
            Optional<Method> afterEach = methods.stream().filter(method -> method.isAnnotationPresent(AfterEach.class)).findFirst();
            List<Method> testMethods = sortTestMethods(methods.stream().filter(method -> method.isAnnotationPresent(Test.class)).toList());

            beforeAllMethod.ifPresent(method -> {
                checkMethodSignature(method);
                runTestMethod(method, testObject);
            });

            for (Method method : testMethods) {
                if (method.isAnnotationPresent(Test.class)) {
                    checkMethodSignature(method);

                    beforeEach.ifPresent(beforeMethod -> {
                        checkMethodSignature(beforeMethod);
                        runTestMethod(beforeMethod, testObject);
                    });

                    runTestMethod(method, testObject);

                    afterEach.ifPresent(afterMethod -> {
                        checkMethodSignature(afterMethod);
                        runTestMethod(afterMethod, testObject);
                    });
                }
            }

            afterAll.ifPresent(method -> {
                checkMethodSignature(method);
                runTestMethod(method, testObject);
            });

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void checkMethodSignature(Method method) {
        if (!method.getReturnType().isAssignableFrom(Void.class) && method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Method has wrong return type or/and parameters");
        }
    }

    private static void runTestMethod(Method method, Object object) {
        try{
            method.setAccessible(true);
            method.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Method> sortTestMethods(List<Method> methods) {
        List<Method> methodsWithOrder = new ArrayList<>(methods.stream().filter(method -> method.isAnnotationPresent(Order.class)).toList());
        List<Method> methodsNoOrder = new ArrayList<>(methods.stream().filter(method -> !method.isAnnotationPresent(Order.class)).toList());
        methodsWithOrder.sort(Comparator.comparingInt(method -> method.getAnnotation(Order.class).value()));
        methodsWithOrder.addAll(methodsNoOrder);
        return methodsWithOrder;
    }
}
