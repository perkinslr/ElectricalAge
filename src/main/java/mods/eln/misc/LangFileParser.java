package mods.eln.misc;

import mods.eln.Eln;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lambdaShade on 14.05.2015.
 */

public class LangFileParser {

    public enum RetStatus{ERR__IO_ERROR, ERR__BAD_HEADER, ERR__PARSING_ERROR, SUCCESS};

    private static final String headerStr = "#<ELN_LANGFILE_V1_0>";

    //Parse and complete missing keys in the file pointed by "filepath" (must be full path name).
    public static RetStatus parseAndFillFile(String filepath){
        try{
            File kvFile = new File(filepath);
            Boolean useExistingFile = (kvFile.exists() && kvFile.isFile());
            if(useExistingFile)
                useExistingFile &= (kvFile.length() < 1024*1024); //Limit existing input file at 1MiB

            //Parse existing file
            if(useExistingFile) {
                FileReader fileRd = new FileReader(filepath);
                BufferedReader fileBufRd = new BufferedReader(fileRd);
                String line = null;
                //Test first line (header)
                if((line = fileBufRd.readLine()) != null){
                    if(line.compareTo(headerStr) != 0)
                        return RetStatus.ERR__BAD_HEADER;
                }
                else
                    return RetStatus.ERR__BAD_HEADER;
                //Parse keys
                line = fileBufRd.readLine();
                while((line = fileBufRd.readLine()) != null) {
                    if(line == "")
                        continue;
                    int separatorIdx = line.indexOf("=");
                    int commentSeparatorIdx = line.indexOf("#");
                    //Check for comment or empty line (with spaces/tabs)
                    if(commentSeparatorIdx != -1)
                        commentSeparatorIdx = line.length();
                    if(commentSeparatorIdx<separatorIdx) {
                        for(int cIdx = 0 ; cIdx < commentSeparatorIdx ; cIdx++){
                            if(!((line.charAt(cIdx)==' ')&&(line.charAt(cIdx)=='\t')))
                                return RetStatus.ERR__PARSING_ERROR; //Malformed line.
                        }
                        continue; //Skip it!
                    }
                    //Key parsing
                    int startIdx = 0;
                    while((line.charAt(startIdx)==' ') || (line.charAt(startIdx)=='\t') || (startIdx < separatorIdx))
                        startIdx++;
                    if(startIdx==separatorIdx)
                        return RetStatus.ERR__PARSING_ERROR; //Empty key!
                    String strKey = line.substring(startIdx,separatorIdx);
                    if(strKey.contains(" ") || strKey.contains("\t"))
                        return RetStatus.ERR__PARSING_ERROR; //Key can't contains one or more space char
                    //Value parsing (if any)
                    String strValue = line.substring(separatorIdx+1);
                    Eln.langFile_DefaultKeys.put(strKey,strValue);
                }
                fileBufRd.close();
            }

            //Generate the new file
            FileWriter fileWr = new FileWriter(filepath);
            BufferedWriter fileBufWr = new BufferedWriter(fileWr);
            //Write the header line.
            fileBufWr.write(headerStr);
            fileBufWr.newLine();

            for(Map.Entry<String,String> item : Eln.langFile_DefaultKeys.entrySet()) {
                fileBufWr.write(item.getKey()+"="+item.getValue());
                fileBufWr.newLine();
            }
            fileBufWr.flush();
            fileBufWr.close();
        }
        catch(IOException e) {
            return RetStatus.ERR__IO_ERROR;
        }

        return RetStatus.SUCCESS;
    }

}
