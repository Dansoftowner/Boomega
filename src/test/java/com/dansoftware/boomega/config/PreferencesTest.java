/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.config;

import com.dansoftware.boomega.config.source.ConfigSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PreferencesTest {

    private ConfigSource configSource;
    private Preferences preferences;

    @BeforeEach
    void init() {
        configSource = mock(ConfigSource.class);
        preferences = new Preferences(configSource);
    }

    @Test
    void itShouldReadValue() {
        //given
        //when
        preferences.get(any());

        //then
        verify(configSource).get(any());
    }

    @Test
    void itShouldReadString() {
        //given
        //when
        preferences.getString(any(), any());

        //then
        verify(configSource).getString(any(), any());
    }

    @Test
    void itShouldReadBoolean() {
        //given
        //when
        preferences.getBoolean(any(), anyBoolean());

        //then
        verify(configSource).getBoolean(any(), anyBoolean());
    }

    @Test
    void itShouldReadInteger() {
        //given
        //when
        preferences.getInteger(any(), anyInt());

        //then
        verify(configSource).getInteger(any(), anyInt());
    }

    @Test
    void itShouldReadDouble() {
        //given
        //when
        preferences.getDouble(any(), anyDouble());

        //then
        verify(configSource).getDouble(any(), anyDouble());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldPutValue() {
        //given
        //when
        preferences.editor().put(any(), any());

        //then
        verify(configSource).put(any(), any());
    }

    @Test
    void itShouldPutBoolean() {
        //given
        //when
        preferences.editor().putBoolean(anyString(), anyBoolean());

        //then
        verify(configSource).putBoolean(anyString(), anyBoolean());
    }

    @Test
    void itShouldPutString() {
        //given
        //when
        preferences.editor().putString(anyString(), anyString());

        //then
        verify(configSource).putString(anyString(), anyString());
    }

    @Test
    void itShouldPutInteger() {
        //given
        //when
        preferences.editor().putInteger(anyString(), anyInt());

        //then
        verify(configSource).putInteger(anyString(), anyInt());
    }

    @Test
    void itShouldPutDouble() {
        //given
        //when
        preferences.editor().putDouble(anyString(), anyDouble());

        //then
        verify(configSource).putDouble(anyString(), anyDouble());
    }

    @Test
    void itShouldRemoveByString() {
        //given
        //when
        preferences.editor().remove(anyString());

        //then
        verify(configSource).remove(anyString());
    }

    @Test
    void itShouldRemoveByKey() {
        //given
        //when
        preferences.editor().remove(mock(PreferenceKey.class));

        //then
        verify(configSource).remove(any(PreferenceKey.class));
    }

    @Test
    void itShouldCommit() throws IOException {
        //given
        //when
        preferences.editor().commit();

        //then
        verify(configSource).commit();
    }

    @Test
    void itShouldTryCommit() throws IOException {
        //given
        doThrow(new IOException()).when(configSource).commit();

        //when
        preferences.editor().tryCommit();

        //then
        verify(configSource).commit();
    }

}
