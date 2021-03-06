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

package com.mango.home.domain.model.resource.secure.cred;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;
import com.mango.home.utils.constant.OcfResourceAttributeKey;

public class OcCredRole {

    private String authority;
    private String role;

    public OcCredRole() {
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void parseOCRepresentation(OCRepresentation rep) {
        /* role */
        String role = OCRep.getString(rep, OcfResourceAttributeKey.ROLE_KEY);
        this.setRole(role);
        /* authority */
        String authority = OCRep.getString(rep, OcfResourceAttributeKey.ROLE_AUTHORITY_KEY);
        this.setAuthority(authority);
    }
}
