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
import java.nio.charset.Charset;


import org.zeromq.ZMQ;


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

        ZMQ.Context context = ZMQ.context(1);
        //  Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:" + Integer.toString(options.port));

        System.out.println("Server is started.");

        while (!Thread.currentThread().isInterrupted()) {
            // Wait for next request from the client

            String received_message = new String(responder.recv(0), Charset.forName("UTF-8"));
            System.out.println(received_message);

            String[] raw_sentences = received_message.split("\n");

            String result = new String();
            for (String raw_sentence: raw_sentences)
            {
                System.out.println(raw_sentence);
                Sentence s=pipeline.parse(raw_sentence);
                result += s.toString();
                result += "\n\n";

            }

            System.out.println(result);

            responder.send(result.getBytes(Charset.forName("UTF-8")), 0);
        }
        responder.close();
        context.term();


    }


}

