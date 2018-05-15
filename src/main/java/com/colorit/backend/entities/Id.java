package com.colorit.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Id<T> {
    private Long id;
    private String additionalInfo;

    public Id(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public static <T> Id<T> of(long id) {
        return new Id<>(id);
    }

    @Override
    public boolean equals(Object subj) {
        if (this == subj) {
            return true;
        }
        if (subj == null || getClass() != subj.getClass()) {
            return false;
        }
        final Id<?> id1 = (Id<?>) subj;

//        if (this.additionalInfo != null) {
//            return this.additionalInfo.equals(id1.additionalInfo);
//        }
        return this.id.equals(id1.id);
    }

    @Override
    public int hashCode() {
        return (id.intValue() ^ (id.intValue() >>> 32));
    }

    @Override
    public String toString() {
        return "Id{"
                + "id=" + id + ","
                + "additionalInfo=" + additionalInfo
                + "}";
    }
}
