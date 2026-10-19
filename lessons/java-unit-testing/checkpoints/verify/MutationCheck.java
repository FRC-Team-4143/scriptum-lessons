// Grades a student's test class by mutation testing. The check compiles and
// runs the tests itself (it never uses the project's Test, Assertions or
// TestRunner), first on the correct code and then on each mutant: a copy of the
// code with one deliberate bug. A test class only counts when it passes on the
// correct code and FAILS on every mutant.
//
//   java MutationCheck.java <project src> <TestClass> <SubjectClass> <method> <verify dir> <work dir> <mutant>...
//
// Only the test class is taken from the project, so an unfinished test class
// for another checkpoint can't break this one. Each mutant's source has a
// "// Slips through:" line that says what its bug is.
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MutationCheck {
    static void fail(String message) {
        System.out.println(message);
        System.exit(1);
    }

    static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler();

    // Compiles sources into out; returns the first error, or null.
    static String compile(File out, String classPath, File... sources) throws Exception {
        out.mkdirs();
        DiagnosticCollector<JavaFileObject> errors = new DiagnosticCollector<>();
        try (StandardJavaFileManager files = JAVAC.getStandardFileManager(null, null, null)) {
            List<String> options = new ArrayList<>(List.of("-d", out.getPath(), "-proc:none"));
            if (classPath != null) {
                options.add("-cp");
                options.add(classPath);
            }
            boolean ok = JAVAC.getTask(null, files, errors, options, null, files.getJavaFileObjects(sources)).call();
            if (ok) {
                return null;
            }
        }
        for (Diagnostic<? extends JavaFileObject> d : errors.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                String where = d.getSource() == null ? "" : new File(d.getSource().getName()).getName() + ":" + d.getLineNumber() + ": ";
                return where + "error: " + d.getMessage(Locale.ENGLISH).lines().findFirst().orElse("");
            }
        }
        return "it doesn't compile";
    }

    static ClassLoader loaderFor(File... dirs) throws Exception {
        URL[] urls = new URL[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            urls[i] = dirs[i].toURI().toURL();
        }
        // The platform loader as parent: nothing from this check's own class path leaks in.
        return new URLClassLoader(urls, ClassLoader.getPlatformClassLoader());
    }

    static boolean isTest(Method method) {
        for (Annotation a : method.getAnnotations()) {
            if (a.annotationType().getSimpleName().equals("Test")) {
                return true;
            }
        }
        return false;
    }

    static List<Method> tests(Class<?> testClass) {
        List<Method> found = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (isTest(method)) {
                found.add(method);
            }
        }
        found.sort(Comparator.comparing(Method::getName));
        return found;
    }

    // null when the test passed; otherwise what went wrong.
    static String runOne(Class<?> testClass, Method test) {
        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            test.setAccessible(true);
            test.invoke(instance);
            return null;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError) {
                return String.valueOf(cause.getMessage());
            }
            return "it crashed with " + cause;
        } catch (ReflectiveOperationException e) {
            return "it couldn't be run (" + e + ")";
        }
    }

    // True when at least one test fails, i.e. the tests caught this version.
    static boolean anyFails(Class<?> testClass) {
        for (Method test : tests(testClass)) {
            if (runOne(testClass, test) != null) {
                return true;
            }
        }
        return false;
    }

    static String slipsThrough(File mutantDir) throws Exception {
        for (File f : mutantDir.listFiles()) {
            if (f.getName().endsWith(".java")) {
                for (String line : Files.readAllLines(f.toPath())) {
                    if (line.startsWith("// Slips through: ")) {
                        return line.substring("// Slips through: ".length());
                    }
                }
            }
        }
        return "A buggy version of the code still passes all your tests.";
    }

    public static void main(String[] args) throws Exception {
        File src = new File(args[0]);
        String testName = args[1];
        String subject = args[2];
        String method = args[3];
        File verify = new File(args[4]);
        File work = new File(args[5]);

        File testSource = new File(src, testName + ".java");
        if (!testSource.isFile()) {
            fail("There is no src/" + testName + ".java. Put your tests in a class called " + testName + ".");
        }

        File lib = new File(work, "lib");
        compile(lib, null, new File(verify, "lib/Test.java"), new File(verify, "lib/Assertions.java"));
        File correct = new File(work, "correct");
        String error = compile(correct, lib.getPath(), new File(verify, "correct/" + subject + ".java"), testSource);
        if (error != null) {
            fail(testName + ".java doesn't compile: " + error);
        }
        List<File> mutants = new ArrayList<>();
        for (int i = 6; i < args.length; i++) {
            File mutant = new File(work, "mutants/" + args[i]);
            File source = new File(verify, "mutants/" + args[i] + "/" + subject + ".java");
            compile(mutant, null, source);
            Files.copy(source.toPath(), new File(mutant, subject + ".java").toPath());
            mutants.add(mutant);
        }

        Class<?> testClass = Class.forName(testName, true, loaderFor(correct, lib));
        List<Method> tests = tests(testClass);
        if (tests.isEmpty()) {
            fail(testName + " has no tests. Put @Test on the line above each test method.");
        }

        for (Method test : tests) {
            String problem = runOne(testClass, test);
            if (problem != null) {
                fail("Your test " + test.getName() + " fails, but " + subject + " is correct here: "
                    + problem + ". A test has to pass on correct code, so check what it expects.");
            }
        }

        List<String> missed = new ArrayList<>();
        for (File mutant : mutants) {
            Class<?> onMutant = Class.forName(testName, true, loaderFor(mutant, correct, lib));
            if (!anyFails(onMutant)) {
                missed.add(slipsThrough(mutant));
            }
        }
        int bugs = mutants.size();
        if (!missed.isEmpty()) {
            String more = missed.size() > 1
                ? " (Your tests miss " + missed.size() + " of the " + bugs + " bugs the check tried.)"
                : "";
            fail(missed.get(0) + more);
        }

        String plural = tests.size() == 1 ? " test passes" : " tests pass";
        String all = bugs == 1 ? "the buggy " + method : "all " + bugs + " buggy versions of " + method;
        System.out.println("Your " + tests.size() + plural + " on the real " + subject
            + " and catch" + (tests.size() == 1 ? "es " : " ") + all + ".");
    }
}
