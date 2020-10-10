package ru.icerebro.nnbrestfull.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import ru.icerebro.nnbrestfull.database.JDBC.interfaces.BookJdbc;
import ru.icerebro.nnbrestfull.services.interfaces.MainService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;

@Service
public class MainServiceImpl implements MainService {

//    private final String prefix = "\\\\192.168.1.200\\MyShare";
    private final String prefix = "/booksdrive";

    private final BookJdbc bookJdbc;

    @Autowired
    public MainServiceImpl(BookJdbc bookJdbc) {
        this.bookJdbc = bookJdbc;
    }

    @Override
    public void downloadBook(String searchName, Integer bid, HttpServletResponse response) {

        String path = bookJdbc.getBookPath(searchName, bid, response);

        path = this.transformPath(path);

        if (path == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Path null: " + path);
            return;
        }

        File file = new File(prefix + path);

        if (!file.exists()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Path error: " + path);
            return;
        }

        sendFile(file,response);
    }

    private void sendFile(File file, HttpServletResponse response){
        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);


        String encodeResult;
        try {
            encodeResult = URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");
        }
        catch (IOException e){
            encodeResult = file.getName();
        }

        response.setHeader("Content-Disposition", "inline; filename*=utf-8''" + encodeResult);

        response.setContentLengthLong(file.length());

        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    private String transformPath(String path) {
        if (path != null){
            path = path.trim();

            if (path.length() > 0) {
                StringBuilder pathBuffer = new StringBuilder(path);
                if (pathBuffer.charAt(0) == '\"') {
                    pathBuffer.deleteCharAt(0);
                }
                if (pathBuffer.charAt(pathBuffer.length()-1) == '\"') {
                    pathBuffer.deleteCharAt(pathBuffer.length()-1);
                }


//                String template = "MyShare";
                String template = "NnbBooks";

                path = pathBuffer.substring(pathBuffer.indexOf(template) + template.length());

                path = path.replace('\\', '/');

                if (!new File(prefix + path).exists()){
                    return null;
                }
            }
        }

        return path;
    }
}
