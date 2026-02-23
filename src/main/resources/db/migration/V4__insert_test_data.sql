-- Insertion de cours
INSERT INTO courses (title, teacher, code) VALUES ('Mathématiques', 'M. Dupont', 'MATH01');
INSERT INTO courses (title, teacher, code) VALUES ('Informatique', 'Mme Martin', 'INFO01');
INSERT INTO courses (title, teacher, code) VALUES ('Anglais', 'M. Smith', 'ANG01');

-- Insertion d'étudiants
INSERT INTO students (last_name, first_name) VALUES ('Dupont', 'Alice');
INSERT INTO students (last_name, first_name) VALUES ('Martin', 'Bob');
INSERT INTO students (last_name, first_name) VALUES ('Bernard', 'Clara');
INSERT INTO students (last_name, first_name) VALUES ('Leroy', 'Thomas');
INSERT INTO students (last_name, first_name) VALUES ('Moreau', 'Emma');
INSERT INTO students (last_name, first_name) VALUES ('Simon', 'Lucas');
INSERT INTO students (last_name, first_name) VALUES ('Laurent', 'Léa');
INSERT INTO students (last_name, first_name) VALUES ('Michel', 'Hugo');
INSERT INTO students (last_name, first_name) VALUES ('Garcia', 'Chloé');
INSERT INTO students (last_name, first_name) VALUES ('David', 'Nathan');

-- Insertion de notes
INSERT INTO grades (score, student_id, course_id) VALUES (15.5, 1, 1);
INSERT INTO grades (score, student_id, course_id) VALUES (12.0, 1, 2);
INSERT INTO grades (score, student_id, course_id) VALUES (18.0, 2, 1);
INSERT INTO grades (score, student_id, course_id) VALUES (9.5, 3, 3);
INSERT INTO grades (score, student_id, course_id) VALUES (14.0, 4, 1);
INSERT INTO grades (score, student_id, course_id) VALUES (16.5, 5, 2);
INSERT INTO grades (score, student_id, course_id) VALUES (11.0, 6, 3);
INSERT INTO grades (score, student_id, course_id) VALUES (19.0, 7, 1);
INSERT INTO grades (score, student_id, course_id) VALUES (13.5, 8, 2);
INSERT INTO grades (score, student_id, course_id) VALUES (17.0, 9, 3);