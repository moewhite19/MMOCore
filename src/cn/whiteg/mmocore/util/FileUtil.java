package cn.whiteg.mmocore.util;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtil {
    public static void writeStringToFile(File file,String data,Charset encoding) throws IOException {
        writeStringToFile(file,data,encoding,false);
    }

    public static void writeStringToFile(File file,String data,Charset encoding,boolean append) throws IOException {
        FileOutputStream out = null;

        try{
            out = openOutputStream(file,append);
            IOUtils.write(data,out,encoding);
            out.close();
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    public static FileOutputStream openOutputStream(File file,boolean append) throws IOException {
        if (file.exists()){
            if (file.isDirectory()){
                throw new IOException("File '" + file + "' exists but is a directory");
            }

            if (!file.canWrite()){
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()){
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }

        return new FileOutputStream(file,append);
    }
}
