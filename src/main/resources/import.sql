INSERT INTO TB_PACIENTE (id, cpf, nome, telefone) VALUES (1, '12345678901', 'Rui Barbosa', '6332142050');
INSERT INTO TB_PACIENTE (id, cpf, nome, telefone) VALUES (2, '12345678902', 'Ana Clara', '6332182500');
INSERT INTO TB_PACIENTE (id, cpf, nome, telefone) VALUES (3, '12345678903', 'Beatriz Souza', '6332320080');

INSERT INTO TB_MEDICO (id, cpf, nome, crm) VALUES (4, '12345678904', 'Maria Souza', '2580/TO');
INSERT INTO TB_MEDICO (id, cpf, nome, crm) VALUES (5, '12345678905', 'Derick Silva', '3250/TO');
INSERT INTO TB_MEDICO (id, cpf, nome, crm) VALUES (6, '12345678906', 'Antonio Paiva', '5030/TO');

INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (1, '2026-08-20T15:30', 500.0, 'Hipertensão', 5, 1);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (2, '2026-08-21T16:30', 1500.0, 'Implanon', 4, 2);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (3, '2026-08-24T17:00', 500.0, 'Hipertensão', 6, 3);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (4, '2026-08-24T17:30', 700.0, 'Enjoo', 5, 2);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES (5, '2026-08-25T09:20', 700.0, 'Procedimento na pele', 4, 3);

ALTER SEQUENCE pessoa_seq RESTART WITH 7;
ALTER SEQUENCE tb_consulta_seq RESTART WITH 6;
