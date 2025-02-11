INSERT INTO company (name)
SELECT 'Company ' || generate_series(1, 10);