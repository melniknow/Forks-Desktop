package com.melniknow.fd.advanced;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.melniknow.fd.Context;
import com.melniknow.fd.domain.Bookmaker;

import java.util.ArrayList;

public class BundleStorage {
    private final ArrayList<BundleSetting> bundleSettingArrayList = new ArrayList<>();

    public void add(BundleSetting bundleSetting) {
        for (BundleSetting setting : bundleSettingArrayList) {
            if (bundleSetting.name().equals(setting.name()) ||
                setting.bk1().equals(bundleSetting.bk1()) && setting.bk2().equals(bundleSetting.bk2()) ||
                setting.bk1().equals(bundleSetting.bk2()) && setting.bk2().equals(bundleSetting.bk1()))
                throw new RuntimeException();
        }

        bundleSettingArrayList.add(bundleSetting);
    }

    public BundleSetting get(Bookmaker bookmaker1, Bookmaker bookmaker2) {
        for (BundleSetting bundleSetting : bundleSettingArrayList) {
            if (bundleSetting.bk1().equals(bookmaker1) && bundleSetting.bk2().equals(bookmaker2) ||
                bundleSetting.bk1().equals(bookmaker2) && bundleSetting.bk2().equals(bookmaker1))
                return bundleSetting;
        }

        return null;
    }

    public void remove(BundleSetting bundleSetting) {
        bundleSettingArrayList.remove(bundleSetting);
    }

    public void saveToDb() {
        var array = new JsonArray();

        for (BundleSetting bundleSetting : bundleSettingArrayList) {
            var obj = new JsonObject();
            obj.addProperty("name", bundleSetting.name());
            obj.addProperty("isValue", bundleSetting.isValue());
            obj.addProperty("bk1", bundleSetting.bk1().name());
            obj.addProperty("bk2", bundleSetting.bk2().name());
            obj.addProperty("isVerifiedValue", bundleSetting.isVerifiedValue());

            array.add(obj);
        }

        Context.profile.json.add("bundle", array);
        Context.profile.save();
    }

    public void clear() {
        bundleSettingArrayList.clear();
    }
}
