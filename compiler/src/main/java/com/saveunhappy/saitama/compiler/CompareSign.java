package com.saveunhappy.saitama.compiler;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;

public enum CompareSign {
    EQUAL("==", Opcodes.IF_ICMPNE),
    NOT_EQUAL("!=", Opcodes.IF_ICMPEQ),
    LESS("<", Opcodes.IF_ICMPGE),
    GREATER(">", Opcodes.IF_ICMPLE),
    LESS_OR_EQUAL("<=", Opcodes.IF_ICMPGT),
    GRATER_OR_EQAL(">=", Opcodes.IF_ICMPLT);

    private String sign;
    //reversed. '>' evalutes to le -> less or equal
    private int opcode;

    CompareSign(String s, int opcode) {
        sign = s;
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public static CompareSign fromString(String sign) {
        return Arrays.stream(values()).filter(cmpSign -> cmpSign.sign.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
