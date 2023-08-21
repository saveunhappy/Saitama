package com.saveunhappy.saitama.compiler;

import com.saveunhappy.saitama.compiler.bytecodegeneration.CompilationUnit;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by KevinOfNeu on 2018/7/18  21:14.
 */
public class Compiler implements Opcodes {

    public static void main(String[] args) {
        String[] sourceCodeLocation = {"SaitamaExample/First.stm"};
        try {
            new Compiler().compile(sourceCodeLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void compile(String[] args) throws Exception {
        final ARGUMENT_ERRORS argumentsErrors = getArgumentValidationErrors(args);
        if (argumentsErrors != ARGUMENT_ERRORS.NONE) {
            System.out.println(argumentsErrors.getMessage());
            return;
        }
        final File enkelFile = new File(args[0]);
        String fileAbsolutePath = enkelFile.getAbsolutePath();
        final CompilationUnit compilationUnit = new Parser().getCompilationUnit(fileAbsolutePath);
        saveBytecodeToClassFile(compilationUnit);
    }

    private ARGUMENT_ERRORS getArgumentValidationErrors(String[] args) {
        if (args.length != 1) {
            return ARGUMENT_ERRORS.NO_FILE;
        }
        String filePath = args[0];
        if (!filePath.endsWith(".stm")) {
            return ARGUMENT_ERRORS.BAD_FILE_EXTENSION;
        }
        return ARGUMENT_ERRORS.NONE;
    }

    private static void saveBytecodeToClassFile(CompilationUnit compilationUnit) throws IOException {
        //这个方法其实应该是void，但是要返回字节码，所以返回字节
        final byte[] byteCode = compilationUnit.getByteCode();
        String className = compilationUnit.getClassName();
        String fileName = className + ".class";
        OutputStream os = Files.newOutputStream(Paths.get(fileName));
        IOUtils.write(byteCode, os);
    }
}
