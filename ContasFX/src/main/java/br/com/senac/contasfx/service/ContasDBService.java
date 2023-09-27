package br.com.senac.contasfx.service;

import br.com.senac.contasfx.model.Conta;
import br.com.senac.contasfx.repository.Contas;

import java.nio.channels.ConnectionPendingException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContasDBService implements Contas {

    //DADOS PARA ACESSO AO BANCO
    final String USUARIO = "root";
    final String SENHA = "root";
    final String URL_BANCO = "jdbc:mysql://localhost:3306/senac_crud_contas_fx";
    final String CLASSE_DRIVER = "com.mysql.jdbc.Driver";

    final String INSERIR = "INSERT INTO conta(concessionaria, descricao, " +
            "data_vencimento) VALUES(?, ?, ?)";
    final String BUSCAR_TODAS = "SELECT id, concessionaria, descricao, " +
            "data_vencimento FROM conta";
    final String ATUALIZAR = "UPDATE conta SET concessionaria = ?, descricao = ?, " +
            "data_vencimento = ? WHERE id =?";
    final String BUSCAR = "SELECT id, concessionaria, descricao," +
            "data_vencimento FROM conta  WHERE id = ?";
    final String APAGAR = "DELETE FROM conta where id = ?";
    final String FORMATO_DATA = "yyyy-MM-dd";
    final SimpleDateFormat FORMATADOR = new SimpleDateFormat(FORMATO_DATA);

    //ABRE UMA CONEXAO COM O BANCO DE DADOS
    private Connection conexao(){
        try{
            Class.forName(CLASSE_DRIVER);
            return DriverManager.getConnection(URL_BANCO, USUARIO, SENHA);
        } catch (Exception e){
            e.printStackTrace();
            if(e instanceof ClassNotFoundException) {
                System.out.println("VERIFIQUE SE O DRIVER DO BANCO DE DADOS" +
                        "ESTA NO CLASSPATH DO PROJETO");
            } else {
                System.out.println("VERIFIQUE SE O BANCO DE DADOS ESTÁ" +
                        "RODANDO E SE OS DADOS DE CONEXAO ESTÃO CORRETOS");
            }
            System.exit(0);
            return null;
        }

    }

    //METODO SALVAR CONTA
    @Override
    public void salvarConta(Conta conta) {
        try {
            Connection con = conexao();
            PreparedStatement salvar = con.prepareStatement(INSERIR);
            String dateStr = formataDataDB(conta.getDataVencimento());
            salvar.setString(1, conta.getConcessionaria());
            salvar.setString(2, conta.getDescricao());
            salvar.setString(3, dateStr);
            salvar.executeUpdate();
            salvar.close();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERRO SALVAND0 CONTA");
            System.exit(0);
        }

    }

    private String formataDataDB(Date dataVencimento) {
        SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormatada = data.format(dataVencimento);
        return dataFormatada;
    }

    @Override
    public void atualizarConta(Conta conta) {
        try {
            Connection con = conexao();
            PreparedStatement atualizar = con.prepareStatement(ATUALIZAR);
            String dataStr = formataDataDB(conta.getDataVencimento());
            atualizar.setString(1, conta.getConcessionaria());
            atualizar.setString(2, conta.getDescricao());
            atualizar.setString(3, dataStr);
            atualizar.setInt(4, conta.getId());
            atualizar.executeUpdate();
            atualizar.close();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Erro atualizando conta com o id " + conta.getId());
            System.exit(0);
        }
    }

    @Override
    public void apagarConta(int id) {
        try{
            Connection con = conexao();
            PreparedStatement apagar = con.prepareStatement(APAGAR);
            apagar.setInt(1, id);
            apagar.executeUpdate();
            apagar.close();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERRO APAGANDO CONTA COM O ID " + id);
            System.exit(0);
        }
    }

    @Override
    public List<Conta> buscarTodasAsContas() {
        List<Conta> contas = new ArrayList<>();

        try {
            Connection con = conexao();
            PreparedStatement buscarTodos = con.prepareStatement(BUSCAR_TODAS);
            ResultSet resultadoBusca = buscarTodos.executeQuery();
            while (resultadoBusca.next()){
                Conta conta = extraiConta(resultadoBusca);
                contas.add(conta);
            }
            buscarTodos.close();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Erro buscando todas as contas.");
            System.exit(0);
        }

        return contas;
    }

    private Conta extraiConta(ResultSet resultadoBusca) throws SQLException,
            ParseException {
        Conta conta = new Conta();
        conta.setId(resultadoBusca.getInt(1));
        conta.setConcessionaria(resultadoBusca.getString(2));
        conta.setDescricao(resultadoBusca.getString(3));
        Date dataVencimento = FORMATADOR.
                parse(resultadoBusca.getString(4));
        conta.setDataVencimento(dataVencimento);
        return conta;
    }

    @Override
    public Conta buscarUmaConta(int id) {
        Conta conta = null;
        try {
            Connection con = conexao();
            PreparedStatement buscar = con.prepareStatement(BUSCAR);
            buscar.setInt(1, id);

            ResultSet resultadoBusca = buscar.executeQuery();
            resultadoBusca.next();
            conta = extraiConta(resultadoBusca);
            buscar.close();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERRO BUSCANDO UMA CONTA COM O ID" + id);
            System.exit(0);
        }
        return conta;
    }
}
