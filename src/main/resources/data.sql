DROP TABLE IF EXISTS tb_pessoa;
CREATE TABLE tb_pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);
-- INSERT INTO TB_PESSOA VALUES(1, 'Samantha');
-- INSERT INTO TB_PESSOA VALUES(2, 'Juraci');
-- INSERT INTO TB_PESSOA VALUES(3, 'Jacira');

DROP TABLE IF EXISTS tb_departamento;
CREATE TABLE tb_departamento(
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                nome VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS tb_funcionario;
CREATE TABLE tb_funcionario(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    departamento_id int NOT NULL,
    salario FLOAT,
    constraint fk_funcionario_departamento FOREIGN KEY (departamento_id) REFERENCES tb_departamento(id)
);





