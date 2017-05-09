package org.jetbrains.test;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ProfilingAgent implements ClassFileTransformer {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("hello premain!");
        inst.addTransformer(new ProfilingAgent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        System.out.println("check class: " + className);
//        if (className.equals("org/jetbrains/test/Main")) {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, 0);
//            TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
            cr.accept(new ProfilingClassAdapter(cw), 0);

//            return classfileBuffer;
//        }
        return classfileBuffer;
    }
}

class ProfilingClassAdapter extends ClassVisitor {
    ProfilingClassAdapter(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("lambda$main$0")) {
            System.out.println("found lambda$main$0");
        }
        return new MethodVisitor(Opcodes.ASM5) {
            @Override
            public void visitTypeInsn(int opcode, String name) {
                if (opcode == Opcodes.NEW && name.equals("org/jetbrains/test/DummyApplication")) {
                    System.out.println("found new Dummy Application");
                }
                super.visitTypeInsn(opcode, name);
            }
        };
//        return super.visitMethod(i, s, s1, s2, strings);
    }
}
