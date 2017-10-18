package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtilities {
    private Context context;
    private File tempFile;
    private String fileName;
    public FileUtilities(Context context) {
        this.context = context;
    }

    public String setFileName(Uri sourceURI) {
        File temp = new File(sourceURI.getPath());
        String unparsedFileName = temp.getName();
        String parsedFileName = unparsedFileName;
        //check if file name has an extension
        if (unparsedFileName.contains(".")) {
            if (unparsedFileName.lastIndexOf(".") != unparsedFileName.length() - 1) {
                parsedFileName = unparsedFileName.substring(0, unparsedFileName.lastIndexOf("."));
            }
        }
        parsedFileName = parsedFileName.replaceAll("\\s+", "");
        //Get the file extension of the source file given the content uri
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(contentResolver.getType(sourceURI));

        this.fileName = parsedFileName+"."+type;
        return parsedFileName + "." + type;
    }

    public File getTempFile() {
        return tempFile;
    }

    public String getFileName() {
        return fileName;
    }

    public File createTemporaryFile(Uri sourceURI, String fileName) {
        //Create a temporary file in cache, open URI as input stream and output to temporaryFile
        File temporaryFile = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(sourceURI)) {
            try (OutputStream outputStream = new FileOutputStream(temporaryFile)) {
                byte[] buffer = new byte[4 * 1024];

                int readLength;

                while ((readLength = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLength);
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.tempFile = temporaryFile;
        return temporaryFile;
    }
}
