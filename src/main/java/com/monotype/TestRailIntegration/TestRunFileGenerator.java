package com.monotype.TestRailIntegration;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStep;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 @author nitin mittal
 */
public class TestRunFileGenerator extends Builder {


    private String testRunPropertyFilePath;
    private String testRailTestRunName;
    private String testRailProjectId;
    private String linkCasesBy;
    private String testRailSuiteId;
    private String isFinalAttempt;
    private String resultEmailRecipient;
    private String testRailRunDetailsFile;
    private String retryCountNo;
    private String isReRun;
    private String isSaveToTestRail;

    //Data Bound Constructor for using values from config.jelly
    @DataBoundConstructor
    public TestRunFileGenerator( String testRunPropertyFilePath, String testRailTestRunName, String testRailProjectId, String linkCasesBy,String testRailSuiteId,String isFinalAttempt, String resultEmailRecipient,String  testRailRunDetailsFile, String  retryCountNo,String jsonOrText, String isReRun, String isSaveToTestRail) {
        this.testRunPropertyFilePath = testRunPropertyFilePath;
        this.testRailTestRunName=testRailTestRunName;
        this.testRailProjectId=testRailProjectId;
        this.testRailSuiteId=testRailSuiteId;
        this.isFinalAttempt=isFinalAttempt;
        this.resultEmailRecipient=resultEmailRecipient;
        this.testRailRunDetailsFile=testRailRunDetailsFile;
        this.retryCountNo=retryCountNo;
        this.linkCasesBy=linkCasesBy;
        this.isReRun=isReRun;
        this.isSaveToTestRail=isSaveToTestRail;
    }

    //Properties to get and set values so that config.jelly can use them

    //getter


    public String getIsFinalAttempt() {
        return isFinalAttempt;
    }


    public String  getTestRailProjectId() {
        return testRailProjectId;
    }

    public String getTestRailSuiteId() {
        return testRailSuiteId;
    }

    public String getTestRailTestRunName() {
        return testRailTestRunName;
    }

    public String getTestRunPropertyFilePath() {
        return testRunPropertyFilePath;
    }

    public String  getRetryCountNo() {
        return retryCountNo;
    }

    public String getTestRailRunDetailsFile() {
        return testRailRunDetailsFile;
    }

    public String getIsReRun() {
        return isReRun;
    }

    public String getResultEmailRecipient() {
        return resultEmailRecipient;
    }

    public String getLinkCasesBy() {
        return linkCasesBy;
    }

    public String getIsSaveToTestRail() {
        return isSaveToTestRail;
    }

    //setter
    public void  setTestRailProjectId(String  testRailProjectId) {
        this.testRailProjectId = testRailProjectId;
    }

    public void setTestRailTestRunName(String testRailTestRunName) {
        this.testRailTestRunName = testRailTestRunName;
    }

    public void setTestRunPropertyFilePath(String testRunPropertyFilePath) {
        this.testRunPropertyFilePath = testRunPropertyFilePath;
    }
    public void setTestRailSuiteId(String  testRailSuiteId) {
        this.testRailSuiteId = testRailSuiteId;
    }

    public void setResultEmailRecipient(String resultEmailRecipient) {
        this.resultEmailRecipient = resultEmailRecipient;
    }

    public void setRetryCountNo(String retryCountNo) {
        this.retryCountNo = retryCountNo;
    }

    public void setLinkCasesBy(String linkCasesBy) {
        this.linkCasesBy = linkCasesBy;
    }

    public void setIsFinalAttempt(String isFinalAttempt) {
        this.isFinalAttempt = isFinalAttempt;
    }


    public void setIsReRun(String isReRun) {
        this.isReRun = isReRun;
    }

    public void setTestRailRunDetailsFile(String testRailRunDetailsFile) {
        this.testRailRunDetailsFile = testRailRunDetailsFile;
    }

    public void setIsSaveToTestRail(String isSaveToTestRail) {
        this.isSaveToTestRail = isSaveToTestRail;
    }

    // This is to perform the required action
    @Override
    public boolean perform(Build <?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException {
        //Variables declaration
        String expandedTestRunPropertyFilePath=" ";
        String expandedTestRailTestRunName=" ";
        String expandedTestRailSuiteId=" ";
        String expandedTestRailRunDetailsFile=" ";
        String expandedRetryCountNo=" ";
        String expandedLinkCasesBy=" ";
        String expandedResultEmailRecipient=" ";
        String expandedTestRailProjectId=" ";
        String runId="";
        String tempFileLocation="";
        FilePath fpRunDetails=null;
        FilePath fpTestRunProperties=null;
        String jsonOrText="";
        FilePath fptempFileLocation=null;
        FilePath workspace=build.getWorkspace();
        String jenkinsHomePath="";
        FilePath fpTestRunPropertiesRead=null;
        String expandedIsSaveToTestRail=" ";

        if(workspace!=null) {
            //This is to convert variables into their corresponding values
            if (testRunPropertyFilePath.contains("$") || testRunPropertyFilePath.contains("%")) {
                expandedTestRunPropertyFilePath = VariableHandling.expandedResult(build, listener, testRunPropertyFilePath);
                if(expandedTestRunPropertyFilePath.equals("Wrong Format"))
                {
                    listener.getLogger().println(" TestRunPropertyFilePath Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedTestRunPropertyFilePath = testRunPropertyFilePath;
            }
            if (testRailSuiteId.contains("$") || testRailSuiteId.contains("%")) {
                expandedTestRailSuiteId = VariableHandling.expandedResult(build, listener, testRailSuiteId);
                if(expandedTestRailSuiteId.equals("Wrong Format"))
                {
                    listener.getLogger().println("TestRailSuiteId Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedTestRailSuiteId = testRailSuiteId;
            }
            if (testRailRunDetailsFile.contains("$") || testRailRunDetailsFile.contains("%")) {
                expandedTestRailRunDetailsFile = VariableHandling.expandedResult(build, listener, testRailRunDetailsFile);
                if(expandedTestRailRunDetailsFile.equals("Wrong Format"))
                {
                    listener.getLogger().println(" TestRailRunDetailsFile Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedTestRailRunDetailsFile = testRailRunDetailsFile;
            }
            if (testRailProjectId.contains("$") || testRailProjectId.contains("%")) {
                expandedTestRailProjectId = VariableHandling.expandedResult(build, listener, testRailProjectId);
                if(expandedTestRailProjectId.equals("Wrong Format"))
                {
                    listener.getLogger().println(" TestRailRunDetailsFile Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedTestRailProjectId = testRailProjectId;
            }
            if (testRailTestRunName.contains("$") || testRailTestRunName.contains("%")) {
                expandedTestRailTestRunName = VariableHandling.expandedResult(build, listener, testRailTestRunName);
                if(expandedTestRailTestRunName.equals("Wrong Format"))
                {
                    listener.getLogger().println("  TestRailTestRunName Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedTestRailTestRunName = testRailTestRunName;
            }
            if (retryCountNo.contains("$") || retryCountNo.contains("%")) {
                expandedRetryCountNo = VariableHandling.expandedResult(build, listener, retryCountNo);
                if(expandedRetryCountNo.equals("Wrong Format"))
                {
                    listener.getLogger().println("RetryCountNo Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedRetryCountNo = retryCountNo;
            }
            if (resultEmailRecipient.contains("$") || resultEmailRecipient.contains("%")) {
                expandedResultEmailRecipient = VariableHandling.expandedResult(build, listener, resultEmailRecipient);
                if(expandedResultEmailRecipient.equals("Wrong Format"))
                {
                    listener.getLogger().println("ResultEmailRecipient Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedResultEmailRecipient = resultEmailRecipient;
            }
            if (linkCasesBy.contains("$") || linkCasesBy.contains("%")) {
                expandedLinkCasesBy = VariableHandling.expandedResult(build, listener, linkCasesBy);
                if(expandedLinkCasesBy.equals("Wrong Format"))
                {
                    listener.getLogger().println("LinkCasesBy Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedLinkCasesBy = linkCasesBy;
            }
            if (isSaveToTestRail.contains("$") || isSaveToTestRail.contains("%")) {
                expandedIsSaveToTestRail = VariableHandling.expandedResult(build, listener, isSaveToTestRail);
                if(expandedIsSaveToTestRail.equals("Wrong Format"))
                {
                    listener.getLogger().println("LinkCasesBy Variable Should be accroding to format %VariableName% or $VariableName$");
                    return false;
                }
            } else {
                expandedIsSaveToTestRail = isSaveToTestRail;
            }


            //To check that isSaveToTestrail contains only Yes or No
           if(expandedIsSaveToTestRail.trim().toLowerCase().equals("true")||expandedIsSaveToTestRail.trim().toLowerCase().equals("false"))
           {
               expandedIsSaveToTestRail=expandedIsSaveToTestRail.trim().toLowerCase();
           }
           else
           {
               listener.getLogger().println("isSaveToTestRail should  only be yes or no");
               return false;
           }
            //To check wheather user need a json file or text file
            if (((expandedTestRunPropertyFilePath.substring(expandedTestRunPropertyFilePath.lastIndexOf('.') + 1)).toLowerCase()).equals("txt")) {
                listener.getLogger().println("Got it! creating a text file");
                jsonOrText = "txt";
            } else if (((expandedTestRunPropertyFilePath.substring(expandedTestRunPropertyFilePath.lastIndexOf('.') + 1)).toLowerCase()).equals("json")) {
                listener.getLogger().println("Got it! creating a json file ");
                jsonOrText = "json";
            } else {
                listener.getLogger().println("Not supported file type, please correct TestRun Property File Path");
                return false;
            }
            listener.getLogger().println("Temporary TestRunSetting Generation Starts at master location:- " + workspace.toString());
            listener.getLogger().println("looking at path and deleting Temporary file if present previously");
            //deleted previosuly Found Temporary TestRun Setting File
            jenkinsHomePath=VariableHandling.expandedResultForDirectVariables(build, listener,"JENKINS_HOME");
            if (jsonOrText == "txt") {
                tempFileLocation = jenkinsHomePath + "/TemporaryTestSetting.txt";
            } else if (jsonOrText == "json") {
                tempFileLocation = jenkinsHomePath + "/TemporaryTestSetting.json";
            }
            boolean isdeleted = FileOperation.deleteFileIfPersent((tempFileLocation));
            if (isdeleted) {
                listener.getLogger().println("Found Temporary Test Run Setting File and deleted successfully");
            } else {
                listener.getLogger().println("No Temporary Test Run Setting File Found to delete");
            }

            //Create new File
            String fileCreationStatus = FileOperation.createNewFile(tempFileLocation);
            if (!fileCreationStatus.equals("Temporary File Created")) {
                listener.getLogger().println("Temporary File Test Run Setting is not created due to exception " + fileCreationStatus);
                return false;
            } else {
                listener.getLogger().println("Temporary File For Test Run Setting is created successfully");
            }
            listener.getLogger().println("Getting Test Run Id from TestRailRunDetailsFile File");

            //Processing for TestRunId from TestRunDetails File
            if (isReRun.equals("Yes")) {
                if (expandedTestRailRunDetailsFile.isEmpty()) {
                    listener.getLogger().println("Please provide path of TestRailRunDetailsFile Path");
                    return false;
                }
                else
                {
                    try {
                        if (workspace.isRemote()) {
                            VirtualChannel channel = workspace.getChannel();
                            fpRunDetails = new FilePath(channel, expandedTestRailRunDetailsFile);
                        } else {
                            fpRunDetails = new FilePath(new File(expandedTestRailRunDetailsFile));
                        }
                        runId = fpRunDetails.readToString();
                        if(runId!=null) {
                            if (expandedTestRailRunDetailsFile.contains("TestRailRunDetails")) {
                                runId = runId.substring(runId.indexOf('=') + 1);
                            } else if (!expandedTestRailRunDetailsFile.contains("RunId")) {
                                listener.getLogger().println("Not supported Run Id File");
                                return false;
                            }
                        }
                        else
                        {
                            listener.getLogger().println("Run Id File is empty");
                            return false;
                        }

                    } catch (IOException e) {
                        listener.getLogger().println(e.getMessage());
                        return false;
                    }
                    listener.getLogger().println("Run Id from TestRunDetails File is " + runId);
                }
                if (expandedRetryCountNo.isEmpty()) {
                    listener.getLogger().println("Please provide retry count");
                    return false;
                }

            }
            listener.getLogger().println("Parameter Writing to temporary file starts");
            //Writing parameters to file
            if (jsonOrText.equals("txt")) {
                //Appending File
                //Text
                if(expandedIsSaveToTestRail.equals("false"))
                {
                    expandedTestRailTestRunName="DO NOT UPLOAD ON TESTRAIL";
                }
                String textFileAppendingStatus = FileOperation.writeToTextFile(tempFileLocation, expandedTestRailTestRunName, expandedTestRailProjectId, expandedTestRailSuiteId, expandedLinkCasesBy, isFinalAttempt, expandedResultEmailRecipient, runId, expandedRetryCountNo);
                if (!textFileAppendingStatus.equals("Parameters are written into temporary Text file")) {
                    listener.getLogger().println("Parameters are not written in temporary text file due to problem:-" + textFileAppendingStatus);
                    return false;
                } else {
                    listener.getLogger().println("Parameters  are written into temporary text file");
                }
            } else {//Appending File
                //Json
                String jsonFileAppendingStatus = FileOperation.writeToJsonFile(tempFileLocation, expandedTestRailTestRunName, expandedTestRailProjectId, expandedTestRailSuiteId, expandedLinkCasesBy, isFinalAttempt, expandedResultEmailRecipient, runId, expandedRetryCountNo,expandedIsSaveToTestRail);
                if (!jsonFileAppendingStatus.equals("Parameters are written into temporary JSON file")) {
                    listener.getLogger().println("Parameters are not written in temporary JSON file due to " + jsonFileAppendingStatus);
                    return false;
                } else {
                    listener.getLogger().println("Parameters  are written into temporary JSON file");

                }
            }
            listener.getLogger().println("Temporary File is ready to be copied to desired place.");
            //Copying File To desired location;
            listener.getLogger().println("Copying File to the desired location.");
            try {
                if (workspace.isRemote()) {
                    VirtualChannel channel = workspace.getChannel();
                    fpTestRunProperties = new FilePath(channel, expandedTestRunPropertyFilePath);
                } else {
                    fpTestRunProperties = new FilePath(new File(expandedTestRunPropertyFilePath));
                }

                //To create a FilePath of tempFileLocation
                fptempFileLocation = new FilePath(new File(tempFileLocation));
                fpTestRunProperties.copyFrom(fptempFileLocation);
            } catch (IOException e) {
                listener.getLogger().println(e.getMessage());
                return false;
            }
            listener.getLogger().println("Test RunSetting File Copied to the destination");
            //To test wheather file exist on that location
            try {
                if (workspace.isRemote()) {
                    VirtualChannel channel = workspace.getChannel();
                    fpTestRunPropertiesRead = new FilePath(channel, expandedTestRunPropertyFilePath);
                } else {
                    fpTestRunPropertiesRead = new FilePath(new File(expandedTestRunPropertyFilePath));
                }

                if(fpTestRunPropertiesRead.read()!=null)
                {
                    listener.getLogger().println("File Found on the location specified");
                }
                else
                {
                    listener.getLogger().println("Check the location to create TestRunProperty File is correct or not");
                }
            } catch (IOException e) {
                listener.getLogger().println(e.getMessage());
                return false;
            }
            listener.getLogger().println("All Done!");
            return true;
        }
        else
        {
            listener.getLogger().println("Workspace not found");
            return false;
        }
    }

    // This is to tell jenkins about different build steps
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return aClass == FreeStyleProject.class;
        }

        @Override
        public String getDisplayName() {
            return "Test Run File Generator";
        }
    }
    }




