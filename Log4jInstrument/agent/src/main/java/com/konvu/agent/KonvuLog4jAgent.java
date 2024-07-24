package com.konvu.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.LegacyAbstractLogger;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class KonvuLog4jAgent {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(KonvuLog4jAgent.class.getSimpleName());
    /**
     * Attaches the agent statically on JVM start,
     * this method is invoked before {@code main} method is called.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void premain(final String agentArgs,
                               final Instrumentation inst) {
        LOGGER.info("[Agent] Instrumenting on the JVM start!");
        transform(inst);
    }

    /**
     * If the agent is attached to an already running JVM,
     * this method is invoked.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void agentmain(final String agentArgs,
                                 final Instrumentation inst) {
        LOGGER.info("[Agent] Instrumenting already running JVM!");
        transform(inst);
    }

    private static void transform(final Instrumentation instrumentation){
        // intercepting Logger implementation
        new AgentBuilder.Default()
                .type(loggerClassMatcher())
                .transform((builder, type, classLoader, module, domain) ->
                                builder.method(ElementMatchers.namedOneOf("info","warn", "error", "debug", "trace"))
                                    .intercept(Advice.to(SLF4JInterceptor.class))
                        )
                .installOn(instrumentation);
        LOGGER.info("[Agent] Intercepting Logger");
        // interception of toString method of all classes
       new AgentBuilder.Default()
                .type(ElementMatchers.any()) // Intercept all classes
                .transform((builder, type, classLoader, module, domain) ->
                        builder.method(ElementMatchers.named("toString"))
                                .intercept(Advice.to(ToStringInterceptor.class)))
                .installOn(instrumentation);
        LOGGER.info("Intercepting toString method");
    }


    public static ElementMatcher.Junction<TypeDescription> loggerClassMatcher() {
        return ElementMatchers.isSubTypeOf(Logger.class) // keeping only the actual Logger implementation in the application where this agent is used
                .and(ElementMatchers.not(ElementMatchers.is(LegacyAbstractLogger.class))) // removing legacy logger to avoid unwanted interception
                .and(ElementMatchers.not(ElementMatchers.is(AbstractLogger.class))); // removing legacy logger to avoid unwanted interception
    }
    public static class SLF4JInterceptor {
        @Advice.OnMethodEnter
        static void onEnter( @Advice.AllArguments Object[] args, @Advice.Origin Method method) {

            StringBuilder result = new StringBuilder("[Agent] ");
            // calculating calling class name, calling method and line number
            StackTraceElement callingMethod = Thread.currentThread().getStackTrace()[2];
            result
                    .append(callingMethod.getClassName())
                    .append('.').append(callingMethod.getMethodName())
                    .append(':').append(callingMethod.getLineNumber())
                    .append(' ').append(method.getName().toUpperCase());

            for (Object arg: args) {
                result.append(' ');
                if(arg instanceof Object[]){
                    result.append(Arrays.toString((Object[]) arg));
                }else
                    result.append(arg);
            }

            System.out.println(result);
        }

    }

    public static class ToStringInterceptor {
        @Advice.OnMethodExit
        static void onExit(@Advice.Return(readOnly = false) String returnValue, @Advice.Origin Class<?> classz) {
            StringBuilder result = new StringBuilder();
            StackTraceElement callingMethod = Thread.currentThread().getStackTrace()[2];

            result
                    .append(callingMethod.getClassName())
                    .append('.').append(callingMethod.getMethodName())
                    .append(':').append(callingMethod.getLineNumber())
                    .append(' ').append(classz.getSimpleName())
                    .append(".toString() => ");

            returnValue = result.append(returnValue).toString();
        }
    }

}
