package com.monotype.TestRailIntegration;

import hudson.EnvVars;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mittaln on 31-05-2017.
 */
public class VariableHandling {
    //for variable passed using % or $
    public static String expandedResult(Build build, BuildListener listener, String entity) throws InterruptedException {
        EnvVars env = null;
        try {
            //Finding no of % in string
            env = build.getEnvironment(listener);
            int counterForPerc=0;
            int counterForDollar=0;
            String beforeVariable="";
            String afterVariable;
            String variableValue;
            String variableName;
            String finalValueOfInput = entity;
            //logic to overwrite all the variables with their values
            // To find count

            if (entity.contains("%")) {
                for (int index=entity.indexOf("%");index>=0;index=entity.indexOf(("%"),index+1)) {
                    counterForPerc++;
                }
            }
            else if (entity.contains("$")) {
                    //logic to overwrite all the variables with their values
                    for (int index = entity.indexOf("$"); index >= 0; index = entity.indexOf(("$"), index + 1)) {
                        counterForDollar++;
                    }
            }
            if ((counterForDollar%2)>0||(counterForPerc%2)>0) {
                    return "Wrong Format";
            }
            for(int index=entity.indexOf("%");counterForPerc>1;counterForPerc=counterForPerc/2,index=entity.indexOf("%"))
            {
                beforeVariable=entity.substring(0,index);
                variableName= entity.substring(index+ 1,entity.indexOf(("%"), index + 1));
                variableValue=env.get(variableName);
                afterVariable=entity.substring(entity.indexOf(("%"), index + 1)+1);
                entity=beforeVariable+variableValue+afterVariable;
            }
            for(int index=entity.indexOf("$");counterForDollar>1;counterForDollar=counterForDollar/2,index=entity.indexOf("$"))
            {
                beforeVariable=entity.substring(0,index);
                variableName= entity.substring(index+ 1,entity.indexOf(("$"), index + 1));
                variableValue=env.get(variableName);
                afterVariable=entity.substring(entity.indexOf(("$"), index + 1)+1);
                entity=beforeVariable+variableValue+afterVariable;
            }
            listener.getLogger().println("converted Value corresponding to Variable is " + entity);
            return entity;

        } catch (IOException e) {
            listener.getLogger().println("Exception encounters during reading environment variables Please check Inputs for more details" + e.getMessage());
            return "Wrong Format";
        }
    }
    //For direct passed jenkins variable like Jenkins_Home etc
    public static String expandedResultForDirectVariables(Build build, BuildListener listener, String entity) throws InterruptedException {
        EnvVars env = null;
        try {
            //Finding no of % in string
            env = build.getEnvironment(listener);
            entity=env.get(entity);
            listener.getLogger().println("converted Value corresponding to Variable is " + entity);
            return entity;
        } catch (IOException e) {
            listener.getLogger().println("Exception encounters during reading environment " + entity + " variables " + e.getMessage());
            return "Wrong Format";
        }
    }


}
