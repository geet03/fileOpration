import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

  public static void main(String[] args) {
    String filePath = "/Users/geetanjali.khabale/practice/src/primary.txt"; // Specify the path to your text file
    Map<String, Set<String>> xyzDataMap = createMap(filePath);

    // Print the constructed map
    System.out.println("Primary Map: ");
    for (Map.Entry<String, Set<String>> entry : xyzDataMap.entrySet()) {
      System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
    }

    String abcFilePath = "/Users/geetanjali.khabale/practice/src/secondary.txt"; // Specify the path to your text file
    Map<String, Set<String>> abcDataMap = createMap(abcFilePath);

    // Print the constructed map
    System.out.println("Secondary Map: ");
    for (Map.Entry<String, Set<String>> entry : abcDataMap.entrySet()) {
      System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
    }


    for (Map.Entry<String, Set<String>> entry : abcDataMap.entrySet()) {
      if(xyzDataMap.containsKey(entry.getKey())) {
        Set<String> secondaryOpr = entry.getValue();
        Set<String> primaryOpr = xyzDataMap.get(entry.getKey());
        primaryOpr.addAll(secondaryOpr);
        xyzDataMap.put(entry.getKey(), primaryOpr);
      }
    }

    for (Map.Entry<String, Set<String>> entry : abcDataMap.entrySet()) {
      if (!xyzDataMap.containsKey(entry.getKey())) {
        xyzDataMap.put(entry.getKey(), entry.getValue());
      }
    }

    System.out.println("Result is : ");
    for (Map.Entry<String, Set<String>> entry : xyzDataMap.entrySet()) {
      System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
    }

    System.out.println("Updating the primary file with the new data");
    updatePrimaryFile(xyzDataMap);


  }

  private static void appendToFile(String filePath, Map<String, Set<String>> xyzDataMap) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Set<String>> entry : xyzDataMap.entrySet()) {
      sb.append(formatLine(entry.getKey(), entry.getValue())).append("\n");
    }


    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
      //bw.newLine();
      bw.write(sb.toString().trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void updatePrimaryFile(Map<String, Set<String>> xyzDataMap) {

    File filePath = new File("/Users/geetanjali.khabale/practice/src/primary.txt");
    List<String> updatedLines = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        if(!line.startsWith("Allow")){
          updatedLines.add(line);
          continue;
        }
        String[] parts = line.split(":");
        if (parts.length == 2) {
          String[] sourceTargetPair = parts[0].split("\\s+");
          String key = sourceTargetPair[1] +" " +sourceTargetPair[2] +" "+ parts[1].split("\\{")[0].trim().split("\\s+")[0];
          if (xyzDataMap.containsKey(key)) {
            String newLine = formatLine(key, xyzDataMap.get(key));
            updatedLines.add(newLine);
            xyzDataMap.remove(key);
          } else {
            updatedLines.add(line);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Write the updated content back to the same file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
      for (String line : updatedLines) {
        bw.write(line);
        bw.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    appendToFile(filePath.getAbsolutePath(), xyzDataMap);

  }

  private static String formatLine(String key, Set<String> strings) {
    //Allow source2 target2:class2 { opr1, opr3 }
    StringBuilder sb = new StringBuilder();
    String[] keys = key.split(" ");
    String keyLine = keys[0] + " " + keys[1] + ":" + keys[2]+" ";
    sb.append("Allow ").append(keyLine).append(" { ");
    String commaSeparatedValues = String.join(", ", strings);
    sb.append(commaSeparatedValues);
    sb.append(" }");
    return sb.toString();
  }

  private static Map<String, Set<String>> createMap(String filePath) {
    Map<String, Set<String>> dataMap = new HashMap<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        if(!line.startsWith("Allow")){
          continue;
        }
        line = line.replace(";", "");
        String[] parts = line.split(":");
        if (parts.length == 2) {
          String[] sourceTargetPair = parts[0].split("\\s+");
          parts[1] = parts[1].replace(",", "");
          String[] classOpsPair = parts[1].split("\\s+");
          String key = sourceTargetPair[1] +" " +sourceTargetPair[2] +" "+ classOpsPair[0];
          Set<String> values = new HashSet<>();
          for (int i = 1; i < classOpsPair.length; i++) {
            String operations = classOpsPair[i].replace("{", "").replace("}", "").trim();
            if(operations.isEmpty()){
              continue;
            }
            values.add(operations.trim());
          }
          dataMap.put(key, values);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dataMap;
  }
}
