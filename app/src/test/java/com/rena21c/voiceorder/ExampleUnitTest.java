package com.rena21c.voiceorder;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.rena21c.voiceorder.model.Order;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Order.OrderState state = Order.OrderState.ACCEPTED;

        assertTrue(Order.OrderState.ACCEPTED == state);
    }
}