\connect site

ALTER TABLE customer ADD COLUMN admingroup_groupid character varying(255);
ALTER TABLE customer ADD COLUMN membergroup_groupid character varying(255);

ALTER TABLE customer
  ADD CONSTRAINT fk_customer_admingroup_groupid FOREIGN KEY (admingroup_groupid)
      REFERENCES group_ (groupid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE customer
  ADD CONSTRAINT fk_customer_membergroup_groupid FOREIGN KEY (membergroup_groupid)
      REFERENCES group_ (groupid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO schemaversion VALUES (NOW(), 'bare', '0003');
