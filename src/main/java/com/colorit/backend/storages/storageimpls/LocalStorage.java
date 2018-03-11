package com.colorit.backend.storages.storageimpls;

import com.colorit.backend.storages.IStorage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.File;


public class LocalStorage implements IStorage {
    private String staticContentPath;
    private MessageDigest md5;

    public LocalStorage(String userHome) {
        staticContentPath = userHome + "/static";
        try {
            md5 = java.security.MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException nSAe) {
            throw new RuntimeException(nSAe.getCause());
        }
    }

    @Override
    public String writeFile(File file) {
        return null;
    }

    @Override
    public byte[] readFile(String path) {
        return null;
    }
}