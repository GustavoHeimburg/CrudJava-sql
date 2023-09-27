package br.com.senac.contasfx.controller;

import br.com.senac.contasfx.model.Conta;
import br.com.senac.contasfx.repository.Contas;
import br.com.senac.contasfx.service.ContasCSVService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ContasController implements Initializable {

    @FXML
    private TextField txtConcessionaria;
    @FXML
    private TextField txtDescricao;
    @FXML
    private DatePicker dpDataVencimento;
    @FXML
    private TableView<Conta> tblContas;
    @FXML
    private TableColumn<Conta, String> clDesc;
    @FXML
    private TableColumn<Conta, String> clConc;
    @FXML
    private TableColumn<Conta, String> clVenc;
    @FXML
    private Button btnApagar;
    @FXML
    private Button btnSalvar;
    @FXML
    private Button btnAtualizar;
    @FXML
    private Button btnLimpar;
    private Contas contas;

    //Esse metodo é chamado ao inicializar a aplicação
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contas = Contas.getNewInstance();
        configuraColunas();
        configuraTela();
        atualizaDados();
    }

    private void configuraTela() {
        // esse binding só e false quando os campos da tela estão preenchidos
        BooleanBinding camposPreenchidos = txtConcessionaria.textProperty()
                        .isEmpty().or(txtDescricao.textProperty().isEmpty())
                        .or(dpDataVencimento.valueProperty().isNull());

        //Esse binding verifica se foi selecionada uma conta na tabela
        BooleanBinding contaSelecionada = tblContas.getSelectionModel()
                .selectedItemProperty().isNull();

        //Tratamento nos botões
        btnApagar.disableProperty().bind(contaSelecionada);
        btnAtualizar.disableProperty().bind(contaSelecionada);
        btnLimpar.disableProperty().bind(contaSelecionada);

        //O botao de salvar só e habilitadado se as informações forem
        //preenchidas corretamente
        btnSalvar.disableProperty().bind(contaSelecionada.not()
                .or(camposPreenchidos));

        tblContas.getSelectionModel().selectedItemProperty().addListener(
                (b, o, n) -> {
                  if(n != null){
                      LocalDate data = null;
                      data = n.getDataVencimento()
                              .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                      txtConcessionaria.setText(n.getConcessionaria());
                      txtDescricao.setText(n.getDescricao());
                      dpDataVencimento.setValue(data);
                  }
                }
        );
    }

    private void configuraColunas() {
        clConc.setCellValueFactory(new PropertyValueFactory<>("concessionaria"));
        clDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        clVenc.setCellValueFactory(
            cellData -> {
                Date data = cellData.getValue().getDataVencimento();
                DateFormat formataData = new SimpleDateFormat("dd/MM/YYYY");
                String dataFormatada = formataData.format(data);
                return new SimpleStringProperty(dataFormatada);
            }
        );
    }

    //Metodo salvar
    public void salvar(){
        Conta conta = new Conta();
        pegaValores(conta);
        contas.salvarConta(conta);
        atualizaDados();
    }

    //Metodo atualizar
    public void atualizar(){
        Conta c = tblContas.getSelectionModel().getSelectedItem();
        pegaValores(c);
        contas.atualizarConta(c);
        atualizaDados();
    }
    private void atualizaDados() {
        tblContas.getItems().setAll(contas.buscarTodasAsContas());
        limpar();
    }

    //Metodo responsavel por limpar os campos da tela
    public void limpar() {
        tblContas.getSelectionModel().select(null);
        txtConcessionaria.setText("");
        txtDescricao.setText("");
        dpDataVencimento.setValue(null);
    }

    //Metodo de Apagar
    public void apagar(){
        Conta c = tblContas.getSelectionModel().getSelectedItem();
        contas.apagarConta(c.getId());
        atualizaDados();
    }

    //pega os valores digitados pelo usuário lá na tela do sistema
    private void pegaValores(Conta conta) {
        conta.setConcessionaria(txtConcessionaria.getText());
        conta.setDescricao(txtDescricao.getText());
        conta.setDataVencimento(dataSelecionada());
    }

    //Responsavel por fazer a conversão da data que o usuário digitou na tela
    private Date dataSelecionada() {
        LocalDateTime time = dpDataVencimento.getValue().atStartOfDay();
        System.out.println(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
        return  Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }
}
