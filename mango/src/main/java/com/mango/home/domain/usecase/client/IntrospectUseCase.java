/*
 *  *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  *****************************************************************
 */

package com.mango.home.domain.usecase.client;

import com.mango.home.data.repository.IotivityRepository;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.introspection.OcIntrospection;
import com.mango.home.domain.model.resource.introspection.OcIntrospectionUrlInfo;
import com.mango.home.domain.model.resource.virtual.res.OcEndpoint;
import com.mango.home.domain.model.resource.virtual.res.OcResource;
import com.mango.home.utils.constant.OcfResourceType;

import javax.inject.Inject;

import io.reactivex.Single;

public class IntrospectUseCase {
    private final IotivityRepository iotivityRepository;

    @Inject
    public IntrospectUseCase(IotivityRepository iotivityRepository) {
        this.iotivityRepository = iotivityRepository;
    }

    public Single<JSONObject> execute(Device device) {
        return iotivityRepository.getNonSecureEndpoint(device)
                .flatMap(endpoint -> iotivityRepository.findResource(endpoint, OcfResourceType.INTROSPECTION))
                .flatMap(res -> {
                    OcResource introspectionResource = res.getResourceList().get(0);
                    OcEndpoint endpoint = introspectionResource.getEndpoints().get(0);
                    return iotivityRepository.get(endpoint.getEndpoint(), introspectionResource.getHref(), device.getDeviceId());
                })
                .flatMap(ocRepresentation -> {
                    OcIntrospection ocIntrospection = new OcIntrospection();
                    ocIntrospection.parseOCRepresentation(ocRepresentation);
                    OcIntrospectionUrlInfo ocIntrospectionUrlInfo = ocIntrospection.getCoapsIpv6Endpoint();

                    if (ocIntrospectionUrlInfo != null) {
                        return iotivityRepository.get(ocIntrospectionUrlInfo.getHost(), ocIntrospectionUrlInfo.getUri(), device.getDeviceId());
                    } else {
                        return null;
                    }
                })
                .map((this::parseOcRepresentationToJson));

    }

    private JSONObject parseOcRepresentationToJson(OCRepresentation rep) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        while (rep != null) {
            switch (rep.getType()) {
                case OC_REP_BOOL:
                    jsonObject.put(rep.getName(), rep.getValue().getBool());
                    break;
                case OC_REP_INT:
                    jsonObject.put(rep.getName(), rep.getValue().getInteger());
                    break;
                case OC_REP_STRING:
                    jsonObject.put(rep.getName(), rep.getValue().getString());
                    break;
                case OC_REP_STRING_ARRAY:
                    JSONArray strArray = new JSONArray();
                    String[] values = OCRep.ocArrayToStringArray(rep.getValue().getArray());
                    for (String value : values) {
                        strArray.put(value);
                    }
                    jsonObject.put(rep.getName(), strArray);
                    break;
                case OC_REP_OBJECT:
                    JSONObject childObject = parseOcRepresentationToJson(rep.getValue().getObject());
                    jsonObject.put(rep.getName(), childObject);
                    break;
                case OC_REP_OBJECT_ARRAY:
                    JSONArray objArray = new JSONArray();
                    OCRepresentation tmp = rep.getValue().getObjectArray();
                    while (tmp != null) {
                        JSONObject childObj = parseOcRepresentationToJson(tmp.getValue().getObject());
                        objArray.put(childObj);

                        tmp = tmp.getNext();
                    }
                    jsonObject.put(rep.getName(), objArray);
                    break;
                default:
                    break;
            }

            rep = rep.getNext();
        }

        return jsonObject;
    }
}
