import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Задача 1: CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "data.csv";

        List<Employee> employeeList = parseCSV(columnMapping, fileName);
        String json = listToJson(employeeList);
        writeString(json, "data.json");

        //Задача 2: XML - JSON парсер
        List<Employee> employeeList2 = parseXML("data.xml");
        String json2 = listToJson(employeeList2);
        writeString(json2, "data2.json");

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy).build();

            return csvToBean.parse();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new Gson();

        return gson.toJson(list, listType);
    }

    public static void writeString(String data, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node rootNode = document.getDocumentElement();
            NodeList nodeList = rootNode.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    Employee employee = new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    );
                    employeeList.add(employee);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return employeeList;
    }
}