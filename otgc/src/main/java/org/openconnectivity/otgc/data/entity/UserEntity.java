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

package org.openconnectivity.otgc.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "users")
public class UserEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String mId;

    @ColumnInfo(name = "userId")
    private String mUserId;

    @ColumnInfo(name = "firstName")
    private String mFirstName;

    @ColumnInfo(name = "lastName")
    private String mLastName;

    @ColumnInfo(name = "idToken")
    private String mIdToken;

    @ColumnInfo(name = "accesToken")
    private String mAccessToken;

    @ColumnInfo(name = "refreshToken")
    private String mRefreshToken;

    @Ignore
    public UserEntity(String userId, String firstName, String lastName, String idToken, String accessToken, String refreshToken) {
        this(UUID.randomUUID().toString(), userId, firstName, lastName, idToken, accessToken, refreshToken);
    }

    public UserEntity(@NonNull String id, String userId, String firstName, String lastName, String idToken, String accessToken, String refreshToken) {
        this.mId = id;
        this.mUserId = userId;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mIdToken = idToken;
        this.mAccessToken = accessToken;
        this.mRefreshToken = refreshToken;
    }

    public String getId() {
        return mId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getIdToken() {
        return mIdToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }
}
