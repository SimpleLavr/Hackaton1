--table document_types
DROP TABLE IF EXISTS document_types CASCADE;

CREATE TABLE document_types(
    id SERIAL PRIMARY KEY,
    fields TEXT[] not null,
    name VARCHAR(50) UNIQUE NOT NULL,
    original_url VARCHAR(255) NOT NULL);

--table documents
DROP TABLE IF EXISTS documents CASCADE;

CREATE TABLE documents(
    id SERIAL PRIMARY KEY,
    fields_values VARCHAR(2047)[] NOT NULL UNIQUE,
    doctype_id INT NOT NULL,
    original VARCHAR(255) NOT NULL,
    checked BOOLEAN NOT NULL,
    changed BOOLEAN NOT NULL,
    CONSTRAINT fk_doctype
        FOREIGN KEY(doctype_id)
            REFERENCES document_types(id)
            ON DELETE CASCADE);

--table changes
DROP TABLE IF EXISTS changes CASCADE;

CREATE TABLE changes(
	document_id INT PRIMARY KEY,
	fields_values VARCHAR(2047)[] NOT NULL,
	CONSTRAINT fk_document
		FOREIGN KEY(document_id)
		REFERENCES documents(id)
		ON DELETE CASCADE);
