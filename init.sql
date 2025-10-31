DROP SCHEMA IF EXISTS proStage CASCADE;
CREATE SCHEMA proStage;

CREATE TABLE proStage.users (
                                user_id SERIAL PRIMARY KEY,
                                email TEXT NOT NULL,
                                lastname TEXT NOT NULL,
                                firstname TEXT NOT NULL,
                                phone_number TEXT NOT NULL,
                                password TEXT NOT NULL,
                                registration_date DATE NOT NULL,
                                school_year TEXT NOT NULL,
                                role TEXT NOT NULL,
                                version INTEGER NOT NULL
);

CREATE TABLE proStage.companies (
                                    company_id SERIAL PRIMARY KEY,
                                    name TEXT NOT NULL,
                                    designation TEXT,
                                    address TEXT NOT NULL,
                                    phone_number TEXT,
                                    email TEXT,
                                    is_blacklisted BOOLEAN NOT NULL,
                                    blacklist_motivation TEXT,
                                    version INTEGER NOT NULL
);

CREATE TABLE proStage.contacts (
                                   contact_id SERIAL PRIMARY KEY,
                                   company INTEGER NOT NULL REFERENCES proStage.companies (company_id),
                                   student INTEGER NOT NULL REFERENCES proStage.users (user_id),
                                   meeting TEXT,
                                   contact_state TEXT NOT NULL,
                                   reason_for_refusal TEXT,
                                   school_year TEXT NOT NULL,
                                   version INTEGER NOT NULL
);

CREATE TABLE proStage.supervisors (
                                      supervisor_id SERIAL PRIMARY KEY,
                                      company INTEGER NOT NULL REFERENCES proStage.companies (company_id),
                                      lastname TEXT NOT NULL,
                                      firstname TEXT NOT NULL,
                                      phone_number TEXT NOT NULL,
                                      email TEXT
);

CREATE TABLE proStage.internships (
                                      internship_id SERIAL PRIMARY KEY,
                                      contact INTEGER NOT NULL REFERENCES proStage.contacts (contact_id),
                                      supervisor INTEGER NOT NULL REFERENCES proStage.supervisors (supervisor_id),
                                      signature_date DATE NOT NULL,
                                      project TEXT,
                                      school_year TEXT NOT NULL,
                                      version INTEGER NOT NULL
);

-- COMPANIES
INSERT INTO proStage.companies VALUES (DEFAULT,'Assyst Europe', NULL, 'Avenue du Japon, 1/B9 1420 Braine-l alleud', '02.609.25.00', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'AXIS SRL', NULL, 'Avenue de l Hélianthe, 63 1180 Uccle', '02 752 17 60', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'Infrabel', NULL, 'Rue Bara, 135 1070 Bruxelles', '02 525 22 11', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'La route du papier', NULL, 'Avenue des Mimosas, 83	1150 Woluwe-Saint-Pierre', '02 586 16 65', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'LetsBuild', NULL, 'Chaussée de Bruxelles, 135A	1310 La Hulpe', '014 54 67 54', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'Niboo', NULL, 'Boulevard du Souverain, 24 1170 Watermael-Boisfort', '0487 02 79 13', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'Sopra Steria', NULL, 'Avenue Arnaud Fraiteur, 15/23 1050 Bruxelles', '02 566 66 66', NULL, FALSE, NULL, 1);
INSERT INTO proStage.companies VALUES (DEFAULT, 'The Bayard Partnership', NULL, 'Grauwmeer, 1/57 bte 55	3001 Leuven', '02 309 52 45', NULL, FALSE, NULL, 1);


-- SUPERVISORS
INSERT INTO proStage.supervisors VALUES (DEFAULT, 5, 'Dossche', 'Stéphanie', '014.54.67.54', 'stephanie.dossche@letsbuild.com');
INSERT INTO proStage.supervisors VALUES (DEFAULT, 7, 'Alvarez Corchete', 'Roberto', '02.566.60.14', NULL);
INSERT INTO proStage.supervisors VALUES (DEFAULT, 1, 'Assal', 'Farid', '0474 39 69 09', 'f.assal@assyst-europe.com');
INSERT INTO proStage.supervisors VALUES (DEFAULT, 4, 'Ile', 'Emile', '0489 32 16 54', NULL);
INSERT INTO proStage.supervisors VALUES (DEFAULT, 3, 'Hibo', 'Owln', '0456 678 567', NULL);
INSERT INTO proStage.supervisors VALUES (DEFAULT, 2, 'Barn', 'Henri', '02 752 17 60', NULL);


-- USERS
-- Administratif : Admin;10.
-- Professeur : Prof24,z
-- Etudiant : mdpuser.1
INSERT INTO proStage.users VALUES (DEFAULT, 'raphael.baroni@vinci.be', 'Baroni', 'Raphaël', '0481 01 01 01', '$2a$10$PYN3bx1NYTlU6Cgh0af6uebLCF8bi1klv.3OYF8lMkUPzFHDREn2q', '2020-09-21', '2020-2021', 'Professeur', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'brigitte.lehmann@vinci.be', 'Lehmann', 'Brigitte', '0482 02 02 02', '$2a$10$PYN3bx1NYTlU6Cgh0af6uebLCF8bi1klv.3OYF8lMkUPzFHDREn2q', '2020-09-21', '2020-2021', 'Professeur', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'laurent.leleux@vinci.be', 'Leleux', 'Laurent', '0483 03 03 03', '$2a$10$PYN3bx1NYTlU6Cgh0af6uebLCF8bi1klv.3OYF8lMkUPzFHDREn2q', '2020-09-21', '2020-2021', 'Professeur', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'annouck.lancaster@vinci.be', 'Lancaster', 'Annouck', '0484 04 04 04', '$2a$10$nM1Rhrp1hzBmYFiv38aDP.Hptfe.F0dO33Fxlse6rytu/4Q3HrmN2', '2020-09-21', '2020-2021', 'Administratif', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'elle.skile@student.vinci.be', 'Skile', 'Elle', '0491 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.ilotie@student.vinci.be', 'Ilotie', 'Basile', '0491 00 00 11', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.frilot@student.vinci.be', 'Frilot', 'Basile', '0491 00 00 21', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.ilot@student.vinci.be', 'Ilot', 'Basile', '0492 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'arnaud.dito@student.vinci.be', 'Dito', 'Arnaud', '0493 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'arnaud.dilo@student.vinci.be', 'Dilo', 'Arnaud', '0494 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'cedric.dilot@student.vinci.be', 'Dilot', 'Cedric', '0495 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'auristelle.linot@student.vinci.be', 'Linot', 'Auristelle', '0496 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2021-09-21', '2021-2022', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.demoulin@student.vinci.be', 'Demoulin', 'Basile', '0496 00 00 02', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-23', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'arthur.moulin@student.vinci.be', 'Moulin', 'Arthur', '0497 00 00 02', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-23', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'hugo.moulin@student.vinci.be', 'Moulin', 'Hugo', '0497 00 00 03', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-23', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'jeremy.demoulin@student.vinci.be', 'Demoulin', 'Jeremy', '0497 00 00 20', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-23', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'aurele.mile@student.vinci.be', 'Mile', 'Aurèle', '0497 00 00 21', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-23', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'frank.mile@student.vinci.be', 'Mile', 'Frank', '0497 00 00 75', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-27', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.dumoulin@student.vinci.be', 'Dumoulin', 'Basile', '0497 00 00 58', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-27', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'axel.dumoulin@student.vinci.be', 'Dumoulin', 'Axel', '0497 00 00 97', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2022-09-27', '2022-2023', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'caroline.line@student.vinci.be', 'Line', 'Caroline', '0486 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2023-09-18', '2023-2024', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'ach.ile@student.vinci.be', 'Ile', 'Achille', '0487 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2023-09-18', '2023-2024', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'basile.ile@student.vinci.be', 'Ile', 'Basile', '0488 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2023-09-18', '2023-2024', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'achille.skile@student.vinci.be', 'Skile', 'Achille', '0490 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2023-09-18', '2023-2024', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'carole.skile@student.vinci.be', 'Skile', 'Carole', '0489 00 00 01', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2023-09-18', '2023-2024', 'Etudiant', 1);
INSERT INTO proStage.users VALUES (DEFAULT, 'theophile.ile@student.vinci.be', 'Ile', 'Théophile', '0488 35 33 89', '$2a$10$urzXRSOmSZTcWZlC5fANp..vQlNFyLg6p22S2Ze.oKbcQeiuppwg2', '2024-03-01', '2023-2024', 'Etudiant', 1);


-- CONTACTS
INSERT INTO proStage.contacts VALUES (DEFAULT, 5, 25, 'A distance', 'accepté', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 22, 'Dans l entreprise', 'accepté', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 22, 'A distance', 'refusé', 'N ont pas accepté d avoir un entretien', '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 1, 23, 'Dans l entreprise', 'accepté', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 5, 23, 'A distance', 'suspendu', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 23, NULL, 'suspendu', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 23, 'Dans l entreprise', 'refusé', 'ne prennent qu un seul étudiant', '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 21, 'A distance', 'refusé', 'Pas d affinité avec le l ERP Odoo', '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 21, NULL, 'non suivi', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 5, 21, 'A distance', 'pris', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 26, NULL, 'initié', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 26, NULL, 'initié', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 5, 26, NULL, 'initié', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 24, NULL, 'initié', NULL, '2023-2024', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 4, 5, 'A distance', 'accepté', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 8, NULL, 'non suivi', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 8, 7, 'A distance', 'refusé', 'ne prennent pas de stage', '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 9, 'Dans l entreprise', 'accepté', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 10, 'Dans l entreprise', 'accepté', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 1, 11, 'Dans l entreprise', 'accepté', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 11, 'Dans l entreprise', 'refusé', 'Choix autre étudiant', '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 3, 12, 'A distance', 'accepté', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 12, NULL, 'suspendu', NULL, '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 12, 'A distance', 'refusé', 'Choix autre étudiant', '2021-2022', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 1, 16, 'A distance', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 2, 14, 'Dans l entreprise', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 2, 15, 'Dans l entreprise', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 2, 17, 'A distance', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 2, 18, 'A distance', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 2, 19, 'Dans l entreprise', 'refusé', 'Entretien n a pas eu lieu', '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 6, 19, 'Dans l entreprise', 'refusé', 'Entretien n a pas eu lieu', '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 19, 'A distance', 'refusé', 'Entretien n a pas eu lieu', '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 20, 'A distance', 'accepté', NULL, '2022-2023', 1);
INSERT INTO proStage.contacts VALUES (DEFAULT, 7, 7, 'A distance', 'refusé', 'Choix autre étudiant', '2022-2023', 1);


-- INTERNSHIPS
INSERT INTO proStage.internships VALUES (DEFAULT, 1, 1, '2023-10-10', 'Un ERP : Odoo', '2023-2024', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 2, 2, '2023-11-23', 'sBMS project - a complex environment', '2023-2024', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 4, 3, '2023-10-12', 'CRM : Microsoft Dynamics 365 For Sales', '2023-2024', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 15, 4, '2021-11-25', 'Conservation et restauration d’œuvres d’art', '2021-2022', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 18, 2, '2021-11-17', 'L analyste au centre du développement', '2021-2022', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 19, 2, '2021-11-17', 'L analyste au centre du développement', '2021-2022', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 20, 3, '2021-11-23', 'ERP : Microsoft Dynamics 366', '2021-2022', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 22, 5, '2021-11-22', 'Entretien des rails', '2021-2022', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 25, 3, '2022-11-23', 'CRM : Microsoft Dynamics 365 For Sales', '2022-2023', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 26, 6, '2022-10-19', 'Un métier : chef de projet', '2022-2023', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 27, 6, '2022-10-19', 'Un métier : chef de projet', '2022-2023', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 28, 6, '2022-10-19', 'Un métier : chef de projet', '2022-2023', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 29, 6, '2022-10-19', 'Un métier : chef de projet', '2022-2023', 1);
INSERT INTO proStage.internships VALUES (DEFAULT, 33, 2, '2022-10-17', 'sBMS project - Java Development', '2022-2023', 1);

SELECT COUNT(*) AS nombre_utilisateur, us.role AS role_utilisateur, us.school_year AS annee_academique FROM proStage.users us GROUP BY us.role, us.school_year;
SELECT int.school_year AS annee_academique, COUNT(*) AS nombre_stages FROM proStage.internships int GROUP BY int.school_year;
SELECT co.company_id AS id_entreprise, co.name AS nom_entreprise, COUNT(int.internship_id) AS nombre_stages, int.school_year AS annee_academique FROM proStage.companies co, proStage.internships int, proStage.contacts cn WHERE co.company_id = cn.company AND cn.contact_id = int.contact GROUP BY co.company_id, co.name, int.school_year;
SELECT ct.school_year AS annee_academique, COUNT(*) AS nombre_stages FROM proStage.contacts ct GROUP BY ct.school_year;
SELECT ct.contact_state AS etat_contact, COUNT(*) AS nombre_contacts FROM proStage.contacts ct GROUP BY ct.contact_state;
SELECT ct.school_year AS annee_academique, ct.contact_state AS etat_contact, COUNT(*) AS nombre_contacts FROM proStage.contacts ct GROUP BY ct.school_year, ct.contact_state;
SELECT co.company_id AS id_entreprise, co.name AS nom_entreprise, ct.contact_state AS etat_contact, COUNT(ct.contact_id) AS nombre_contacts FROM proStage.companies co, proStage.contacts ct WHERE co.company_id = ct.company GROUP BY co.company_id, co.name, ct.contact_state;