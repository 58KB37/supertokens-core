/*
 *    Copyright (c) 2024, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.webserver.api.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.supertokens.Main;
import io.supertokens.config.CoreConfig;
import io.supertokens.storageLayer.StorageLayer;
import io.supertokens.webserver.WebserverAPI;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class CoreConfigListAPI extends WebserverAPI {
    private static final long serialVersionUID = -4641988458637882374L;

    public CoreConfigListAPI(Main main) {
        super(main, "");
    }

    @Override
    public String getPath() {
        return "/core-config/list";
    }

    @Override
    protected boolean checkAPIKey(HttpServletRequest req) {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonArray config = CoreConfig.getConfigFieldsJson(main);
        JsonObject result = new JsonObject();

        try {
            if (shouldProtectProtectedConfig(req)) {
                JsonArray configWithouProtectedFields = new JsonArray();
                String[] protectedFields = StorageLayer.getBaseStorage(main)
                        .getProtectedConfigsFromSuperTokensSaaSUsers();
                for (JsonElement field : config) {
                    String fieldName = field.getAsJsonObject().get("name").getAsString();
                    if (!Arrays.asList(protectedFields).contains(fieldName)) {
                        configWithouProtectedFields.add(field);
                    }
                }
                config = configWithouProtectedFields;
            }
        } catch (Exception e) {
        }

        result.addProperty("status", "OK");
        result.add("config", config);
        super.sendJsonResponse(200, result, resp);

    }
}
