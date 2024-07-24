package com.konvu.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;

import static com.konvu.agent.KonvuLog4jAgent.loggerClassMatcher;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KonvuLog4jAgentTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    @BeforeAll
    public static void setUp() {
        System.setOut(new PrintStream(outContent));
        Instrumentation instrumentation = ByteBuddyAgent.install();

        // Apply the agent's transformations
        new AgentBuilder.Default()
                .type(loggerClassMatcher())
                .transform((builder, type, classLoader, module, domain) ->
                        builder.method(ElementMatchers.namedOneOf("info","warn", "error", "debug", "trace"))
                                .intercept(Advice.to(KonvuLog4jAgent.SLF4JInterceptor.class))
                )
                .installOn(instrumentation);

    }

    @Test
    public void testLoggerInterceptor() {

        Logger logger = LoggerFactory.getLogger(SampleLogger.class);
        logger.info("Test message");

        String output = outContent.toString();
        // Intercepted message contains "Agent"
        assertTrue(output.contains("[Agent]"));
    }
}

