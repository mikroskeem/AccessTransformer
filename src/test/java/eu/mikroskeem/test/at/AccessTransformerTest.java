package eu.mikroskeem.test.at;

import eu.mikroskeem.at.AccessTransformer;
import eu.mikroskeem.shuriken.instrumentation.ClassLoaderTools;
import eu.mikroskeem.shuriken.instrumentation.ClassTools;
import eu.mikroskeem.shuriken.reflect.Reflect;
import eu.mikroskeem.shuriken.reflect.wrappers.ClassWrapper;
import eu.mikroskeem.shuriken.reflect.wrappers.FieldWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @author Mark Vainomaa
 */
public class AccessTransformerTest {
    @Test
    @SuppressWarnings("ConstantConditions")
    public void testFieldAccessTransformer() throws Exception {
        AccessTransformer at = new AccessTransformer();
        loadAt(at, "test_field_at.cfg");

        /* Read class and transform it */
        byte[] clazz = at.transformClass(getClass(TestClass1.class));

        /* Load class */
        URLClassLoader newUcl = URLClassLoader.newInstance(new URL[0], null);
        Class<?> newClass = ClassLoaderTools.defineClass(newUcl, TestClass1.class.getName(), clazz);
        ClassWrapper<?> cw = Reflect.wrapClass(newClass);

        /* Do assertions */
        int aModifiers = cw.getField("a", String.class).get().getField().getModifiers();
        int bModifiers = cw.getField("b", String.class).get().getField().getModifiers();
        Assertions.assertTrue(Modifier.isProtected(aModifiers), "Field a must be protected!");
        Assertions.assertTrue(Modifier.isPublic(bModifiers), "Field b must be public!");
    }

    @Test
    public void testMethodAccessTransformer() throws Exception {
        AccessTransformer at = new AccessTransformer();
        loadAt(at, "test_method_at.cfg");

        /* Read class and transform it */
        byte[] clazz = at.transformClass(getClass(TestClass1.class));

        /* Load class */
        URLClassLoader newUcl = URLClassLoader.newInstance(new URL[0], null);
        Class<?> newClass = ClassLoaderTools.defineClass(newUcl, TestClass1.class.getName(), clazz);

        /* Do assertions */
        Assertions.assertTrue(Modifier.isPublic(newClass.getConstructor(long.class).getModifiers()),
                "Constructor with long as parameter should be public!");
        Assertions.assertTrue(Modifier.isPublic(newClass.getDeclaredMethod("h", String.class).getModifiers()),
                "Method h should be public!");
    }

    @Test
    public void testWildcardAccessTransformer() throws Exception {
        AccessTransformer at = new AccessTransformer();
        loadAt(at, "test_wildcard_at.cfg");

        /* Read class and transform it */
        byte[] clazz = at.transformClass(getClass(TestClass1.class));

        /* Load class */
        URLClassLoader newUcl = URLClassLoader.newInstance(new URL[0], null);
        Class<?> newClass = ClassLoaderTools.defineClass(newUcl, TestClass1.class.getName(), clazz);

        /* Do assertions */
        List<FieldWrapper<?>> fields = Reflect.wrapClass(newClass).getFields();
        fields.forEach(fieldWrapper -> {
            System.out.println(fieldWrapper.getField());
            Assertions.assertTrue(Modifier.isPublic(fieldWrapper.getField().getModifiers()),
                    String.format("Field %s should be public", fieldWrapper.getField().getName()));
            Assertions.assertTrue(Modifier.isFinal(fieldWrapper.getField().getModifiers()),
                    String.format("Field %s should be final", fieldWrapper.getField().getName()));
        });
    }

    /* Utils */
    private void loadAt(AccessTransformer at, String file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/" + file)));
        at.loadAccessTransformers(br);
    }

    private byte[] getClass(Class<?> clazz) throws IOException {
        URLClassLoader ucl = (URLClassLoader)this.getClass().getClassLoader();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(InputStream is = ucl.getResourceAsStream(ClassTools.unqualifyName(clazz) + ".class")) {
            while(is.available() != 0) baos.write(is.read());
        }
        return baos.toByteArray();
    }

    private void dumpClass(byte[] rawClass) {
        ClassReader cr = new ClassReader(rawClass);
        cr.accept(new TraceClassVisitor(null, new PrintWriter(System.out)), 0);
    }
}
