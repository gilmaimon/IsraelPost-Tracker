package com.gilmaimon.israelposttracker.Branches;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public abstract class BranchesProvider {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    abstract boolean available();

    @NonNull
    public final List<Branch> getAll() {
        if(!available()) throw new IllegalStateException("Resource Not Available");
        return _getAllImpl();
    }

    @NonNull
    abstract List<Branch> _getAllImpl();

    @Nullable
    public final Branch getBranch(int id) {
        if(!available()) throw new IllegalStateException("Resource Not Available");
        return _getBranchImpl(id);
    }

    @Nullable
    abstract Branch _getBranchImpl(int id);
}
