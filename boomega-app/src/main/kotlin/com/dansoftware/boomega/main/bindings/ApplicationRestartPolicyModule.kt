/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.main.bindings

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.process.SingletonProcessService
import com.google.inject.AbstractModule
import com.google.inject.Provides
import javafx.application.Platform
import javax.inject.Named

/**
 * DI module that provides the default policies for the [com.dansoftware.boomega.gui.app.ApplicationRestart] entity.
 */
class ApplicationRestartPolicyModule : AbstractModule() {
    @Provides
    @Named("preProcessCreation")
    @Suppress("unused")
    fun providePreProcessCreation() = Runnable { get(SingletonProcessService::class).release() }

    @Provides
    @Named("terminationPolicy")
    @Suppress("unused")
    fun provideTerminationPolicy() = Runnable { Platform.exit() }
}