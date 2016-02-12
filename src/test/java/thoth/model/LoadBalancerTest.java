package thoth.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoadBalancerTest {

    @Test
    public void testMap() throws Exception {
        int result = LoadBalancer.ROUND_ROBIN.map(null, 5);
        assertEquals(0, result);

        result = LoadBalancer.ROUND_ROBIN.map(null, 5);
        assertEquals(1, result);

        LoadBalancer.ROUND_ROBIN.map(null, 5);
        LoadBalancer.ROUND_ROBIN.map(null, 5);
        result = LoadBalancer.ROUND_ROBIN.map(null, 5);
        assertEquals(4, result);

        result = LoadBalancer.ROUND_ROBIN.map(null, 5);
        assertEquals(0, result);
    }
}