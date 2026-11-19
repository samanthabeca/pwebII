INSERT INTO TB_ADMINISTRADOR (id, cpf, nome, email) VALUES (1, '12345678907', 'Samantha', 'sam@mail.com');
INSERT INTO TB_ADMINISTRADOR (id, cpf, nome, email) VALUES (2, '12345678908', 'Paulo Freitas', 'paulo@mail.com');

INSERT INTO TB_PACIENTE (id, cpf, nome, telefone, email) VALUES (3, '12345678901', 'Rui Barbosa', '6332142050', 'rui@mail.com');
INSERT INTO TB_PACIENTE (id, cpf, nome, telefone, email) VALUES (4, '12345678902', 'Ana Clara', '6332182500','ana@mail.com');
INSERT INTO TB_PACIENTE (id, cpf, nome, telefone, email) VALUES (5, '12345678903', 'Beatriz Souza', '6332320080','bia@mail.com');

INSERT INTO TB_MEDICO (id, cpf, nome, crm, email) VALUES (6, '12345678904', 'Maria Souza', '2580/TO','maria@mail.com');
INSERT INTO TB_MEDICO (id, cpf, nome, crm, email) VALUES (7, '12345678905', 'Derick Silva', '3250/TO','derick@mail.com');
INSERT INTO TB_MEDICO (id, cpf, nome, crm, email) VALUES (8, '12345678906', 'Antonio Paiva', '5030/TO','toni@mail.com');

INSERT INTO TB_SECRETARIA (id, cpf, nome, email) VALUES (9, '12345678909', 'Ana Lima', 'analima@mail.com');
INSERT INTO TB_SECRETARIA (id, cpf, nome, email) VALUES (10, '12345678910', 'Joana Alves', 'joana@mail.com');

INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (1, '2026-08-20T15:30', 500.0, 'Hipertensão', 7, 3);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (2, '2026-08-21T16:30', 1500.0, 'Implanon', 6, 4);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (3, '2026-08-24T17:00', 500.0, 'Hipertensão', 8, 5);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (4, '2026-08-24T17:30', 700.0, 'Enjoo', 7, 4);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (5, '2026-08-25T09:20', 700.0, 'Procedimento na pele', 6, 5);

INSERT INTO PERFIL (id, nome) VALUES (1, 'ADMIN');
INSERT INTO PERFIL (id, nome) VALUES (2, 'SECRETARIA');
INSERT INTO PERFIL (id, nome) VALUES (3, 'MEDICO');
INSERT INTO PERFIL (id, nome) VALUES (4, 'PACIENTE');

INSERT INTO USUARIO (id, login, password, ativo) VALUES (1, 'sam@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (2, 'paulo@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (3, 'rui@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (4, 'ana@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (5, 'bia@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (6, 'maria@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (7, 'derick@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (8, 'toni@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (9, 'analima@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);
INSERT INTO USUARIO (id, login, password, ativo) VALUES (10, 'joana@mail.com', '$2a$10$1VXXvG1MrK4PfM.YfxNTaeg0AYu2EWUNI3ZdkYu9R9cfe0cpZ337a', true);

INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (1, '1');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (2, '1');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (3, '4');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (4, '4');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (5, '4');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (6, '3');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (7, '1');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (7, '3');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (8, '3');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (9, '2');
INSERT INTO USUARIO_PERFIL (usuario_id, perfil_id) VALUES (10, '2');

UPDATE TB_ADMINISTRADOR SET usuario_id = 1 WHERE id = 1;
UPDATE TB_ADMINISTRADOR SET usuario_id = 2 WHERE id = 2;
UPDATE TB_PACIENTE SET usuario_id = 3 WHERE id = 3;
UPDATE TB_PACIENTE SET usuario_id = 4 WHERE id = 4;
UPDATE TB_PACIENTE SET usuario_id = 5 WHERE id = 5;
UPDATE TB_MEDICO SET usuario_id = 6 WHERE id = 6;
UPDATE TB_MEDICO SET usuario_id = 7 WHERE id = 7;
UPDATE TB_MEDICO SET usuario_id = 8 WHERE id = 8;
UPDATE TB_SECRETARIA SET usuario_id = 9 WHERE id = 9;
UPDATE TB_SECRETARIA SET usuario_id = 10 WHERE id = 10;

ALTER SEQUENCE pessoa_seq RESTART WITH 11;
ALTER SEQUENCE tb_consulta_seq RESTART WITH 6;
ALTER TABLE usuario ALTER COLUMN id RESTART WITH 11;
