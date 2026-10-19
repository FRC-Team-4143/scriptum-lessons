// The check's own copy, so an edited project copy can't change what passes.
// Put @Test on the line above a method to mark it as a test. TestRunner
// finds every method marked this way and runs it.
//
// Real JUnit has the same annotation: import org.junit.jupiter.api.Test;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
