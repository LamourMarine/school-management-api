-- Table teachers
CREATE TABLE teachers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Ajout de la colonne teacher_id dans courses
ALTER TABLE courses ADD COLUMN teacher_id BIGINT;
ALTER TABLE courses ADD CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id);