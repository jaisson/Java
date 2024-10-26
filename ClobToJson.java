import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import oracle.sql.CLOB;

public class ClobToJson {
  public static void main(String[] args) {
    try {
      try (Connection conn = conexao.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        conn.setAutoCommit(false);
        try (ResultSet rs = pstmt.executeQuery()) {
          while (rs.next()) {
            clob = (CLOB) rs.getClob("DADOS");
            is = clob.getAsciiStream();

            try (FileOutputStream os = new FileOutputStream("dado.json")) {
              int size = clob.getChunkSize();
              
              byte buffer[] = new byte[size];
              int length;
              
              while ((length = is.read(buffer, 0, size)) != -1) {
                os.write(buffer, 0, length);
              }
              is.close();
              os.close();
            }
          }
          rs.close();
        } catch (IOException ex) {
          System.out.println("Erro de io: " + ex.getMessage());
        }

        pstmt.close();
        conn.close();
      }
    } catch (SQLException ex) {
      System.out.println("Erro: " + ex.getLocalizedMessage());
    }
  }
}
