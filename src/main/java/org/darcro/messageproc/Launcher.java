package org.darcro.messageproc;

import org.apache.commons.cli.*;
import org.darcro.messageproc.proc.gmp.data.GMPMessageParser;
import org.darcro.messageproc.proc.gmp.msg.GMPFileProcessor;
import org.darcro.messageproc.proc.gmp.msg.GMPPrintProcessor;
import org.darcro.messageproc.input.FileInput;
import org.darcro.messageproc.input.UDPSocketInput;
import org.darcro.messageproc.util.Shutdownable;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;

public class Launcher {

    private static final ShutdownHandler shutdownHandler = new ShutdownHandler();

    public static void main(String [] args) {
        final Options opts = createOptions();
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cl;

        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHandler));

        try {
            cl = parser.parse(opts, args);

            // First check if the help option has been selected to print help and exit.
            if(cl.hasOption("h")) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "messageproc", opts, true);
                System.out.println("Example usage: java -jar messageproc.jar -gmp -f <filename.dat> -p");
                System.exit(0);
            }

            final GMPMessageParser gmpMessageParser;
            // Initialise Data Processors
            if(cl.hasOption("gmp")) {
                gmpMessageParser = new GMPMessageParser();
                shutdownHandler.register(gmpMessageParser);
                System.out.print("Loaded GMPMessageParser...");

                // Initialise Message Processors for GMP
                // File Output Writer
                if (cl.hasOption("w")) {
                    final GMPFileProcessor fileProcessor = new GMPFileProcessor();
                    gmpMessageParser.register(fileProcessor);
                    System.out.print("Loaded GMPFileProcessor...");
                }

                // Standard Out Printer
                if (cl.hasOption("p")) {
                    final GMPPrintProcessor pp = new GMPPrintProcessor();
                    gmpMessageParser.register(pp);
                    System.out.print("Loaded GMPPrintProcessor...");
                }
            } else {
                gmpMessageParser = null;
            }

            // Initialise Readers
            // File Reader
            if(cl.hasOption("f")) {
                final String inputFile = cl.getOptionValue("f");
                final FileInput fi = new FileInput(inputFile);
                if(gmpMessageParser != null) fi.register(gmpMessageParser);
                shutdownHandler.register(fi);
                new Thread(fi).start();
                System.out.print("Loaded FileInput...");
            }

            // Socket Reader
            if(cl.hasOption("s")) {
                final int listenPort = Integer.parseInt(cl.getOptionValue("s"));
                final UDPSocketInput si = new UDPSocketInput(listenPort);
                if(gmpMessageParser != null) si.register(gmpMessageParser);
                shutdownHandler.register(si);
                new Thread(si).start();
                System.out.print("Loaded SocketInput...");
            }


        } catch (ParseException | FileNotFoundException | SocketException e) {
            System.err.println("Unable to parse arguments.");
            e.printStackTrace();
        }
    }


    private static Options createOptions() {
        final Options opts = new Options();
        opts.addOption("h", "help", false, "Display this information.");

        opts.addOption("s", "socket", true, "Enable the Socket Listener on a specified port.");

        opts.addOption("f", "file", true, "Read data out of a specified file.");

        opts.addOption("p", "print", false, "Enables the console print processor.");

        opts.addOption("w", "write", true, "Write output to a file(s).");

        opts.addOption("gmp", "Run the processors for the Generic Message Protocol");

        return opts;
    }


    private static class ShutdownHandler implements Runnable {
        final ArrayList<Shutdownable> shutdownables;
        private ShutdownHandler(){
            shutdownables = new ArrayList<>();
        }

        private void register(Shutdownable shutdownable) {
            shutdownables.add(shutdownable);
        }

        @Override
        public void run() {
            shutdownables.forEach(Shutdownable::shutdown);
            System.out.println("Called shutdown on all registered items.");
        }
    }

}
