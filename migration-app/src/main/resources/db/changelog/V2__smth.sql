INSERT INTO person (name)
SELECT 'Person ' || generate_series(1, 50);