package com.examples.nio;

import java.nio.IntBuffer;

public class BufferExample {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);

        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }

        intBuffer.flip();
        intBuffer.position(1);
        intBuffer.limit(2);
        while (intBuffer.hasRemaining()){
            int next = intBuffer.get();
            System.out.println("next = " + next);
        }
    }
}
