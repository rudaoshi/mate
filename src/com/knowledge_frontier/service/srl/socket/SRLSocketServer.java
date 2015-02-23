package com.knowledge_frontier.service.srl.socket;

/**
 * Created by Sun on 15/2/22.
 */

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.util.FileExistenceVerifier;
import se.lth.cs.srl.CompletePipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import java.io.*;
import java.net.*;


public class SRLSocketServer {


    public static void main(String[] args) throws Exception{

        SRLSocketServerOptions options=new SRLSocketServerOptions();
        options.parseCmdLineArgs(args);
        String error= FileExistenceVerifier.verifyCompletePipelineAllNecessaryModelFiles(options);
        if(error!=null){
            System.err.println(error);
            System.err.println();
            System.err.println("Aborting.");
            System.exit(1);
        }

        CompletePipeline pipeline=CompletePipeline.getCompletePipeline(options);

        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(options.port);

        System.out.println("Server is started.");

        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
            parseNonSegmentedLineByLine(pipeline,inFromClient,outToClient);
        }

    }

    private static void parseNonSegmentedLineByLine(CompletePipeline pipeline, BufferedReader in, BufferedWriter writer)	throws IOException, Exception {
        int senCount=0;
        String str;

        while((str=in.readLine()) != null){
            Sentence s=pipeline.parse(str);
            writer.write(s.toString());
            senCount++;

        }

        System.out.println("Processing sentence: "+senCount); //TODO, same as below.

    }

}

