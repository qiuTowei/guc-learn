package com.doublev.function;

        import java.util.function.Function;

/**
 * Function 函数式接口，有一个输入参数，一个输出
 *
 *
 */
public class FunctionDemo {
    public static void main(String[] args) {
        Function<String, String> function = new Function<String, String>() {
            /**
             * Applies this function to the given argument.
             *
             * @param o the function argument
             * @return the function result
             */
            @Override
            public String apply(String o) {
                return o;
            }
        };
        // lambda表达式简化
        Function<String,String> function1 = (str) -> str;
        System.out.println(function.apply("3333"));
        System.out.println(function1.apply("function1"));
    }
}
