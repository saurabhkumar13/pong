package thefallen.pong;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.util.*;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.System.err;
import static java.lang.System.out;

public class ping extends Thread {
    private int Port;
    private String myIP;
    private SortedSet<String> IPset;
    private SortedSet<String> initset;
    private HashMap<String,Racket> players;
    private static pong game;
    private DatagramSocket ds = null;
    private DatagramPacket dp = null;

    static JSONObject initmsg;
    static JSONObject goodbye;
    static JSONObject ingame;
    static JSONObject serverDetails;
    static JSONObject initmsgreply;

    public static enum Command {
        START, STOP, REPLY, GAMING, UpKey, DownKey, ReleaseKey, RequestBall, GotBall
    }


    Misc.state State=Misc.state.INIT;
    public ping(String IP, int port) throws Exception {
        Port = port;
        myIP = IP;
        IPset = new TreeSet<>();
        initset = new TreeSet<>();
        start();
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
            err.println("chat error (Socket Closed = good): " + se.getMessage());
        } catch (IOException se) {
            err.println("chat error: " + se.getMessage());
        }
    }

    void handleComms(String sender, String m)
    {

        JSONObject message = new JSONObject(m);
        String command = message.getString("command");
        out.println("got msg: \""+m+"\" sender: "+sender+" "+System.currentTimeMillis());
        if(command.equals(Command.START))
        {
            if(!sender.equals(myIP))
            {
                if (!IPset.contains(sender))
                    addGamer(sender);
            }
        }
        else if(command.equals(Misc.Command.FIND.toString())&&State== Misc.state.WAITmaster)
        {
            sendMessage((new JSONObject().accumulate("server_details",serverDetails).accumulate("command",Misc.Command.FINDreply)).toString(),sender,Port);
        }
        else if(command.equals(Misc.Command.FINDreply.toString())&&State==Misc.state.WAITslave)
        {
//            sendMessage(serverDetails.toString(),m,Port);
            out.println("Found server "+sender+" "+message.getJSONObject("server_details"));
        }
        else if(command.equals(Misc.Command.JOIN.toString())&&State== Misc.state.WAITmaster)
        {
            if(IPset.size()<serverDetails.getInt("maxPlayers")) {
                IPset.add(sender);
                sendMessage((new JSONObject().accumulate("command",Misc.Command.JOINack)).toString(),sender,Port);
                JSONArray slaves = new JSONArray();
                slaves.put(IPset);
                slaves = slaves.getJSONArray(0);
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.JOINedslave).accumulate("Slaves",slaves)).toString());
            }
        }
        else if(command.equals(Misc.Command.JOINedslave.toString())&&State== Misc.state.WAITslave)
        {
//            out.println("Slave added "+sender+" "+message.getString("SlaveIP"));
            JSONArray slaves = message.getJSONArray("Slaves");
            slaves = slaves.getJSONArray(0);
            for(int i=0;i<slaves.length();i++)
            {
                IPset.add(slaves.getString(i));
            }
        }
        else if(command.equals(Misc.Command.START.toString()))
        {
            startGame(this,IPset.size());
            if(myIP.equals(IPset.first()))
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.INITBall).accumulate("vx",1.5).accumulate("vy",2.1)).toString());
        }
        else if(command.equals(Misc.Command.JOINack.toString()))
        {
            IPset.add(sender);
            IPset.add(myIP);
        }
        else if (command.equals(Misc.Command.INITBall.toString()))
        {
            Misc.INITballvx = message.getDouble("vx");
            Misc.INITballvy = message.getDouble("vy");
            double vx,vy,normal=2 * PI * (IPset.headSet(sender).size() - IPset.headSet(myIP).size()) / IPset.size();
            vx = (Misc.INITballvx*cos(normal) + Misc.INITballvy*sin(normal));
            vy = (-Misc.INITballvx*sin(normal) + Misc.INITballvy*cos(normal));
            Misc.INITballvx = vx;
            Misc.INITballvy = vy;
//
            if(game!=null) {
                Ball ball = game.f_balls.get(0);
                if (ball!=null) {
                    ball.vx = Misc.INITballvx;
                    ball.vy = Misc.INITballvy;
                }
            }
        }
        else if (command.equals(Misc.Command.BallReady.toString()))
        {
            initset.add(sender);
            if(initset.size()==IPset.size()) {
                err.println(System.currentTimeMillis());
                game.f_balls.get(0).vx = Misc.INITballvx;
                game.f_balls.get(0).vy = Misc.INITballvy;
            }
        }
        else if(command.equals(Misc.Command.ACTION.toString()))
        {
            int index = (IPset.headSet(sender).size() - IPset.headSet(myIP).size());
            if(index<0) index+=IPset.headSet(myIP).size();
            String action = message.getString("action");
            if(action.equals(Misc.Command.L.toString())) game.rackets[index].pressed(KeyMap.left);
            else if(action.equals(Misc.Command.R.toString())) game.rackets[index].pressed(KeyMap.right);
            else if(action.equals(Misc.Command.LT.toString())) game.rackets[index].pressed(KeyMap.tiltLeft);
            else if(action.equals(Misc.Command.RT.toString())) game.rackets[index].pressed(KeyMap.tiltRight);
            else if(action.equals(Misc.Command.ReleaseKey.toString())) game.rackets[index].released(KeyMap.tiltRight);
        }
        else if (command.equals(Command.STOP))
            IPset.remove(sender);
        else if (command.equals(Command.GAMING))
            {
                Racket r = players.get(sender);
                int key = message.getInt("key");
                if(key==Command.UpKey.ordinal())
                    r.pressed(KeyEvent.VK_LEFT);
                else if(key==Command.DownKey.ordinal())
                    r.pressed(KeyEvent.VK_RIGHT);
                if(key==Command.ReleaseKey.ordinal())
                    r.released(0);

            }
        else if (command.equals(Command.RequestBall))
        {
            Ball ball = game.f_balls.get(0);
            broadcastToGroup(new JSONObject().put("command",Command.GotBall).put("sync",new JSONObject().accumulate("ax",ball.ax).accumulate("ay",ball.ay).accumulate("vx",ball.vx).accumulate("vy",ball.vy).accumulate("x",ball.x).accumulate("y",ball.y)).toString());
        }
        else if (command.equals(Command.GotBall))
        {
            game.initBALLproperties = message.getJSONObject("sync");
        }

    }

    void addGamer(String sender)
    {
        IPset.add(sender);
//        if(game.r2==null)
//        {
//            game.initRacket2();
//            players.put(sender, game.r2);
//        }

//        if(Sync!=null)
//        {
//            Ball b = game.f_balls.get(0);
//            b.x = Sync.getInt("x");
//            b.y = Sync.getInt("y");
//            b.ax = Sync.getInt("ax");
//            b.ay = Sync.getInt("ay");
//            b.vx = Sync.getInt("vx");
//            b.vy = Sync.getInt("vy");
//        }
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
        out.println("trying to broadcast: "+message+" "+IPset.size());
        for(String ips : IPset)
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
            out.println("\tsuccess: msg sent "+System.currentTimeMillis());
            return true;
        } catch (IOException e) {
            err.println("failed: for "+recIP+" "+Port+" "+e.getLocalizedMessage());
            return false;
        }
    }

    public static boolean sendMessage_mew(String message,String recIP,int Port) {
        try {
            byte[] data = message.getBytes();
            DatagramSocket theSocket = new DatagramSocket();
            DatagramPacket theOutput = new DatagramPacket(data, data.length, InetAddress.getByName(recIP), Port);
            theSocket.send(theOutput);
            // Screen.writeText(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static void broadcast(String message,String myIP,int Port){
        String[] prefix = myIP.split("\\.");
        int lim = Integer.valueOf(prefix[1]);
//        for(int i=0;i<=lim;i++){
            for(int j=1;j<10;j++) {
            sendMessage(message,prefix[0]+"."+prefix[1]+"."+prefix[2]+"."+j,Port);
        }
//    }
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

    static void startGame(ping master,int size)
    {
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                game = new pong(size);
                game.master=master;
                game.f_renderer.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.addBall();
                    }
                });
//                broadcast(initmsg.toString(), master.myIP, master.Port);
            }
        });
    }

    public static void main(String[] args) {
        try{
            int Port = 6969;
            String ip = getmyIP();
            if(ip.equals("")) {
                out.println("Could not get host .. Are You connected to a network?");
            }
            else {
                ping messageSender = new ping(ip, Port);
//                startGame(messageSender);
                out.println("myIP: "+ip);
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String a = scanner.nextLine();
                    if(a.equals("stop")) {messageSender.Stop();break;}
                    else if(a.equals("start")) {
                        messageSender.State = Misc.state.WAITmaster;
                        messageSender.IPset.add(ip);
                        messageSender.serverDetails = new JSONObject()
                                .accumulate("name","Saurabh's server")
                                .accumulate("mode",Misc.Modes.DEATHMATCH)
                                .accumulate("maxPlayers",3)
                                .accumulate("password","lollipop");
                    }
                    else if(a.equals("find")) {
//                        messageSender.IPset.add(ip);
                        messageSender.State = Misc.state.WAITslave;
                        broadcast(Misc.findServer.toString(),ip,Port);
                    }

                    else if(a.equals("findp")) {
//                        messageSender.IPset.add(ip);
                        messageSender.State = Misc.state.WAITslave;
                        sendMessage(Misc.findServer.toString(),scanner.nextLine(),Port);
                    }

//                        sendMessage(Misc.findServer.toString(),ip,Port);
                    else if(a.equals("join")) {
                        sendMessage("mew",scanner.nextLine(),Port);
                        out.println("sending "+(new JSONObject().accumulate("command",Misc.Command.JOIN)).toString()+" " + a.split(" ")[1]);
                    }
                    else if(a.equals("joined"))
                        out.println(messageSender.IPset.size());
                    else if(a.equals("state"))
                        out.println(messageSender.State);
                    else if(a.equals("startGame"))
                    {
                        messageSender.broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.START)).toString());
                    }
                }
            }
        }
        catch(Exception e){
            out.println(e.toString());
        }
    }
}
