DO $$
    DECLARE
        rec RECORD;
    BEGIN
        FOR rec IN
            SELECT id FROM person
            LOOP
                INSERT INTO person_company (person_id, company_id)
                SELECT rec.id, id
                FROM company
                ORDER BY RANDOM()
                LIMIT 2;
            END LOOP;
    END $$;
