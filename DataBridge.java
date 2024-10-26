/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package databridge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
//import java.util.UUID;
import org.json.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OraclePreparedStatement;

/**
 *
 * @author jaisson.duarte
 */
public class DataBridge {

  private static Conexao getDataSetPincipal() {
    JSONObject dataBase = ConfigLocal.getDataBase();

    String classDrive = dataBase.get("classDrive").toString();
    String url = dataBase.get("url").toString();
    String usuario = dataBase.get("usuario").toString();
    String senha = dataBase.get("senha").toString();

    Conexao conexao = new Conexao();
    conexao.setClassDrive(classDrive);
    conexao.setUrl(url);
    conexao.setUsuario(usuario);
    conexao.setSenha(senha);

    return conexao;
  }

  public static void gravarResult(String lote, String dataSet, String query, String dados) {
    Conexao conexao = getDataSetPincipal();

    try {
      String sql = "insert into z_datasource_result (nmlote, dsdatabase, dssql, dados) values (?, ?, ?, ?)";
      try (Connection conn = conexao.connect()) {
        conn.setAutoCommit(false);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, lote);
          pstmt.setString(2, dataSet);
          ((OraclePreparedStatement) pstmt).setStringForClob(3, query);
          ((OraclePreparedStatement) pstmt).setStringForClob(4, dados);

          pstmt.execute();
          conn.commit();

          pstmt.close();
        }
        conn.close();
      }
    } catch (SQLException ex) {
      System.out.println("Erro: " + ex.getLocalizedMessage());
    }
  }

  public static Conexao obterDataSet(String base) {
    Conexao conexao = new Conexao();
    Conexao principal = getDataSetPincipal();
    try {
      String sql = "SELECT * from z_datasource WHERE dsdatabase = ?";
      try (Connection conn = principal.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, base);
        try (ResultSet rs = pstmt.executeQuery()) {
          while (rs.next()) {
            conexao.setClassDrive(rs.getString("dsclassDrive"));
            conexao.setUrl(rs.getString("dsurl"));
            conexao.setUsuario(rs.getString("dsusuario"));
            conexao.setSenha(rs.getString("dssenha"));
          }
          rs.close();
        }
        conn.close();
        pstmt.close();
      }
    } catch (SQLException ex) {
      System.out.println("Erro: " + ex.getLocalizedMessage());
    }

    return conexao;
  }

  public static JSONArray consultar(Conexao conexao, String sql) {
    JSONArray dados = new JSONArray();

    try {
      try (Statement st = (Statement) conexao.connect().createStatement(); ResultSet rs = st.executeQuery(sql)) {
        if (rs.isBeforeFirst()) {
          try {
            while (rs.next()) {
              ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
              JSONArray linha = new JSONArray();
              for (int rg = 1; rg <= metaData.getColumnCount(); rg++) {
                JSONObject dado = new JSONObject();

                dado.put("coluna", metaData.getColumnName(rg));
                dado.put("tipo", metaData.getColumnTypeName(rg));
                dado.put("valor", rs.getObject(metaData.getColumnName(rg)).toString());

                linha.put(dado);
              }

              dados.put(linha);
            }
          } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
          }
        }

        rs.close();
        st.close();
      }

    } catch (SQLException ex) {
      Logger.getLogger(DataBridge.class.getName()).log(Level.SEVERE, null, ex);
    }

    return dados;
  }

  /**
   * @param args the command line arguments
   */
  /*public static void main(String[] args) {
    if (args.length > 0) {
      String tipoOpr = args[0];
      String loteExc = args[1];
      String dataSet = args[2];
      String fileSql = args[3];

      File file = new File(fileSql);
      try {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String consulta = new String(bytes, "UTF-8");

        Conexao conexao = obterDataSet(dataSet);
        if (tipoOpr.equals("1")) {
          String dados = consultar(conexao, consulta).toString();
          gravarResult(loteExc, dataSet, consulta, dados);
        }
      } catch (IOException ex) {
        Logger.getLogger(DataBridge.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
  }*/
}
