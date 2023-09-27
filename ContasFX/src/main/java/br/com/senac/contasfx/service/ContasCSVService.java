package br.com.senac.contasfx.service;

import br.com.senac.contasfx.model.Conta;
import br.com.senac.contasfx.repository.Contas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class ContasCSVService implements Contas {

    //divisor de colunas no arquivo ;
    private static final String  SEPARADOR = ";";

    //o caminho para o arquivo na aplicação
    //PATH utiliza pra manipulação de arquivo
    private static final Path ARQUIVO_SAIDA = Paths.get("./dados.csv");
    private List<Conta> contas = new ArrayList<>();

    final SimpleDateFormat formatoData =
            new SimpleDateFormat("dd/MM/yyyy");

    public ContasCSVService(){
        carregaDados();
    }

    @Override
    public void salvarConta(Conta conta) {
        conta.setId(ultimoId() + 1);
        contas.add(conta);
        salvaDados();
    }

    @Override
    public void atualizarConta(Conta conta) {
        Conta contaAntiga = buscaPorId(conta.getId());
        contaAntiga.setConcessionaria(conta.getConcessionaria());
        contaAntiga.setDescricao(conta.getDescricao());
        contaAntiga.setDataVencimento(conta.getDataVencimento());
        salvaDados();
    }

    @Override
    public void apagarConta(int id) {
        Conta conta = buscaPorId(id);
        contas.remove(conta);
        salvaDados();
    }

    @Override
    public List<Conta> buscarTodasAsContas() {
        //VOID SIGNIFICA QUE NÃO PRECISA TER UM RETURN
        for (Conta conta: contas) {
            System.out.println(conta.getConcessionaria());
        }
        return contas;
    }

    //ESSE METODO VAI BUSCAR UMA CONTA NA LISTA DE CONTAS
    @Override
    public Conta buscarUmaConta(int id) {
        return buscaPorId(id);
    }

    //VAI VERIFICAR SE O ARQUIVO dados.csv JA EXISTE NO SISTEMA
    //SE O ARQUIVO JA EXISTE NO SISTEMA NÃO É CRIADO NOVAMENTE
    private void carregaDados() {
        try {
            if (!Files.exists(ARQUIVO_SAIDA)) {
                Files.createFile(ARQUIVO_SAIDA);
            }

            contas = Files.lines(ARQUIVO_SAIDA).map(this::leLinha)
                    .collect(Collectors.toList());
        } catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    //Le o arquivo pegando todas as linhas e convertendo para objeto
    //do tipo conta
    private Conta leLinha(String linha) {
        String colunas[] = linha.split(SEPARADOR);
        int id = Integer.parseInt(colunas[0]);
        Date dataVencimento = null;
        try {
            dataVencimento = formatoData.parse(colunas[3]);
        } catch (ParseException e){
            e.printStackTrace();
            System.exit(0);
        }
        Conta conta = new Conta();
        conta.setId(id);
        conta.setConcessionaria(colunas[1]);
        conta.setDescricao(colunas[2]);
        conta.setDataVencimento(dataVencimento);

        return conta;
    }

    //ESSE METODO É RESPONSAVEL POR BUSCAR LÁ NO NOSSO ARQUIVO dados.csv
    //O ID DA CONTA MAIS ALTA PARA QUE POSSAMOS INCREMENTAR O NOSSO CONTADOR
    //DE REGEISTROS
    private int ultimoId() {
        if(contas == null){
            System.out.println("Passou no if e era pra retorna 0");
            return 0;
        } else {
            System.out.println("Passou no if e era pra retorna o id da conta");
            return contas.stream().mapToInt(Conta::getId).max().orElse(0);
        }
    }

    //SALVAR A LISTA DE DADOS NO ARQUIVO dados.csv
    //ESCREVENDO COMPLETAMENTO TODOS OS DADOS NO ARQUIVO dados.csv
    private void salvaDados() {
        StringBuffer sb = new StringBuffer();
        for(Conta c : contas){
            String linha = criaLinha(c);
            sb.append(linha);
            sb.append(System.getProperty("line.separator"));
        }
        try{
          Files.delete(ARQUIVO_SAIDA);
          Files.write(ARQUIVO_SAIDA, sb.toString().getBytes());
        } catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    //PREPARA TODOS OS DADOS QUE O O USUARIO PREENCHEU NA CONTA
    private String criaLinha(Conta conta) {
        String dataVencimentoStr = formatoData.format(conta.getDataVencimento());
        String idStr = String.valueOf(conta.getId());

        //FINALIZA CRIAÇÃO DA LINHA
        String linha = String.join(SEPARADOR, idStr, conta.getConcessionaria(),
                conta.getDescricao(), dataVencimentoStr);
        return linha;
    }

    // METODO RESPONSAVEL POR BUSCAR UMA CONTA JÁ SALVA NO ARQUIVO
    private Conta buscaPorId(int id) {
        return contas.stream().filter(c -> c.getId() == id).findFirst()
                .orElseThrow(() -> new Error("Conta não encontrada"));
    }
}
