
CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL
);

CREATE TABLE address (
                         id SERIAL PRIMARY KEY,
                         city VARCHAR(100) NOT NULL,
                         person_id INT UNIQUE REFERENCES person(id) ON DELETE CASCADE
);

CREATE TABLE company (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL
);

CREATE TABLE person_company (
                                person_id INT REFERENCES person(id),
                                company_id INT REFERENCES company(id),
                                PRIMARY KEY (person_id, company_id)
);
