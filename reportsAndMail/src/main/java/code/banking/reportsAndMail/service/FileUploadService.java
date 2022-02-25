package africode.banking.reportsAndMail.service;



import com.itextpdf.layout.border.Border;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Text;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.layout.element.Image;


import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;



import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 @Author Ella Ty Karambizi
 // * */
@Service
public class FileUploadService {
    MultipartFile uploadedfile;
    ArrayList<String[]> entries = new ArrayList<String[]>() ;
    String[] headerData;
    String openingBalance;
    String closingBalance;
    String email;
    String generatedPassowrd;
    EmailService emailService;
    String customerName;
    String filepath;
    PdfDocument pdfDoc;
    Image image;



    public void fileUpload(MultipartFile file) throws IOException {
   
        file.transferTo(new File(pathTo));
        
        File filetoRead = new File(pathToReadForProcess);
        uploadedfile = file;
    }

    public void readFile(File file) throws IOException {
        // reading the file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String firstLine = reader.readLine();
        headerData = firstLine.split(",");
        String secondLine = reader.readLine();
        email = headerData[1];
        customerName = headerData[0];
        String[] temp = secondLine.split("[*]");
        openingBalance = temp[6];
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ secondLine);
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + openingBalance);
        String everyOtherLine;
        while((everyOtherLine =reader.readLine()) != null) {
            // check if it is not closing balance
            if (everyOtherLine.contains("Closing Balance") || everyOtherLine.contains("CLOSING BALANCE")){
                closingBalance = everyOtherLine.split("[*]")[6];
            }
            else{
                String[] template = everyOtherLine.split("[*]");

                entries.add(template);
            }

        }

    }

    public void pdfGenerate () throws IOException, URISyntaxException {
        // getting the uploaded file
    
        String str = uploadedfile.getOriginalFilename();
         str = str.substring(0, str.length() - 4);
        
       // creating the PDF file

        //loading the itext document
        createDocumentWithItext();
        PDDocument document = PDDocument.load(new File("bankStatement.pdf"));
        generatedPassowrd = generatePassword();
        protectfile(document,generatedPassowrd);
        document.save(filepath);
        document.close();

    }

    public String generatePassword(){

        String input = headerData[4];
        String newpassword = input.substring(input.length() - 4);

        Random r = new Random();

        int result = r.nextInt(1000-1) + 1;

        String password;
                int a= (int)System.currentTimeMillis();

        password= ""+a+"@"+result;
        return newpassword;
    }

    public void protectfile(PDDocument pdd , String password) throws IOException {

        // step 2.Creating instance of AccessPermission
        // class
        AccessPermission accessPermission = new AccessPermission();

        // step 3. Creating instance of
        // StandardProtectionPolicy
        StandardProtectionPolicy standardProtectionPolicy
                = new StandardProtectionPolicy(password, password, accessPermission);

        // step 4. Setting the length of Encryption key
//        standardProtectionPolicy.setEncryptionKeyLength(128);

        // step 5. Setting the permission
        standardProtectionPolicy.setPermissions(accessPermission);

        // step 6. Protecting the PDF file
        pdd.protect(standardProtectionPolicy);

        System.out.println("PDF Encrypted successfully...");
        System.out.println("METHOD PROTECT FILE  WELL EXECUTED ==================================================");
    }



    private void createDocumentWithItext() throws IOException {
        // Creating a PdfWriter
        String dest = "bankStatement.pdf";
        PdfWriter writer = new PdfWriter(dest);
        // Creating a PdfDocument
       pdfDoc = new PdfDocument(writer);
        // Adding an empty page
//        pdfDoc.addNewPage();
        // Creating a Document
        Document document = new Document(pdfDoc);

        // adding the logo on top
        // Creating an ImageData object
 
        ImageData data = ImageDataFactory.create(imFile);

        // Creating an Image object
        image = new Image(data);
//        // Setting the position of the image to the center of the page
//        image.setFixedPosition(50, 750);
//
//        // Adding image to the document
//        document.add(image);
// adding the top content
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
       PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        Text dateText = new Text(formatter.format(date));
        Text ac = new Text("Account Statement" );
        Text a = new Text("Account :" +  headerData[4] );
        Text c = new Text("Customer:  "+ headerData[5]);
        Text cu = new Text("Currency:  "+ headerData[6]);
        Text customerName = new Text(headerData[0]);


        // Setting font of the text
        dateText.setFont(font);
        ac.setFont(font);
        a.setFont(font);
        c.setFont(font);
        cu.setFont(font);
        customerName.setFont(font);
        dateText.setFontSize(6);
        ac.setFontSize(6);
        a.setFontSize(6);
        c.setFontSize(6);
        cu.setFontSize(6);
        customerName.setFontSize(6);

        // Creating Paragraph




        // Adding text1 to the paragraph
        Cell dateCell = new Cell(0,2).add(formatter.format(date));
        dateCell.setFontSize(6);
        dateCell.setFont(font);
        dateCell.setBorder(Border.NO_BORDER);
        Cell accountStatementCell = new Cell(0,7).add("Account Statement");
        accountStatementCell.setFontSize(6);
        accountStatementCell.setFont(font);
        accountStatementCell.setBorder(Border.NO_BORDER);
        Cell accountNumberCell = new Cell(0,5).add("Account :" +  headerData[4]);
        accountNumberCell.setFontSize(6);
        accountNumberCell.setFont(font);
        accountNumberCell.setBorder(Border.NO_BORDER);
        Cell customerNameCell = new Cell(0,2).add(headerData[0]);
        customerNameCell.setFontSize(6);
        customerNameCell.setFont(font);
        customerNameCell.setBorder(Border.NO_BORDER);
        Cell customerNumberCell = new Cell(0,5).add("Customer:  "+ headerData[5]);
        customerNumberCell.setFontSize(6);
        customerNumberCell.setFont(font);
        customerNumberCell.setBorder(Border.NO_BORDER);
        Cell currencyCell = new Cell(0,7).add("Currency:  "+ headerData[6]);
        currencyCell.setFontSize(6);
        currencyCell.setFont(font);
        currencyCell.setBorder(Border.NO_BORDER);





        // Creating a table

        // list of heading menu
        ArrayList<String> menu = new ArrayList<String>();
        menu.add("Book Date");
        menu.add("Reference");
        menu.add("Description");
        menu.add("Value Date");
        menu.add("Debit Amount");
        menu.add("Credit Amount");
        menu.add(" Running Balance");


        float col = 70f;
        float logoWidth [] = {col,col,col,col,col,col,col};

        float [] pointColumnWidths = {70F, 70F, 70F,70F,70F,70F,70F};
//        new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        Table table = new Table(pointColumnWidths, true);
        Cell spaceCell = new Cell(0,7);
        spaceCell.setBorder(Border.NO_BORDER);

        Cell logoCell = new Cell(0,7).add(image);
        logoCell.setBorder(Border.NO_BORDER);

        table.addCell(logoCell);
        table.addCell(spaceCell);
        table.addCell(spaceCell);
        // Adding the date
        Cell datespace = new Cell (0,5);

        datespace.setBorder(Border.NO_BORDER);
        table.addCell(datespace);
        table.addCell(dateCell);

        table.addCell(spaceCell);
        // adding the title
        table.addCell(accountStatementCell);
        table.addCell(spaceCell);
        // adding the account number and the account name
        table.addCell(accountNumberCell);
        table.addCell(customerNameCell);
        table.addCell(spaceCell);
        //adding the customer number and the account name
        table.addCell(customerNumberCell);
        table.addCell(customerNameCell);
        table.addCell(spaceCell);
        // adding the currency
        table.addCell(currencyCell);
        table.addCell(spaceCell);

        for (int i =0; i<menu.size();i++){
            Cell newCell = new Cell().add(menu.get(i));
            newCell.setBold();
            newCell.setFontSize(6);
            newCell.setBorder(Border.NO_BORDER);
            newCell.setBackgroundColor(Color.LIGHT_GRAY);
//            table.addHeaderCell(newCell);
            table.addCell(newCell);

        }
        // adding the opening alance
        Cell openingBalanceCell = new Cell (0,7);
        openingBalanceCell.add("Opening balance  : " + openingBalance);
        openingBalanceCell.setBold();
        openingBalanceCell.setFontSize(6);
        openingBalanceCell.setBorder(Border.NO_BORDER);
        table.addCell(openingBalanceCell);
//  adding the table first before populating it because its a large table
        document.add(table);
        // populating the entry rows
        for(int i=0;i< entries.size();i++){
            if (i % menu.size() == 0) {

                // Flushes the current content, e.g. places it on the document.
                // Please bear in mind that the method (alongside complete()) make sense only for 'large tables'
                table.flush();

            }
                for (int j=0; j< entries.get(i).length;j++){
                    if (j > 3){
                        Cell dataCell = new Cell().add(entries.get(i)[j] +".00");
                        dataCell.setBold();
                        dataCell.setFontSize(6);
                        dataCell.setBorder(Border.NO_BORDER);
                        table.addCell(dataCell);
                    }
                    else {
                        Cell dataCell = new Cell().add(entries.get(i)[j] );
                        dataCell.setBold();
                        dataCell.setFontSize(6);
                        dataCell.setBorder(Border.NO_BORDER);
                        table.addCell(dataCell);
                    }

                }



        }
        System.out.println ("LOOP EXECUTED FUULY=======================");//        Cell newCell = new Cell().add("Closing Balance");
        Cell newCell1 = new Cell().add("Closing balance" );
        newCell1.setBold();
        newCell1.setFontSize(6);
        newCell1.setBorder(Border.NO_BORDER);
        table.addCell(newCell1);

        Cell newCell2 = new Cell().add(closingBalance );
        newCell2.setBold();
        newCell2.setFontSize(6);
        newCell2.setBorder(Border.NO_BORDER);
        table.addCell(newCell2);

// Where add table was .... closing the table here

        table.complete();

        // Closing the document
        document.close();
        System.out.println("METHOD createDoucmentwithitext  WELL EXECUTED ==================================================");

    }
    // cont the number of records in a table if the records are more than ten ... continue the table on another page

    public void processlistFilesForFolder(final String folderAdress, EmailService emailService) throws IOException, URISyntaxException {
        File folder = new File(folderAdress);
        for (final File fileEntry : folder.listFiles()) {
            readFile(fileEntry);


            FileInputStream input = new FileInputStream(fileEntry);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    fileEntry.getName(), "text/plain", IOUtils. toByteArray(input));

            fileUpload( multipartFile);
            pdfGenerate ();

           
                    "#################################################################################################";

            String body = "Dear " + customerName + "\n "+ "Please find attached your monthly bank account statement for Feb 2022."+ "\n"+
                    "use this password to open it " + generatedPassowrd;

            emailService.sendMailWithAttachment(email, "Your Bank Statement", newBody,filepath,headerData[4]);

            entries.clear();
            System.out.println("METHOD processlistFilesForFolder  WELL EXECUTED ==================================================");

        }
    }
}
