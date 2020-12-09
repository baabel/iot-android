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
package com.mango.home.utils.di;

import android.app.Application;
import androidx.room.Room;
import android.content.Context;

import com.mango.home.data.persistence.dao.DeviceDao;
import com.mango.home.data.persistence.dao.UserDao;
import com.mango.home.data.persistence.database.MangoDb;
import com.mango.home.data.repository.AndroidPreferencesRepository;
import com.mango.home.data.repository.PreferencesRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    MangoDb provideDb(Application application) {
        return Room.databaseBuilder(application, MangoDb.class, "otgc.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(MangoDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    DeviceDao provideDeviceDao(MangoDb db) {
        return db.deviceDao();
    }

    @Provides
    PreferencesRepository providePreferencesRepository(AndroidPreferencesRepository repo) {
        return repo;
    }
}
