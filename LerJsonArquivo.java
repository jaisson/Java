import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class LerJsonArquivo {
  public static JSONObject getDataBase() {
    JSONObject confJson = lerJsonConf();
    JSONObject dataBasa = (JSONObject) confJson.get("nome");

    return dataBasa;
  }
  
  private static JSONObject lerJsonConf() {
    String caminho = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    String arqConf = "dados.json";
    String confgDB = caminho + File.separator + arqConf;

    try (FileReader fileReader = new FileReader(confgDB)) {
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      List<String> collect = bufferedReader.lines().collect(Collectors.toList());
      StringBuilder jsonTemp = new StringBuilder();
      for (String s : collect) {
        jsonTemp.append(s);
      }

      return new JSONObject(jsonTemp.toString());
    } catch (IOException ex) {
      Logger.getLogger(ConfigLocal.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }
}
