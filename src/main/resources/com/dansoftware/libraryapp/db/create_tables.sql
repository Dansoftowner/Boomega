CREATE TABLE IF NOT EXISTS authors (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    NAME TEXT NOT NULL UNIQUE,
    DESCRIPTION TEXT
);

CREATE TABLE IF NOT EXISTS publishers (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    NAME TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subjects (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    NAME TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title varchar(100) NOT NULL,
    published_year INTEGER,
    lang varchar(100),
    notes varchar(500),
	isbn varchar(13),
	number_of_pages INTEGER,
	number_of_copies INTEGER,

    author_id01 INTEGER NOT NULL,
    author_id02 INTEGER,
    author_id03 INTEGER,
    author_id04 INTEGER,
    author_id05 INTEGER,
    author_id06 INTEGER,
    author_id07 INTEGER,
    author_id08 INTEGER,
    author_id09 INTEGER,
    author_id10 INTEGER,

    publisher_id INTEGER,
    subject_id INTEGER,

    FOREIGN KEY(author_id01) REFERENCES authors (id),
    FOREIGN KEY(author_id02) REFERENCES authors (id),
    FOREIGN KEY(author_id03) REFERENCES authors (id),
    FOREIGN KEY(author_id04) REFERENCES authors (id),
    FOREIGN KEY(author_id05) REFERENCES authors (id),
    FOREIGN KEY(author_id06) REFERENCES authors (id),
    FOREIGN KEY(author_id07) REFERENCES authors (id),
    FOREIGN KEY(author_id08) REFERENCES authors (id),
    FOREIGN KEY(author_id09) REFERENCES authors (id),
    FOREIGN KEY(author_id10) REFERENCES authors (id),

    FOREIGN KEY (publisher_id) REFERENCES publishers(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);