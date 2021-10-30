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

package com.dansoftware.boomega.database;

import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.database.api.data.Record;
import com.dansoftware.boomega.database.bmdb.NitriteDatabase;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NitriteDatabaseTest {

    @Mock private Nitrite nitriteClient;
    @Mock private ObjectRepository<Record> recordRepository;
    private NitriteDatabase underTest;

    @BeforeEach
    void initialize() {
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);
        underTest = new NitriteDatabase(nitriteClient, mock(DatabaseMeta.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldGetRecords() {
        //given
        Cursor<Record> mockCursor = mock(Cursor.class);
        List<Record> expected = Collections.emptyList();
        given(mockCursor.toList()).willReturn(expected);
        given(recordRepository.find()).willReturn(mockCursor);

        //when
        List<Record> result = underTest.getRecords();

        //then
        verify(recordRepository).find();
        verify(mockCursor).toList();
        assertThat(result == expected).isTrue();
    }

    @Test
    void itShouldInsertRecord() {
        //given
        Record record = new Record(Record.Type.BOOK);

        //when
        underTest.insertRecord(record);

        //then
        verify(recordRepository).insert(record);
    }

    @Test
    void itShouldUpdateRecord() {
        //given
        Record record = new Record(Record.Type.BOOK);

        //when
        underTest.updateRecord(record);

        //then
        verify(recordRepository).update(record);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldRemoveRecord() {
        //given
        Record record = new Record(Record.Type.BOOK);

        //when
        underTest.removeRecord(record);

        //then
        verify(recordRepository).remove(record);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldGetTotalCount() {
        //given
        Cursor<Record> mockCursor = mock(Cursor.class);
        given(recordRepository.find()).willReturn(mockCursor);

        //when
        underTest.getTotalRecordCount();

        //then
        verify(recordRepository).find();
        verify(mockCursor).totalCount();
    }

    @Test
    void itShouldClose() {
        //given
        //when
        underTest.close();

        //then
        verify(nitriteClient).close();
    }

}
