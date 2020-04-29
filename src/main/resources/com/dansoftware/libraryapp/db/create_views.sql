CREATE VIEW books_joined AS

SELECT books.id AS Bookid, books.title, books.published_year, books.lang, books.notes, books.isbn, books.number_of_pages, books.number_of_copies,

books.author_id01, authors01.name AS author_name01, authors01.description AS author_description01,
books.author_id02, authors02.name AS author_name02, authors02.description AS author_description02,
books.author_id03, authors03.name AS author_name03, authors03.description AS author_description03,
books.author_id04, authors04.name AS author_name04, authors04.description AS author_description04,
books.author_id05, authors05.name AS author_name05, authors05.description AS author_description05,
books.author_id06, authors06.name AS author_name06, authors06.description AS author_description06,
books.author_id07, authors07.name AS author_name07, authors07.description AS author_description07,
books.author_id08, authors08.name AS author_name08, authors08.description AS author_description08,
books.author_id09, authors09.name AS author_name09, authors09.description AS author_description09,
books.author_id10, authors10.name AS author_name10, authors10.description AS author_description10,

books.publisher_id, publishers.name AS publisher_name, books.subject_id, subjects.name AS subject_name

FROM books

LEFT JOIN authors authors01 ON authors01.id = books.author_id01
LEFT JOIN authors authors02 ON authors02.id = books.author_id02
LEFT JOIN authors authors03 ON authors03.id = books.author_id03
LEFT JOIN authors authors04 ON authors04.id = books.author_id04
LEFT JOIN authors authors05 ON authors05.id = books.author_id05
LEFT JOIN authors authors06 ON authors06.id = books.author_id06
LEFT JOIN authors authors07 ON authors07.id = books.author_id07
LEFT JOIN authors authors08 ON authors08.id = books.author_id08
LEFT JOIN authors authors09 ON authors09.id = books.author_id09
LEFT JOIN authors authors10 ON authors10.id = books.author_id10

LEFT JOIN publishers ON publishers.id = books.publisher_id
LEFT JOIN subjects ON subjects.id  = books.subject_id;

CREATE VIEW single_authors AS
    SELECT * FROM authors WHERE (SELECT COUNT(*) FROM books WHERE authors.id IN (author_id01, author_id02, author_id03,
                                                                                 author_id04, author_id05, author_id06,
                                                                                 author_id07, author_id08, author_id09,
                                                                                 author_id10)) IS 0;
CREATE VIEW single_publishers AS
    SELECT * FROM publishers WHERE (SELECT COUNT(*) FROM books WHERE publishers.id IS books.publisher_id);

CREATE VIEW single_subjects AS
    SELECT * FROM subjects WHERE (SELECT COUNT(*) FROM books WHERE subjects.id IS books.subject_id);