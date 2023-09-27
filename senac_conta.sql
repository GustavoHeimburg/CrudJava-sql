create database senac_crud_contas_fx;

use senac_crud_contas_fx;

create table conta(
	id bigint(20) auto_increment primary key not null,
    concessionaria varchar(500) not null,
    descricao longtext not null,
    data_vencimento date not null
);

select * from conta;