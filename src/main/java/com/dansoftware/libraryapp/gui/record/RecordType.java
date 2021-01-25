package com.dansoftware.libraryapp.gui.record;

import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;

public enum RecordType {
    BOOK(GoogleBooksQueryBuilder.PrintType.BOOKS), MAGAZINE(GoogleBooksQueryBuilder.PrintType.MAGAZINES);

    private GoogleBooksQueryBuilder.PrintType printType;

    RecordType(GoogleBooksQueryBuilder.PrintType printType) {
        this.printType = printType;
    }

    public GoogleBooksQueryBuilder.PrintType getPrintType() {
        return printType;
    }
}
