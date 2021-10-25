package com.example.rbserver.pool;


import java.util.Stack;

public class RBServerClientIDXPool {
    public static final int MIN = 1;

    private volatile static RBServerClientIDXPool singleton;
    public static RBServerClientIDXPool getSingleton() {
        if (singleton == null) {
            synchronized (RBServerClientIDXPool.class) {
                if (singleton == null) {
                    count = MIN;
                    singleton = new RBServerClientIDXPool();
                }
            }
        }
        return singleton;
    }

    private static int count = 1;
    private static Stack<Integer> rest = new Stack<Integer>();

    public int getId(){
        if (rest.empty()){
            count++;
            return count - 1;
        }
        else{
            int id = rest.peek();
            rest.pop();
            return id;
        }
    }

    public void freeId(int id){
        if (id == count - 1) count--;
        else rest.push(id);
    }

}
