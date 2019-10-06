package com.monotype.TestRailIntegration;

import org.apache.commons.lang.ObjectUtils;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.Scanner;

/**
 * Created by mittaln on 30-05-2017.
 */
public class FileOperation {


    //To check wheather a file exist or not
    public static boolean isFileExist(String fileLocationPath) {
        File f = new File(fileLocationPath);
        if (f.exists() && !f.isDirectory()) {
            return true;
        } else return false;
    }

    //To delete Test Run Setting File if Exist previously
    public static boolean deleteFileIfPersent(String fileLocationPath) {
        File file = new File(fileLocationPath);
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }
  //To create a new text file
    public static String createNewFile(String fileLocationPath) {
        try {
            File f = new File(fileLocationPath);
            if (f.createNewFile()) {
                return "Temporary File Created";
            } else {
                return "Temporary File not created";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }
//deprecated
    // Function to return RunId
    public static String getRunIdFromFile(String runDetailFilePath)
    {   String runId=" ";
        if(runDetailFilePath.contains("TestRailRunDetails"))
        {
            try {
                File in = new File(runDetailFilePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("=")) {
                        runId = line.substring(line.indexOf('=') + 1);
                    } else {
                        br.close();
                        return " TestRun Details File is not correct format! Please recheck it ";
                    }
                }
                br.close();
            }catch (FileNotFoundException e) {
                return e.getMessage();
            }catch(IOException e) {
                return e.getMessage();

            }
        }
        else if (runDetailFilePath.contains("RunId"))
        {
            try {
                File in = new File(runDetailFilePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    runId = line;
                    }
                br.close();

             }catch (FileNotFoundException e) {
                return e.getMessage();
             }catch(IOException e) {
                return e.getMessage();
             }
        }
        else
        {
            return "This is not a correct TestRunDetails File";

        }
        return "Test Run Id is:"+ runId;

    }
    //to append into a text file
    public static String writeToTextFile(String fileLocationPath, String testRailTestRunName,String testRailProjectId, String testRailSuiteId, String linkCasesBy,  String isFinalAttempt,String resultEmailRecipient,String runId,String retryCountNo)
    {
        try{
            File file = new File(fileLocationPath);
            Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            PrintWriter out = new PrintWriter(w);
            if(file.exists()==false){
                out.close();
                w.close();
                return "File Not Found";
            }
            if (testRailTestRunName.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Test Rail Run Name";
            }
            if (testRailProjectId.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Test Rail Project Id";
            }

            if (resultEmailRecipient.isEmpty()) {
                out.close();
                w.close();
                return "Please provide appropriate Email Recipient List";
            }
            if(linkCasesBy.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Link cases By parameter Value";
            }
            out.append("MT_RUN_TESTRAILTESTRUNNAME=" + testRailTestRunName+"\n");
            out.append("MT_RUN_TESTRAILPROJECTID=" + testRailProjectId+"\n");
            if(!testRailSuiteId.isEmpty()) {
                out.append("MT_RUN_TESTRAILSUITEID=" + testRailSuiteId+"\n");
            }
            out.append("MT_RUN_LINKCASESBY=" + linkCasesBy+"\n");
            out.append("MT_RUN_RESULTSPROCESSINGOUTCOMEEMAILRECIPIENTS=" + resultEmailRecipient+"\n");
            if(isFinalAttempt.equals("Yes")) {
                out.append("MT_RUN_ISFINALATTEMPT=" + "TRUE"+"\n");
            }
            else {
                out.append("MT_RUN_ISFINALATTEMPT=" + "FALSE"+"\n");
            }
            if(!runId.isEmpty()) {
                out.append("MT_RUN_TESTRAILTESTRUNID=" + runId+"\n");
            }
            if(!retryCountNo.isEmpty())
            {
                out.append("MT_RUN_RETRYCOUNTER=" + retryCountNo+"\n");
            }

            out.close();
            w.close();
            return "Parameters are written into temporary Text file";
           }catch(IOException e){
            return e.getMessage();

        }
    }

    //to append into a json file
    public static String writeToJsonFile(String fileLocationPath, String testRailTestRunName,String testRailProjectId, String testRailSuiteId, String linkCasesBy,  String isFinalAttempt, String resultEmailRecipient,String runId,String retryCountNo, String isSaveToTestRail)
    {
        try{
            File file = new File(fileLocationPath);
            Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            PrintWriter out = new PrintWriter(w);
            if(file.exists()==false){
                out.close();
                w.close();
                return "File Not Found";
            }
            if (testRailTestRunName.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Test Rail Run Name";
            }
            if (testRailProjectId.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Test Rail Project Id";
            }
            if (resultEmailRecipient.isEmpty()) {
                out.close();
                w.close();
                return "Please provide appropriate Email Recipient List";
            }
            if(linkCasesBy.isEmpty()) {
                out.close();
                w.close();
                return "Please provide Link cases By parameter Value";
            }
            JSONObject obj = new JSONObject();
            if(isSaveToTestRail.equals("true"))
            {
                obj.put("MT_RUN_SAVERESULTSTOTESTRAIL",true);
            }
            else
            {
                obj.put("MT_RUN_SAVERESULTSTOTESTRAIL",false);
            }
            obj.put("MT_RUN_TESTRAILTESTRUNNAME", testRailTestRunName);
            obj.put("MT_RUN_TESTRAILPROJECTID", Integer.parseInt(testRailProjectId));
            if(!testRailSuiteId.isEmpty()) {
                obj.put("MT_RUN_TESTRAILSUITEID", Integer.parseInt(testRailSuiteId));
            }
            obj.put("MT_RUN_LINKCASESBY", linkCasesBy);
            obj.put("MT_RUN_RESULTSPROCESSINGOUTCOMEEMAILRECIPIENTS", resultEmailRecipient);
            if(isFinalAttempt.equals("Yes")) {
                obj.put("MT_RUN_ISFINALATTEMPT", true);
            }
            else {
                obj.put("MT_RUN_ISFINALATTEMPT", false);
            }
            if(!runId.isEmpty()) {
                obj.put("MT_RUN_TESTRAILTESTRUNID", Integer.parseInt(runId));
            }
            if(!retryCountNo.isEmpty()) {
                obj.put("MT_RUN_RETRYCOUNTER", Integer.parseInt(retryCountNo));
            }
            w.write(obj.toJSONString());
            w.flush();
            out.close();
            w.close();
            return "Parameters are written into temporary JSON file";
        }catch(IOException e){
            return e.getMessage();
        }
    }


}
