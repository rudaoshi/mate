package com.knowlege_frontier.service.srl.socket;

/**
 * Created by Sun on 15/2/22.
 */

import se.lth.cs.srl.options.FullPipelineOptions;

import com.knowlege_frontier.service.srl.socket.SRLSocketServer;


public class SRLSocketServerOptions extends FullPipelineOptions {

    //TODO add some sort of toString() method to all option classes. And make the system print these out when it is initialized.

    public int port = 8080;

    public SRLSocketServerOptions(){
        super.loadPreprocessorWithTokenizer=false; //We think default is CoNLL09 corpus,
        super.skipPI = false;
    }

    @Override
    public String getSubUsageOptions() {
        return "-port   <int>    the listening port of the server\n";
    }

    @Override
    public int trySubParseArg(String[] args, int ai) {
        if(args[ai].equals("-port")){
            ai++;
            port=Integer.parseInt(args[ai]);
            ai++;
        }
        return ai;
    }

    @Override
    public Class<?> getIntendedEntryClass() {
        return SRLSocketServer.class;
    }

}
