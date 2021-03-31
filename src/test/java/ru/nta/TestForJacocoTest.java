package ru.nta;


import org.junit.Assert;
import org.junit.Test;

public class TestForJacocoTest {

   @Test
    public void multy() {
       Assert.assertEquals(TestForJacoco.multy(5), 25);
    }
}