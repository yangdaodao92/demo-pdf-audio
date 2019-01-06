package com.yangdaodao.demopdfaudio.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class JSONUtil {

    public static JSONObject set(JSONObject obj, String paths, String key, Object val) {
        obj = obj == null ? new JSONObject() : obj;
        paths = StringUtils.isEmpty(paths) ? "" : paths;
        if (!StringUtils.isEmpty(key)) {
            JSONObject tempObj = obj;
            for (String path : paths.split(",")) {
                if (tempObj.getJSONObject(path) == null) {
                    tempObj.put(path, new JSONObject());
                }
                tempObj = tempObj.getJSONObject(path);
            }
            tempObj.put(key, val);
        }
        return obj;
    }

}
