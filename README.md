# JenkinPluginForaConfigFileCreation
 This is for demo of how jenkins plugins are created. This is creating a config file in txt or json format acording to some given set of parameter values.
Lets divide jenkins plugin creation in two parts :-
1. Adding Field using Jelly Scripts:- 
  Update config.jelly file as per your need of input type i.e. textbox, select options etc
2. Overriding Perform method of builder class :- 
  Builder class is default class provided by plugin maven proect provided by org.jenkins-ci.plugins.Just costomize acording to   need will turn into a working piece of jenkins plugin.
 
