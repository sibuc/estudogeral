/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  UC - SIBUC - Ana Luisa Silva
 * Created: 27/Nov/2015
 * Adiciona a tabela eperson a coluna preferred_handles
 * para possibilitar a visualizacao de comunidades/colecoes preferidas
 * listadas automaticamente em MyDspace/A minha area de trabalho
 */
alter table eperson add column preferred_handles character varying(510);

