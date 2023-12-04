import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdministracionEstudiantes extends JFrame {

	private JPanel contentPane;
	private JTable tabla;
	private DefaultTableModel modelo;
	private JButton btnAnadir, btnEliminar, btnModificar;
	private JTextField txtID, txtNombre;
	private JTextField txtApellido;
	private JTextField txtCurso;
	private JTextField txtEdad;

	private Connection conexion;
	private String url = "jdbc:mysql://localhost:3306/registro_estudiantes"; // Reemplaza con tu URL de conexión
	private String usuario = "root";
	private String contraseña = "";

	private String idSeleccionado = "";

	public static void main(String[] args) {
		AdministracionEstudiantes frame = new AdministracionEstudiantes();
		frame.setVisible(true);
	}

	public AdministracionEstudiantes() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		setTitle("Administracion de estudiantes");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 673, 370);
		contentPane = new JPanel();
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				tabla.getSelectionModel().clearSelection();
				limpiarCampos();
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 42, 530, 278);
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				tabla.getSelectionModel().clearSelection();
				limpiarCampos();
			}
		});
		contentPane.add(scrollPane);

		tabla = new JTable();
		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				int fila = tabla.getSelectedRow();
				txtID.setText((String) modelo.getValueAt(fila, 0));
				txtNombre.setText((String) modelo.getValueAt(fila, 1));
				txtApellido.setText((String) modelo.getValueAt(fila, 2));
				txtEdad.setText((String) modelo.getValueAt(fila, 3));
				txtCurso.setText((String) modelo.getValueAt(fila, 4));
				updateEliminar();
				updateModificar();
				idSeleccionado = (String) modelo.getValueAt(fila, 0);
			}
		});
		modelo = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Nombre", "Apellido", "Edad", "Curso" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hace las celdas no editables
			}
		};
		tabla.setModel(modelo);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Deshabilita seleccion multiple para filas
		tabla.getTableHeader().setReorderingAllowed(false); // Deshabilita reordenar las columnas
		scrollPane.setViewportView(tabla);

		// BOTON AÑADIR
		btnAnadir = new JButton("Añadir");
		btnAnadir.setEnabled(false);
		btnAnadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelo.addRow(new String[] { txtID.getText(), txtNombre.getText(), txtApellido.getText(),
						txtEdad.getText(), txtCurso.getText() });
				guardarEstudiante();
				limpiarCampos();
				limpiarEstudiantesVacios();
			}
		});
		btnAnadir.setBounds(550, 44, 97, 23);
		contentPane.add(btnAnadir);

		// BOTON ELIMINAR
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setEnabled(false);
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int fila = tabla.getSelectedRow();
				try {
					eliminarEstudiante((String) modelo.getValueAt(fila, 0));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				modelo.removeRow(fila);
				limpiarCampos();
				guardarEstudiante();
				limpiarEstudiantesVacios();
			}
		});
		btnEliminar.setBounds(550, 112, 97, 23);
		contentPane.add(btnEliminar);

		// BOTON MODIFICAR
		btnModificar = new JButton("Modificar");
		btnModificar.setEnabled(false);
		btnModificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int fila = tabla.getSelectedRow();
				try {
					String id = txtID.getText();
					String nombre = txtNombre.getText();
					String apellido = txtApellido.getText();
					String edad = txtEdad.getText();
					String curso = txtCurso.getText();

					conectar();
					String query = "UPDATE estudiantes SET id=?, nombre=?, apellido=?, edad=?, curso=? WHERE id=?";
					PreparedStatement pstmt = conexion.prepareStatement(query);
					pstmt.setString(1, id);
					pstmt.setString(2, nombre);
					pstmt.setString(3, apellido);
					pstmt.setString(4, edad);
					pstmt.setString(5, curso);
					pstmt.setString(6, idSeleccionado); // Utilizamos la ID original para actualizar el registro

					pstmt.executeUpdate();
					pstmt.close();
					conexion.close();

					// Actualizar los datos en la tabla
					modelo.setValueAt(id, fila, 0);
					modelo.setValueAt(nombre, fila, 1);
					modelo.setValueAt(apellido, fila, 2);
					modelo.setValueAt(edad, fila, 3);
					modelo.setValueAt(curso, fila, 4);

					limpiarCampos();
					cargar();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				limpiarEstudiantesVacios();
			}
		});
		btnModificar.setBounds(550, 78, 97, 23);
		contentPane.add(btnModificar);

		// CAMPOS DE TEXTO
		txtID = new JTextField();
		txtID.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				updateAnadir();
				updateModificar();
			}
		});
		txtID.setBounds(10, 11, 98, 20);
		contentPane.add(txtID);
		txtID.setColumns(10);

		txtNombre = new JTextField();
		txtNombre.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateModificar();
			}
		});
		txtNombre.setColumns(10);
		txtNombre.setBounds(118, 11, 98, 20);
		contentPane.add(txtNombre);

		txtApellido = new JTextField();
		txtApellido.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateModificar();
			}
		});
		txtApellido.setColumns(10);
		txtApellido.setBounds(226, 11, 98, 20);
		contentPane.add(txtApellido);

		txtCurso = new JTextField();
		txtCurso.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateModificar();
			}
		});
		txtCurso.setColumns(10);
		txtCurso.setBounds(442, 11, 98, 20);
		contentPane.add(txtCurso);

		txtEdad = new JTextField();
		txtEdad.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateAnadir();
				updateModificar();
			}
		});
		txtEdad.setColumns(10);
		txtEdad.setBounds(334, 11, 98, 20);
		contentPane.add(txtEdad);
		cargar();

	}

	private void updateAnadir() {
		if (txtID.getText().length() == 0 || txtNombre.getText().length() == 0 || txtApellido.getText().length() == 0
				|| txtEdad.getText().length() == 0 || txtCurso.getText().length() == 0) {
			btnAnadir.setEnabled(false);
		} else {
			btnAnadir.setEnabled(true);
		}
	}

	private void updateEliminar() {
		if (tabla.getSelectedRow() == -1) {
			btnEliminar.setEnabled(false);
		} else {
			btnEliminar.setEnabled(true);
		}
	}

	private void updateModificar() {
		if (txtID.getText().length() == 0 || txtNombre.getText().length() == 0 || txtApellido.getText().length() == 0
				|| txtEdad.getText().length() == 0 || txtCurso.getText().length() == 0
				|| tabla.getSelectedRow() == -1) {
			btnModificar.setEnabled(false);
		} else {
			btnModificar.setEnabled(true);
		}
	}

	private void limpiarCampos() {
		txtID.setText(null);
		txtNombre.setText(null);
		txtApellido.setText(null);
		txtEdad.setText(null);
		txtCurso.setText(null);
		updateAnadir();
		updateEliminar();
		updateModificar();
	}

	// Método para establecer la conexión con la base de datos
	private void conectar() throws SQLException {
		conexion = DriverManager.getConnection(url, usuario, contraseña);
	}

	// Método para eliminar estudiantes de la base de datos
	public void eliminarEstudiante(String id) throws SQLException {
		conectar();
		String query = "DELETE FROM estudiantes WHERE id = ?";
		PreparedStatement pstmt = conexion.prepareStatement(query);
		pstmt.setString(1, id);
		pstmt.executeUpdate();
		pstmt.close();
		conexion.close();
	}

	// Método para guardar datos en la base de datos desde la tabla
	public void guardarEstudiante() {
		try {
			conectar();
			String query = "INSERT INTO estudiantes (id, nombre, apellido, edad, curso) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conexion.prepareStatement(query);
			pstmt.setString(1, txtID.getText());
			pstmt.setString(2, txtNombre.getText());
			pstmt.setString(3, txtApellido.getText());
			pstmt.setString(4, txtEdad.getText());
			pstmt.setString(5, txtCurso.getText());
			pstmt.executeUpdate();
			pstmt.close();
			conexion.close();
			limpiarCampos();
			cargar(); // Actualiza la tabla después de guardar
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Método para cargar datos desde la base de datos a la tabla
	public void cargar() {
		try {
			conectar();
			Statement stmt = conexion.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estudiantes");

			modelo.setRowCount(0); // Limpiar la tabla antes de cargar nuevos datos

			while (rs.next()) {
				String id = rs.getString("id");
				String nombre = rs.getString("nombre");
				String apellido = rs.getString("apellido");
				String edad = rs.getString("edad");
				String curso = rs.getString("curso");

				// Agregar los datos a la tabla
				modelo.addRow(new Object[] { id, nombre, apellido, edad, curso });
			}

			rs.close();
			stmt.close();
			conexion.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Metodo para eliminar estudiantes en blanco de la base de datos
	public void limpiarEstudiantesVacios() {
		try {
			conectar(); // limpiar la BBDD
			Statement stmt = conexion.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM estudiantes");

			while (rs.next()) {
				String id = rs.getString("id");
				String nombre = rs.getString("nombre");
				String apellido = rs.getString("apellido");
				String edad = rs.getString("edad");
				String curso = rs.getString("curso");

				// Si alguno de los campos está vacío, eliminar el estudiante
				if (id.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || curso.isEmpty()) {
					eliminarEstudiante(id); // Eliminar estudiante de la base de datos
				}
			}
			rs.close();
			stmt.close();
			conexion.close();

			cargar(); // Actualizar la tabla después de eliminar estudiantes vacíos
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}