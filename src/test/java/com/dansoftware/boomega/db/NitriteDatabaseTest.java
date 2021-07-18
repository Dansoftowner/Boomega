package com.dansoftware.boomega.db;

import com.dansoftware.boomega.db.data.Record;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        underTest = new NitriteDatabase(nitriteClient);
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
        Record record = new Record.Builder(Record.Type.BOOK).build();

        //when
        underTest.insertRecord(record);

        //then
        verify(recordRepository).insert(record);
    }

    @Test
    void itShouldUpdateRecord() {
        //given
        Record record = new Record.Builder(Record.Type.BOOK).build();

        //when
        underTest.updateRecord(record);

        //then
        verify(recordRepository).update(record);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldRemoveRecord() {
        //given
        Record record = new Record.Builder(Record.Type.BOOK).build();

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
