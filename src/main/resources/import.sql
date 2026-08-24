INSERT INTO TB_MEDICO (id_medico, nome, crm) VALUES(1, 'Maria Souza', '2580/TO');
INSERT INTO TB_MEDICO (id_medico, nome, crm) VALUES(2, 'Derick Silva', '3250/TO');
INSERT INTO TB_MEDICO (id_medico, nome, crm) VALUES(3, 'Antonio Paiva', '5030/TO');

INSERT INTO TB_PACIENTE (id_paciente, nome, telefone) VALUES(1, 'Rui Barbosa', '6332142050');
INSERT INTO TB_PACIENTE (id_paciente, nome, telefone) VALUES(2, 'Ana Clara', '6332182500');
INSERT INTO TB_PACIENTE (id_paciente, nome, telefone) VALUES(3, 'Beatriz Souza', '6332320080');

INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES(1, '2026-08-20T15:30', 500.0, 'Hipertensão', 2,1);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES(2, '2026-08-21T16:30',1500.0, 'Implanon', 1,2);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES(3, '2026-08-24T17:00',500.0, 'Hipertensão', 3,3);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES(4, '2026-08-24T17:30',700.0, 'Enjoo', 2,2);
INSERT INTO TB_CONSULTA (id_consulta, data, valor, observacao, id_medico, id_paciente) VALUES(5, '2026-08-25T09:20',700.0, 'Procedimento na pele', 1,3);

ALTER TABLE TB_PACIENTE ALTER COLUMN id_paciente RESTART WITH 4;
ALTER TABLE TB_MEDICO ALTER COLUMN id_medico RESTART WITH 4;
ALTER TABLE TB_CONSULTA ALTER COLUMN id_consulta RESTART WITH 6;
