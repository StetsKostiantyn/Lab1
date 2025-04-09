package lab1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XMLManager {

    public static void ExportToXml(Library library, OutputStream outputStream, Library.SortOption sortOption) {
        try {
            JAXBContext context = JAXBContext.newInstance(Library.class, Reader.class, Book.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Library libraryToExport = library.getExportableCopy(sortOption);
            marshaller.marshal(libraryToExport, outputStream);
            System.out.println("Data successfully exported to OutputStream");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void ExportToXml(Library library, String filePath, Library.SortOption sortOption){
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            ExportToXml(library, fos, sortOption);
            System.out.println("Data successfully exported to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Library ImportFromXml(InputStream inputStream) {
        try {
            JAXBContext context = JAXBContext.newInstance(Library.class, Reader.class, Book.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Library) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Library ImportFromXml(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return ImportFromXml(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}