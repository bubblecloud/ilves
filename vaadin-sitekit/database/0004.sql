ALTER TABLE company ADD COLUMN url character varying(255);
UPDATE COMPANY SET url = 'http://127.0.0.1:8083/site';
INSERT INTO schemaversion VALUES (NOW(), 'bare', '0004');