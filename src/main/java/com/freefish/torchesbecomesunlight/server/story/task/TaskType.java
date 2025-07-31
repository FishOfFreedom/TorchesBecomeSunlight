package com.freefish.torchesbecomesunlight.server.story.task;

import java.util.function.Supplier;

public class TaskType<T extends Task> {
    private final Supplier<T> supplier;

    public TaskType(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T create(){
        return supplier.get();
    }
}
