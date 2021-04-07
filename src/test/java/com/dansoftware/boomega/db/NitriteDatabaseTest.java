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
    private NitriteDatabase underTest;

    @BeforeEach
    void initialize() {
        underTest = NitriteDatabase.of(nitriteClient);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldGetRecords() {
        //given
        ObjectRepository<Record> recordRepository = mock(ObjectRepository.class);
        Cursor<Record> mockCursor = mock(Cursor.class);
        List<Record> expected = Collections.emptyList();
        given(mockCursor.toList()).willReturn(expected);
        given(recordRepository.find()).willReturn(mockCursor);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

        //when
        List<Record> result = underTest.getRecords();

        //then
        verify(recordRepository).find();
        verify(mockCursor).toList();
        assertThat(result == expected).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldInsertRecord() {
        //given
        Record record = new Record.Builder(Record.Type.BOOK).build();

        ObjectRepository<Record> recordRepository = Mockito.mock(ObjectRepository.class);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

        //when
        underTest.insertRecord(record);

        //then
        verify(recordRepository).insert(record);
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldUpdateRecord() {
        //given
        Record record = new Record.Builder(Record.Type.BOOK).build();

        ObjectRepository<Record> recordRepository = Mockito.mock(ObjectRepository.class);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

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

        ObjectRepository<Record> recordRepository = Mockito.mock(ObjectRepository.class);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

        //when
        underTest.removeRecord(record);

        //then
        verify(recordRepository).remove(record);
    }

    @SuppressWarnings("unchecked")
    @Test
    @Disabled
    void itShouldFindRecord() {
        //TODO: Finish this test

        //given
        Record record = new Record.Builder(Record.Type.BOOK).build();

        ObjectRepository<Record> recordRepository = Mockito.mock(ObjectRepository.class);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

        final Cursor<Record> mockCursor = Mockito.mock(Cursor.class);
        given(mockCursor.toList()).willReturn(Collections.emptyList());
        given(recordRepository.find(any(FindOptions.class))).willReturn(mockCursor);
        given(recordRepository.find(any(ObjectFilter.class))).willReturn(mockCursor);
        given(recordRepository.find(any(), any(FindOptions.class))).willReturn(mockCursor);

        //when
        //underTest.getRecords(any());
        underTest.getRecords(any(), any());

        //then
        //verify(recordRepository).find(any(FindOptions.class));
        verify(recordRepository).find(any(), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    void itShouldGetTotalCount() {
        //given
        ObjectRepository<Record> recordRepository = mock(ObjectRepository.class);
        Cursor<Record> mockCursor = mock(Cursor.class);
        given(recordRepository.find()).willReturn(mockCursor);
        given(nitriteClient.getRepository(any(), eq(Record.class))).willReturn(recordRepository);

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
