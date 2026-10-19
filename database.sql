DROP DATABASE IF EXISTS course_management;
CREATE DATABASE course_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_management;

CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(160) NOT NULL,
                        description VARCHAR(500) NOT NULL,
                        credits INT NOT NULL
);

CREATE TABLE listener (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          full_name VARCHAR(160) NOT NULL,
                          email VARCHAR(120) NOT NULL,
                          phone VARCHAR(40) NOT NULL
);

CREATE TABLE enrollment (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            course_id BIGINT NOT NULL,
                            listener_id BIGINT NOT NULL,
                            state VARCHAR(30) NOT NULL,
                            f1 DOUBLE NOT NULL,
                            f2 DOUBLE NOT NULL,
                            f3 DOUBLE NOT NULL,
                            integral_score DOUBLE NOT NULL,
                            FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                            FOREIGN KEY (listener_id) REFERENCES listener(id) ON DELETE CASCADE
);

CREATE TABLE study_task (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            enrollment_id BIGINT NOT NULL,
                            title VARCHAR(200) NOT NULL,
                            max_score INT NOT NULL,
                            due_date VARCHAR(20) NOT NULL,
                            FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE
);

INSERT INTO course (title, description, credits) VALUES
                                                     ('Кросплатформене програмування', 'Вивчення Java, JavaFX, Servlets та MySQL', 5),
                                                     ('Бази даних та SQL', 'Проєктування реляційних баз даних', 4);

INSERT INTO listener (full_name, email, phone) VALUES
                                                   ('Шаптала Назар Миколайович', 'shaptala@example.com', '+380501112233'),
                                                   ('Петренко Іван Іванович', 'petrenko@example.com', '+380679998877');

INSERT INTO enrollment (course_id, listener_id, state, f1, f2, f3, integral_score) VALUES
                                                                                       (1, 1, 'REQUESTED', 80.0, 85.0, 90.0, 84.25),
                                                                                       (2, 2, 'APPROVED', 70.0, 75.0, 60.0, 69.25);

INSERT INTO study_task (enrollment_id, title, max_score, due_date) VALUES
                                                                       (1, 'Лабораторна робота №1', 100, '2026-10-25'),
                                                                       (1, 'Курсова робота Етап 1', 100, '2026-11-01');