package com.colorit.backend.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public class Id<T> {
    private Long id;
    private String nickname;

    public Id(long id) {
        this.id = id;
    }

    public Id(String nickname) { this.nickname = nickname; }

    @JsonValue
    public long getId() {
        return id;
    }

    @JsonValue
    public String getNickname() { return this.nickname; }

    public static <T> Id<T> of(long id) {
        return new Id<>(id);
    }

    public static <T> Id<T> of(String nickname) { return new Id<>(nickname);}

    @Override
    public boolean equals(Object subj) {
        if (this == subj) {
            return true;
        }
        if (subj == null || getClass() != subj.getClass()) {
            return false;
        }
        final Id<?> id1 = (Id<?>) subj;
        if (id == null) {
            nickname.equals(id1.nickname);
        }
        return id.equals(id1.id);
    }

    @Override
    public int hashCode() {
        return id == null ? nickname.hashCode() : (int) (id.intValue() ^ (id.intValue() >>> 32));
    }

    @Override
    public String toString() {
        return "Id{"
                + "id=" + id
                + '}';
    }
}
