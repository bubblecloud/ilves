-- Copyright 2013 Tommi S.E. Laukkanen
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.8
-- Dumped by pg_dump version 9.1.8
-- Started on 2013-03-29 23:53:41 EET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1925 (class 1262 OID 16671)
-- Name: site; Type: DATABASE; Schema: -; Owner: site
--

CREATE DATABASE site WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE site OWNER TO site;

\connect site

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 168 (class 3079 OID 11645)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1928 (class 0 OID 0)
-- Dependencies: 168
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 161 (class 1259 OID 16690)
-- Dependencies: 6
-- Name: company; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE company (
    companyid character varying(255) NOT NULL,
    phonenumber character varying(255) NOT NULL,
    invoicingemailaddress character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    salesemailaddress character varying(255) NOT NULL,
    companyname character varying(255) NOT NULL,
    supportemailaddress character varying(255) NOT NULL,
    companycode character varying(255) NOT NULL,
    modified timestamp without time zone NOT NULL,
    deliveryaddress_postaladdressid character varying(255),
    invoicingaddress_postaladdressid character varying(255),
    iban character varying(255) NOT NULL,
    bic character varying(255) NOT NULL,
    host character varying(255),
    termsandconditions character varying(4096)
);


ALTER TABLE public.company OWNER TO site;

--
-- TOC entry 162 (class 1259 OID 16696)
-- Dependencies: 6
-- Name: customer; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE customer (
    customerid character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    phonenumber character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    company boolean NOT NULL,
    emailaddress character varying(255) NOT NULL,
    firstname character varying(255) NOT NULL,
    companyname character varying(255),
    modified timestamp without time zone NOT NULL,
    companycode character varying(255),
    deliveryaddress_postaladdressid character varying(255),
    invoicingaddress_postaladdressid character varying(255),
    owner_companyid character varying(255) NOT NULL
);


ALTER TABLE public.customer OWNER TO site;

--
-- TOC entry 163 (class 1259 OID 16702)
-- Dependencies: 6
-- Name: group_; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE group_ (
    groupid character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    modified timestamp without time zone NOT NULL,
    owner_companyid character varying(255) NOT NULL
);


ALTER TABLE public.group_ OWNER TO site;

--
-- TOC entry 164 (class 1259 OID 16708)
-- Dependencies: 6
-- Name: groupmember; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE groupmember (
    groupmemberid character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    group_groupid character varying(255),
    user_userid character varying(255)
);


ALTER TABLE public.groupmember OWNER TO site;

--
-- TOC entry 165 (class 1259 OID 16750)
-- Dependencies: 6
-- Name: postaladdress; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE postaladdress (
    postaladdressid character varying(255) NOT NULL,
    addresslinetwo character varying(255),
    addresslinethree character varying(255),
    postalcode character varying(255),
    addresslineone character varying(255),
    city character varying(255),
    country character varying(255)
);


ALTER TABLE public.postaladdress OWNER TO site;

--
-- TOC entry 167 (class 1259 OID 16952)
-- Dependencies: 6
-- Name: privilege; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE privilege (
    privilegeid character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    dataid character varying(255),
    key character varying(255) NOT NULL,
    user_userid character varying(255),
    group_groupid character varying(255)
);


ALTER TABLE public.privilege OWNER TO site;

--
-- TOC entry 166 (class 1259 OID 16774)
-- Dependencies: 6
-- Name: user_; Type: TABLE; Schema: public; Owner: site; Tablespace:
--

CREATE TABLE user_ (
    userid character varying(255) NOT NULL,
    lastname character varying(255) NOT NULL,
    phonenumber character varying(255) NOT NULL,
    created timestamp without time zone NOT NULL,
    emailaddress character varying(255) NOT NULL,
    firstname character varying(255) NOT NULL,
    modified timestamp without time zone NOT NULL,
    owner_companyid character varying(255) NOT NULL,
    passwordhash character varying(256) NOT NULL
);


ALTER TABLE public.user_ OWNER TO site;

--
-- TOC entry 1914 (class 0 OID 16690)
-- Dependencies: 161 1921
-- Data for Name: company; Type: TABLE DATA; Schema: public; Owner: site
--

INSERT INTO company VALUES ('3248528E-4D90-41F7-968F-AF255AD16901', '+358 40 1639099', 'invoice@bare.com', '2011-04-22 08:52:13.899', 'sales@bare.com', 'Test Company', 'support@bare.com', '0000000-0', '2011-04-22 08:52:13.899', '4EA7E643-3C80-49B2-8D1C-AAFA7E66A28C', 'CFE997C0-3FAF-4F6C-BBED-DB09689936B6', '-', '-', '127.0.0.1', '-');


--
-- TOC entry 1915 (class 0 OID 16696)
-- Dependencies: 162 1921
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: site
--



--
-- TOC entry 1916 (class 0 OID 16702)
-- Dependencies: 163 1921
-- Data for Name: group_; Type: TABLE DATA; Schema: public; Owner: site
--

INSERT INTO group_ VALUES ('3DE5D850-B015-44C1-904C-86DC2B0276A4', '2012-02-13 21:37:24.804', 'Users', 'user', '2012-02-13 21:37:24.804', '3248528E-4D90-41F7-968F-AF255AD16901');
INSERT INTO group_ VALUES ('1DE5D850-B015-44C1-904C-86DC2B0276A3', '2012-06-25 19:57:00', 'Administrators', 'administrator', '2012-06-25 19:57:00', '3248528E-4D90-41F7-968F-AF255AD16901');


--
-- TOC entry 1917 (class 0 OID 16708)
-- Dependencies: 164 1921
-- Data for Name: groupmember; Type: TABLE DATA; Schema: public; Owner: site
--

INSERT INTO groupmember VALUES ('50413BBB-DB86-402E-9E98-C7E73F219827', '2013-03-29 19:11:42.986', '1DE5D850-B015-44C1-904C-86DC2B0276A3', 'A591FCB8-772E-4157-B64B-4371A6C7CAE0');


--
-- TOC entry 1918 (class 0 OID 16750)
-- Dependencies: 165 1921
-- Data for Name: postaladdress; Type: TABLE DATA; Schema: public; Owner: site
--

INSERT INTO postaladdress VALUES ('CFE997C0-3FAF-4F6C-BBED-DB09689936B6', '-', '-', '00000', '-', 'Helsinki', 'Finland');
INSERT INTO postaladdress VALUES ('4EA7E643-3C80-49B2-8D1C-AAFA7E66A28C', '-', '-', '00000', '-', 'Helsinki', 'Finland');


--
-- TOC entry 1920 (class 0 OID 16952)
-- Dependencies: 167 1921
-- Data for Name: privilege; Type: TABLE DATA; Schema: public; Owner: site
--



--
-- TOC entry 1919 (class 0 OID 16774)
-- Dependencies: 166 1921
-- Data for Name: user_; Type: TABLE DATA; Schema: public; Owner: site
--

INSERT INTO user_ VALUES ('A591FCB8-772E-4157-B64B-4371A6C7CAE0', 'Test', '+123', '2013-03-29 18:21:23.769', 'admin@admin.org', 'User', '2013-03-29 18:21:23.769', '3248528E-4D90-41F7-968F-AF255AD16901', 'c8213c753f70e6ef82a3bbece671c183cc9aa70d944f2d8abbbc50ab7432f2b4');


--
-- TOC entry 1882 (class 2606 OID 16788)
-- Dependencies: 161 161 1922
-- Name: company_pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY company
    ADD CONSTRAINT company_pkey PRIMARY KEY (companyid);


--
-- TOC entry 1884 (class 2606 OID 16790)
-- Dependencies: 162 162 1922
-- Name: customer_pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customerid);


--
-- TOC entry 1886 (class 2606 OID 16792)
-- Dependencies: 163 163 1922
-- Name: group__pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY group_
    ADD CONSTRAINT group__pkey PRIMARY KEY (groupid);


--
-- TOC entry 1890 (class 2606 OID 16794)
-- Dependencies: 164 164 1922
-- Name: groupmember_pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY groupmember
    ADD CONSTRAINT groupmember_pkey PRIMARY KEY (groupmemberid);


--
-- TOC entry 1894 (class 2606 OID 16802)
-- Dependencies: 165 165 1922
-- Name: postaladdress_pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY postaladdress
    ADD CONSTRAINT postaladdress_pkey PRIMARY KEY (postaladdressid);


--
-- TOC entry 1900 (class 2606 OID 16959)
-- Dependencies: 167 167 1922
-- Name: privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT privilege_pkey PRIMARY KEY (privilegeid);


--
-- TOC entry 1888 (class 2606 OID 16810)
-- Dependencies: 163 163 163 1922
-- Name: unq_group__0; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY group_
    ADD CONSTRAINT unq_group__0 UNIQUE (owner_companyid, name);


--
-- TOC entry 1892 (class 2606 OID 16812)
-- Dependencies: 164 164 164 1922
-- Name: unq_groupmember_0; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY groupmember
    ADD CONSTRAINT unq_groupmember_0 UNIQUE (user_userid, group_groupid);


--
-- TOC entry 1902 (class 2606 OID 16961)
-- Dependencies: 167 167 167 1922
-- Name: unq_privilege_0; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT unq_privilege_0 UNIQUE (user_userid, group_groupid);


--
-- TOC entry 1896 (class 2606 OID 16818)
-- Dependencies: 166 166 166 1922
-- Name: unq_user__0; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY user_
    ADD CONSTRAINT unq_user__0 UNIQUE (owner_companyid, emailaddress);


--
-- TOC entry 1898 (class 2606 OID 16820)
-- Dependencies: 166 166 1922
-- Name: user__pkey; Type: CONSTRAINT; Schema: public; Owner: site; Tablespace:
--

ALTER TABLE ONLY user_
    ADD CONSTRAINT user__pkey PRIMARY KEY (userid);


--
-- TOC entry 1903 (class 2606 OID 16841)
-- Dependencies: 165 1893 161 1922
-- Name: fk_company_deliveryaddress_postaladdressid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY company
    ADD CONSTRAINT fk_company_deliveryaddress_postaladdressid FOREIGN KEY (deliveryaddress_postaladdressid) REFERENCES postaladdress(postaladdressid);


--
-- TOC entry 1904 (class 2606 OID 16846)
-- Dependencies: 1893 161 165 1922
-- Name: fk_company_invoicingaddress_postaladdressid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY company
    ADD CONSTRAINT fk_company_invoicingaddress_postaladdressid FOREIGN KEY (invoicingaddress_postaladdressid) REFERENCES postaladdress(postaladdressid);


--
-- TOC entry 1905 (class 2606 OID 16851)
-- Dependencies: 165 162 1893 1922
-- Name: fk_customer_billingaddress_postaladdressid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT fk_customer_billingaddress_postaladdressid FOREIGN KEY (invoicingaddress_postaladdressid) REFERENCES postaladdress(postaladdressid);


--
-- TOC entry 1906 (class 2606 OID 16856)
-- Dependencies: 162 165 1893 1922
-- Name: fk_customer_deliveryaddress_postaladdressid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT fk_customer_deliveryaddress_postaladdressid FOREIGN KEY (deliveryaddress_postaladdressid) REFERENCES postaladdress(postaladdressid);


--
-- TOC entry 1907 (class 2606 OID 16861)
-- Dependencies: 161 162 1881 1922
-- Name: fk_customer_owner_companyid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT fk_customer_owner_companyid FOREIGN KEY (owner_companyid) REFERENCES company(companyid);


--
-- TOC entry 1908 (class 2606 OID 16866)
-- Dependencies: 1881 163 161 1922
-- Name: fk_group__owner_companyid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY group_
    ADD CONSTRAINT fk_group__owner_companyid FOREIGN KEY (owner_companyid) REFERENCES company(companyid);


--
-- TOC entry 1909 (class 2606 OID 16871)
-- Dependencies: 1885 163 164 1922
-- Name: fk_groupmember_group_groupid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY groupmember
    ADD CONSTRAINT fk_groupmember_group_groupid FOREIGN KEY (group_groupid) REFERENCES group_(groupid);


--
-- TOC entry 1910 (class 2606 OID 16876)
-- Dependencies: 166 1897 164 1922
-- Name: fk_groupmember_user_userid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY groupmember
    ADD CONSTRAINT fk_groupmember_user_userid FOREIGN KEY (user_userid) REFERENCES user_(userid);


--
-- TOC entry 1912 (class 2606 OID 16962)
-- Dependencies: 1885 163 167 1922
-- Name: fk_privilege_group_groupid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT fk_privilege_group_groupid FOREIGN KEY (group_groupid) REFERENCES group_(groupid);


--
-- TOC entry 1913 (class 2606 OID 16967)
-- Dependencies: 166 167 1897 1922
-- Name: fk_privilege_user_userid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT fk_privilege_user_userid FOREIGN KEY (user_userid) REFERENCES user_(userid);


--
-- TOC entry 1911 (class 2606 OID 16941)
-- Dependencies: 161 166 1881 1922
-- Name: fk_user__owner_companyid; Type: FK CONSTRAINT; Schema: public; Owner: site
--

ALTER TABLE ONLY user_
    ADD CONSTRAINT fk_user__owner_companyid FOREIGN KEY (owner_companyid) REFERENCES company(companyid);


--
-- TOC entry 1927 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 1929 (class 0 OID 0)
-- Dependencies: 161
-- Name: company; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE company FROM PUBLIC;
REVOKE ALL ON TABLE company FROM site;
GRANT ALL ON TABLE company TO site;
GRANT ALL ON TABLE company TO site;


--
-- TOC entry 1930 (class 0 OID 0)
-- Dependencies: 162
-- Name: customer; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE customer FROM PUBLIC;
REVOKE ALL ON TABLE customer FROM site;
GRANT ALL ON TABLE customer TO site;
GRANT ALL ON TABLE customer TO site;


--
-- TOC entry 1931 (class 0 OID 0)
-- Dependencies: 163
-- Name: group_; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE group_ FROM PUBLIC;
REVOKE ALL ON TABLE group_ FROM site;
GRANT ALL ON TABLE group_ TO site;
GRANT ALL ON TABLE group_ TO site;


--
-- TOC entry 1932 (class 0 OID 0)
-- Dependencies: 164
-- Name: groupmember; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE groupmember FROM PUBLIC;
REVOKE ALL ON TABLE groupmember FROM site;
GRANT ALL ON TABLE groupmember TO site;
GRANT ALL ON TABLE groupmember TO site;


--
-- TOC entry 1933 (class 0 OID 0)
-- Dependencies: 165
-- Name: postaladdress; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE postaladdress FROM PUBLIC;
REVOKE ALL ON TABLE postaladdress FROM site;
GRANT ALL ON TABLE postaladdress TO site;
GRANT ALL ON TABLE postaladdress TO site;


--
-- TOC entry 1934 (class 0 OID 0)
-- Dependencies: 166
-- Name: user_; Type: ACL; Schema: public; Owner: site
--

REVOKE ALL ON TABLE user_ FROM PUBLIC;
REVOKE ALL ON TABLE user_ FROM site;
GRANT ALL ON TABLE user_ TO site;
GRANT ALL ON TABLE user_ TO site;


-- Completed on 2013-03-29 23:53:41 EET

--
-- PostgreSQL database dump complete
--

CREATE TABLE schemaversion
(
  created timestamp without time zone NOT NULL,
  schemaname character varying(255) NOT NULL,
  schemaversion character varying(255) NOT NULL,
  CONSTRAINT schemaversion_pkey PRIMARY KEY (created )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schemaversion
  OWNER TO site;

INSERT INTO schemaversion VALUES (NOW(), 'bare', '0001');
