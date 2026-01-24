package io.swagger.v3.core.modern;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

public class AnnotationUtilsTest {
    @Test
    public void testSetDetection() {
        assertTrue(AnnotationUtils.isSetType(Set.class));
    }

    @Test
    public void testHashSetDetection() {
        assertTrue(AnnotationUtils.isSetType(HashSet.class));
    }
}