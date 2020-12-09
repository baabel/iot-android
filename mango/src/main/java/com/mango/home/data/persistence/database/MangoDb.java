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

package com.mango.home.data.persistence.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mango.home.data.entity.DeviceEntity;
import com.mango.home.data.entity.UserEntity;
import com.mango.home.data.persistence.dao.DeviceDao;
import com.mango.home.data.persistence.dao.UserDao;

/**
 * The Room database that contains the Users table.
 */
@Database(entities = {UserEntity.class, DeviceEntity.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class MangoDb extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract DeviceDao deviceDao();
}
