package thefallen.pong;


import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.System.out;

public class ping extends Thread {
    private int Port;
    private String myIP;
    private ArrayList<String> IPlist;
    private HashMap<String,Racket> players;
    private static pong game;
    private DatagramSocket ds = null;
    private DatagramPacket dp = null;

    static JSONObject initmsg;
    static JSONObject goodbye;
    static JSONObject ingame;
    static JSONObject initmsgreply;

    public static enum Command{
        START,STOP,REPLY,GAMING,UpKey,DownKey,ReleaseKey
    }



    public ping(String IP, int port) throws Exception {
        Port = port;
        myIP = IP;
        IPlist = new ArrayList<>();
        start();
        initmsg = new JSONObject();
        initmsgreply = new JSONObject();
        goodbye = new JSONObject();
        ingame = new JSONObject();
        initmsg.accumulate("command",Command.START.ordinal());
        initmsgreply.accumulate("command", Command.REPLY.ordinal());
        goodbye.accumulate("command",Command.STOP.ordinal());
        ingame.accumulate("command",Command.GAMING.ordinal());
        players = new HashMap<>();
    }

    public void run() {
        try {
            // open DatagramSocket to receive
            ds = new DatagramSocket(Port);
            // loop forever reading datagrams from the DatagramSocket
            while (true) {
                byte[] buffer = new byte[65000]; // max char length
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                String s = new String(dp.getData(), 0, dp.getLength());
                // Screen.writeText(s);
                String sender = dp.getAddress().getHostAddress();
                handleComms(sender,s);
            }
        } catch (SocketException se) {
            System.err.println("chat error (Socket Closed = good): " + se.getMessage());
        } catch (IOException se) {
            System.err.println("chat error: " + se.getMessage());
        }
    }

    void handleComms(String sender, String m)
    {
        out.println("got msg: "+m);
        JSONObject message = new JSONObject(m);
        int command = message.getInt("command");
        if(command==Command.START.ordinal())
        {
            if(!sender.equals(myIP))
            {
                if (!IPlist.contains(sender))
                    addGamer(sender);
                sendMessage(initmsg.toString(),sender,Port);
            }
        }
        else if (command==Command.REPLY.ordinal())
            addGamer(sender);
        else if (command==Command.STOP.ordinal())
            IPlist.remove(sender);
        else if (command==Command.GAMING.ordinal())
            {
                Racket r = players.get(sender);
                int key = message.getInt("key");
                if(key==Command.UpKey.ordinal())
                    r.pressed(KeyEvent.VK_UP);
                else if(key==Command.DownKey.ordinal())
                    r.pressed(KeyEvent.VK_DOWN);
                if(key==Command.ReleaseKey.ordinal())
                    r.released(0);

            }

    }

    void addGamer(String sender)
    {
        IPlist.add(sender);
        if(game.r2==null)
        {
            game.initRacket2();
//            game.r2 = new Racket(null,false);
            players.put(sender, game.r2);
        }
    }

    public void Stop() {
        if (ds != null) {
            ds.close();
            broadcastToGroup(goodbye.toString());
            ds = null;
        }
    }

    public void broadcastToGroup(String message)
    {
        out.println("msg to send: "+message);
        for(String ips : IPlist)
        {
            sendMessage(message,ips,Port);
        }
    }

    public static boolean sendMessage(String message,String recIP,int Port) {
        try {
            out.print("Sending to " + recIP + " socket " + Port + " data: " + message);
            byte[] data = message.getBytes();
            DatagramSocket theSocket = new DatagramSocket();
            DatagramPacket theOutput = new DatagramPacket(data, data.length, InetAddress.getByName(recIP), Port);
            theSocket.send(theOutput);
            // Screen.writeText(message);
            out.println("\tsuccess: msg sent");
            return true;
        } catch (IOException e) {
            System.err.println("failed: "+e.getLocalizedMessage());
            return false;
        }
    }
    public static void broadcast(String message,String myIP,int Port){
        String prefix = myIP.substring(0,myIP.lastIndexOf('.')+1);
        for(int i=0;i<256;i++) {
            sendMessage(message,prefix+i,Port);
        }
    }

    public static String getmyIP()
    {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().split("[.]").length==4&&!i.getHostAddress().split("[.]")[0].equals("127"))
                        return i.getHostAddress();
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
            return "";
        }
        return "";
    }

    static void startGame(ping master)
    {
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                game = new pong(master);
            }
        });
    }

    public static void main(String[] args) {
        try{
            int Port = 71;
            String ip = getmyIP();
            if(ip.equals("")) {
                out.println("Could not get host .. Are You connected to a network?");
            }
            else {
                ping messageSender = new ping(ip, Port);
                startGame(messageSender);
                broadcast(initmsg.toString(),ip,Port);
                out.println("myIP: "+ip);
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String a = scanner.nextLine();
                    if(a.equals("stop")) {messageSender.Stop();break;}
                    messageSender.broadcastToGroup(a);
                }
            }
        }
        catch(Exception e){
            out.println(e.toString());
        }
    }
}
