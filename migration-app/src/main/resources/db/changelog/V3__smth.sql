INSERT INTO address (city, person_id)
SELECT 'City ' || id, id FROM person;