package com.saveunhappy.saitama.compiler.utils;

import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.ClassType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Optional;

public class TypeResolver {
    public static Type getFromTypeName(SaitamaParser.TypeContext typeContext) {
        /**
         *      main(string[] args) {
         *         print sum(5,2, 1)
         *     }
         *     这样子typeContext就是空,然后就是void类型的
         * */

        if (typeContext == null) return BuiltInType.VOID;
        String typeName = typeContext.getText();
        if (typeName.equals("java.lang.String")) return BuiltInType.STRING;
        Optional<? extends Type> buildInType = getBuiltInType(typeName);
        if (buildInType.isPresent()) return buildInType.get();
        return new ClassType(typeName);
    }

    public static Type getFromValue(String value) {
        if (StringUtils.isEmpty(value)) return BuiltInType.VOID;
        if (NumberUtils.isNumber(value)) {
            return BuiltInType.INT;
        }
        //因为有负数的情况，所以不能用这个工具类了。
//        if(StringUtils.isNumeric(value)){
//            return BuiltInType.INT;
//        }
        return BuiltInType.STRING;
    }


    private static Optional<BuiltInType> getBuiltInType(String typeName) {
        return Arrays.stream(BuiltInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
