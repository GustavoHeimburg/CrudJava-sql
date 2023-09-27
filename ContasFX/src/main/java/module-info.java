module br.com.senac.contasfx{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.com.senac.contasfx.view to javafx.fxml;
    exports br.com.senac.contasfx.view;

    opens br.com.senac.contasfx.controller to javafx.fxml;
    exports br.com.senac.contasfx.controller;

    opens br.com.senac.contasfx.model to javafx.fxml;
    exports br.com.senac.contasfx.model;
}