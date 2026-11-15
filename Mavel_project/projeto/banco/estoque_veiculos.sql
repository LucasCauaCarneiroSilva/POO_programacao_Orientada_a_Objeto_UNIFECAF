-- Sistema Automotivo - Gestão de Estoque de Veículos
-- Cria o banco, as tabelas e os dados iniciais (MySQL 8).
-- Atenção: apaga e recria o banco estoque_veiculos.

DROP DATABASE IF EXISTS estoque_veiculos;
CREATE DATABASE estoque_veiculos DEFAULT CHARACTER SET utf8mb4;
USE estoque_veiculos;

-- ---------------------------------------------------------------- Tabelas

CREATE TABLE tipo_veiculo (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  nome          VARCHAR(40)  NOT NULL,
  criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_tipo_nome UNIQUE (nome)
) ENGINE = InnoDB;

CREATE TABLE marca (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  nome          VARCHAR(60)  NOT NULL,
  criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_marca_nome UNIQUE (nome)
) ENGINE = InnoDB;

CREATE TABLE modelo (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  nome          VARCHAR(60)  NOT NULL,
  marca_id      BIGINT       NOT NULL,
  tipo_id       BIGINT       NOT NULL,
  criado_em     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  atualizado_em DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_modelo_marca_nome UNIQUE (marca_id, nome),
  CONSTRAINT fk_modelo_marca FOREIGN KEY (marca_id) REFERENCES marca (id),
  CONSTRAINT fk_modelo_tipo  FOREIGN KEY (tipo_id)  REFERENCES tipo_veiculo (id)
) ENGINE = InnoDB;

CREATE TABLE veiculo (
  id            BIGINT         NOT NULL AUTO_INCREMENT,
  modelo_id     BIGINT         NOT NULL,
  ano           INT            NOT NULL,
  cor           VARCHAR(30)    NOT NULL,
  preco         DECIMAL(12, 2) NOT NULL,
  quilometragem INT            NOT NULL,
  status        VARCHAR(20)    NOT NULL DEFAULT 'DISPONIVEL',
  criado_em     DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  atualizado_em DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_veiculo_modelo FOREIGN KEY (modelo_id) REFERENCES modelo (id),
  CONSTRAINT ck_veiculo_status CHECK (status IN ('DISPONIVEL', 'RESERVADO', 'VENDIDO', 'DESCONTINUADO')),
  CONSTRAINT ck_veiculo_preco  CHECK (preco > 0),
  CONSTRAINT ck_veiculo_km     CHECK (quilometragem >= 0)
) ENGINE = InnoDB;

-- Índices usados pelos filtros
CREATE INDEX ix_veiculo_status ON veiculo (status);
CREATE INDEX ix_veiculo_ano    ON veiculo (ano);
CREATE INDEX ix_veiculo_preco  ON veiculo (preco);

-- Histórico (sem chave estrangeira: fica salvo mesmo após remover o veículo)
CREATE TABLE historico_alteracao (
  id          BIGINT        NOT NULL AUTO_INCREMENT,
  data_hora   DATETIME(6)   NOT NULL,
  usuario     VARCHAR(60)   NOT NULL,
  entidade    VARCHAR(30)   NOT NULL,
  registro_id BIGINT        NULL,
  acao        VARCHAR(12)   NOT NULL,
  registro    VARCHAR(200)  NOT NULL,
  detalhes    VARCHAR(2000) NULL,
  motivo      VARCHAR(200)  NULL,
  PRIMARY KEY (id),
  CONSTRAINT ck_historico_acao CHECK (acao IN ('INCLUSAO', 'ALTERACAO', 'EXCLUSAO'))
) ENGINE = InnoDB;

CREATE INDEX ix_historico_registro ON historico_alteracao (entidade, registro_id);

-- ---------------------------------------------------------------- Dados iniciais
-- Podem ser alterados ou excluídos pela aplicação.

INSERT INTO tipo_veiculo (id, nome) VALUES
  (1, 'Hatch'), (2, 'Sedã'), (3, 'SUV'), (4, 'Picape'), (5, 'Moto');

INSERT INTO marca (id, nome) VALUES
  (1, 'Chevrolet'), (2, 'Fiat'), (3, 'Honda'), (4, 'Hyundai'), (5, 'Toyota'), (6, 'Volkswagen');

INSERT INTO modelo (id, nome, marca_id, tipo_id) VALUES
  (1,  'Onix',    1, 1),
  (2,  'Tracker', 1, 3),
  (3,  'Argo',    2, 1),
  (4,  'Strada',  2, 4),
  (5,  'CG 160',  3, 5),
  (6,  'HB20',    4, 1),
  (7,  'Corolla', 5, 2),
  (8,  'Hilux',   5, 4),
  (9,  'Polo',    6, 1),
  (10, 'T-Cross', 6, 3);

INSERT INTO veiculo (modelo_id, ano, cor, preco, quilometragem, status) VALUES
  (1,  2023, 'Branco',   82900.00,  18500, 'DISPONIVEL'),
  (1,  2021, 'Prata',    64900.00,  52300, 'VENDIDO'),
  (2,  2024, 'Preto',   139900.00,   6200, 'DISPONIVEL'),
  (3,  2022, 'Vermelho', 71500.00,  31000, 'RESERVADO'),
  (4,  2023, 'Branco',  109900.00,  22800, 'DISPONIVEL'),
  (5,  2024, 'Vermelho', 17900.00,   3100, 'DISPONIVEL'),
  (6,  2020, 'Cinza',    58900.00,  68400, 'DISPONIVEL'),
  (7,  2022, 'Prata',   142500.00,  40100, 'DISPONIVEL'),
  (8,  2019, 'Branco',  189000.00,  98700, 'DESCONTINUADO'),
  (9,  2023, 'Azul',     89900.00,  15400, 'DISPONIVEL'),
  (10, 2024, 'Cinza',   149900.00,   9800, 'RESERVADO');

-- ---------------------------------------------------------------- Usuário da aplicação
-- A aplicação conecta com este usuário (não usa o root).
CREATE USER IF NOT EXISTS 'estoque'@'localhost' IDENTIFIED BY 'estoque123';
ALTER USER 'estoque'@'localhost' IDENTIFIED BY 'estoque123';
GRANT SELECT, INSERT, UPDATE, DELETE ON estoque_veiculos.* TO 'estoque'@'localhost';
FLUSH PRIVILEGES;
