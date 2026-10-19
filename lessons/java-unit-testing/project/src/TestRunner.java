// PROVIDED: don't edit this file.
//
// Runs every @Test method in a test class and prints PASS or FAIL for each.
// Each test gets a brand-new object, so one test can't change another.
//
// On a real robot project, JUnit does this job: run ./gradlew test.
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {
    // Runs the tests in testClass and returns how many failed.
    public static int run(Class<?> testClass) {
        List<Method> tests = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                tests.add(method);
            }
        }
        tests.sort(Comparator.comparing(Method::getName));

        System.out.println(testClass.getSimpleName());
        int failed = 0;
        for (Method test : tests) {
            String problem = runOne(testClass, test);
            if (problem == null) {
                System.out.println("  PASS " + test.getName());
            } else {
                System.out.println("  FAIL " + test.getName() + ": " + problem);
                failed++;
            }
        }
        int passed = tests.size() - failed;
        System.out.println("  " + passed + " passed, " + failed + " failed");
        System.out.println();
        return failed;
    }

    // null means the test passed; otherwise, what went wrong.
    private static String runOne(Class<?> testClass, Method test) {
        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            test.setAccessible(true);
            test.invoke(instance);
            return null;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError) {
                return cause.getMessage();
            }
            return "crashed with " + cause;
        } catch (ReflectiveOperationException e) {
            return "couldn't run it: " + e;
        }
    }
}
