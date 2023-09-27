package br.com.senac.contasfx.repository;

import br.com.senac.contasfx.model.Conta;
import br.com.senac.contasfx.service.ContasCSVService;
import br.com.senac.contasfx.service.ContasDBService;

import java.util.List;

public interface Contas {

    //Salvar uma conta no arquivo
    public void salvarConta(Conta conta);

    //Atualizar uma conta no arquivo
    public void atualizarConta(Conta conta);

    //Apagar uma conta no arquivo
    public void apagarConta(int id);

    //Listar todas as contas do arquivo
    public List<Conta> buscarTodasAsContas();

    //Buscar uma conta no arquivo
    public Conta buscarUmaConta(int id);

    //esse metodo inicializa a classe responsavel por executar todos
    //os metodos disponiveis para o nosso usuário utilizar
    public static Contas getNewInstance() {
        //return new ContasCSVService();
        return new ContasDBService();
    }

}
