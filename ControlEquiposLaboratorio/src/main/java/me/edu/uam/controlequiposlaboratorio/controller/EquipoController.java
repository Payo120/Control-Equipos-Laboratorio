package me.edu.uam.controlequiposlaboratorio.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import me.edu.uam.controlequiposlaboratorio.model.Equipo;
import me.edu.uam.controlequiposlaboratorio.model.TipoEquipo;
import me.edu.uam.controlequiposlaboratorio.persistence.EquipoDAO;

import java.util.List;
import java.util.Optional;

public class EquipoController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtCantidad;
    @FXML
    private ComboBox<TipoEquipo> cmbTipo;
    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private TextField txtBuscarNombre;
    @FXML
    private ComboBox<TipoEquipo> cmbFiltroTipo;

    @FXML
    private TableView<Equipo> tablaEquipos;
    @FXML
    private TableColumn<Equipo, String> colCodigo;
    @FXML
    private TableColumn<Equipo, String> colNombre;
    @FXML
    private TableColumn<Equipo, String> colMarca;
    @FXML
    private TableColumn<Equipo, String> colEstado;
    @FXML
    private TableColumn<Equipo, Integer> colCantidad;
    @FXML
    private TableColumn<Equipo, String> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private Equipo equipoSeleccionado = null;
    private final List<String> estadosPermitidos = List.of("Disponible", "En mantenimiento", "Fuera de servicio");

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarCatalogos();
        cargarEquipos();

        tablaEquipos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual != null) {
                seleccionarEquipo(actual);
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colMarca.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMarca()));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado()));
        colCantidad.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTipoEquipo() != null ? cell.getValue().getTipoEquipo().getNombre() : ""
        ));
    }

    private void cargarCatalogos() {
        cmbEstado.setItems(FXCollections.observableArrayList(estadosPermitidos));
        try {
            List<TipoEquipo> tipos = equipoDAO.listarTipos();
            cmbTipo.setItems(FXCollections.observableArrayList(tipos));
            cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexion", "No se pudieron cargar los tipos de equipo: " + e.getMessage());
        }
    }

    private void cargarEquipos() {
        try {
            List<Equipo> lista = equipoDAO.listarEquipos();
            tablaEquipos.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al consultar", "No se pudo recuperar el inventario: " + e.getMessage());
        }
    }

    private void seleccionarEquipo(Equipo equipo) {
        this.equipoSeleccionado = equipo;
        txtId.setText(String.valueOf(equipo.getIdEquipo()));
        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca() != null ? equipo.getMarca() : "");
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));
        cmbEstado.setValue(equipo.getEstado());
        cmbTipo.setValue(equipo.getTipoEquipo());
    }

    @FXML
    public void onGuardar() {
        try {
            Equipo nuevo = construirYValidarEquipo(null);
            if (nuevo == null) return;

            equipoDAO.guardar(nuevo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operacion exitosa", "El equipo se registro correctamente.");
            onLimpiar();
            cargarEquipos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", "Fallo en la transaccion (se aplico rollback): " + e.getMessage());
        }
    }

    @FXML
    public void onActualizar() {
        if (equipoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccion requerida", "Seleccione un equipo de la tabla para actualizar.");
            return;
        }
        try {
            Equipo editado = construirYValidarEquipo(equipoSeleccionado.getIdEquipo());
            if (editado == null) return;

            editado.setIdEquipo(equipoSeleccionado.getIdEquipo());
            equipoDAO.actualizar(editado);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operacion exitosa", "El equipo se actualizo correctamente.");
            onLimpiar();
            cargarEquipos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", "Fallo en la transaccion (se aplico rollback): " + e.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        if (equipoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccion requerida", "Seleccione un equipo de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Esta seguro de que desea eliminar el equipo con codigo " + equipoSeleccionado.getCodigo() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                equipoDAO.eliminar(equipoSeleccionado.getIdEquipo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Operacion exitosa", "El equipo fue eliminado correctamente.");
                onLimpiar();
                cargarEquipos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", "No se pudo eliminar el registro: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onBuscar() {
        try {
            String texto = txtBuscarNombre.getText();
            TipoEquipo tipo = cmbFiltroTipo.getValue();
            List<Equipo> resultados = equipoDAO.buscarYFiltrar(texto, tipo);
            tablaEquipos.setItems(FXCollections.observableArrayList(resultados));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en consulta", "No se pudo aplicar el filtro: " + e.getMessage());
        }
    }

    @FXML
    public void onLimpiar() {
        equipoSeleccionado = null;
        txtId.clear();
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbEstado.setValue(null);
        cmbTipo.setValue(null);
        txtBuscarNombre.clear();
        cmbFiltroTipo.setValue(null);
        tablaEquipos.getSelectionModel().clearSelection();
        cargarEquipos();
    }

    private Equipo construirYValidarEquipo(Integer idActual) {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String marca = txtMarca.getText() != null ? txtMarca.getText().trim() : "";
        String cantidadStr = txtCantidad.getText() != null ? txtCantidad.getText().trim() : "";
        String estado = cmbEstado.getValue();
        TipoEquipo tipo = cmbTipo.getValue();

        if (codigo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "El codigo del equipo es obligatorio.");
            return null;
        }
        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "El nombre del equipo es obligatorio.");
            return null;
        }
        if (tipo == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "Debe seleccionar un tipo de equipo.");
            return null;
        }
        if (estado == null || !estadosPermitidos.contains(estado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "Seleccione un estado permitido (Disponible, En mantenimiento o Fuera de servicio).");
            return null;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "La cantidad debe ser un numero entero valido.");
            return null;
        }

        if (cantidad < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "La cantidad no puede ser negativa.");
            return null;
        }

        if (equipoDAO.existeCodigo(codigo, idActual)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validacion", "Ya existe un equipo registrado con el codigo: " + codigo);
            return null;
        }

        return new Equipo(codigo, nombre, marca, estado, cantidad, tipo);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}