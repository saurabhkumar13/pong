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
import static java.lang.System.in;
import static java.lang.System.out;

public class ping extends Thread {
    private int Port;
    private String myIP;
    private SortedSet<String> IPset;
    private SortedSet<String> initset;
    SortedSet<String> players;
    private static pong game;
    private DatagramSocket ds = null;
    private DatagramPacket dp = null;

    static JSONObject initmsg;
    static JSONObject goodbye;
    static JSONObject ingame;
    static JSONObject serverDetails;
    static JSONObject initmsgreply;

    Misc.state State=Misc.state.INIT;
    public ping(String IP, int port) throws Exception {
        Port = port;
        myIP = IP;
        IPset = new TreeSet<>();
        initset = new TreeSet<>();
        start();
        players = new TreeSet<>();
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
        if(command.equals(Misc.Command.FIND.toString())&&State== Misc.state.WAITmaster)
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
                players.add(message.accumulate("IP",sender).toString());
                JSONArray slaves = new JSONArray();
                slaves.put(players);
                slaves = slaves.getJSONArray(0);
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.JOINedslave).accumulate("Slaves",slaves)).toString());
            }
        }
        else if(command.equals(Misc.Command.JOINedslave.toString())&&State== Misc.state.WAITslave)
        {
//            out.println("Slave added "+sender+" "+message.getString("SlaveIP"));
            JSONArray slaves = message.getJSONArray("Slaves");
            slaves = slaves.getJSONArray(0);
            for(int i=0;i<slaves.length();i++) {
                JSONObject slav = (new JSONObject(slaves.getString(i)));
                IPset.add(slav.getString("IP"));
                if (!players.contains(slaves.getString(i)))
                {
                    err.println(slav.getString("name")+"||"+  slav.getString("element")+"||"+ slav.getString("IP"));
                    if (joinListener != null)
                        joinListener.onjoin(slav.getString("name"), slav.getString("element"), slav.getString("IP"));
                }
            players.add(slaves.getString(i));
            }
        }
        else if(command.equals(Misc.Command.START.toString()))
        {
            startGame(this,IPset.size());
            if(myIP.equals(IPset.first()))
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.INITBall).accumulate("vx",1.5).accumulate("vy",2.1)).toString());
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
                Ball ball = game.ball;
                if (ball!=null) {
                    ball.vx = Misc.INITballvx;
                    ball.vy = Misc.INITballvy;
                }
            }
        }
        else if (command.equals(Misc.Command.SyncBall.toString()))
        {
            double vn,vt;
            vn = message.getDouble("vx");
            vt = message.getDouble("vy");
            double vx,vy,normal=2 * PI * (IPset.headSet(sender).size() - IPset.headSet(myIP).size()) / IPset.size(),x,y;
            x = message.getDouble("x");
            y = message.getDouble("y");
            vx = (vn*cos(normal) + vt*sin(normal));
            vy = (-vn*sin(normal) + vt*cos(normal));

            if(game!=null) {
                Ball ball = game.ball;
                if (ball!=null) {
                    ball.vx = vx;
                    ball.vy = vy;
                    ball.x = game.center.getX() + (x - game.center.getX())*cos(normal) - (y - game.center.getY())*sin(normal);
                    ball.y = game.center.getY() + (x - game.center.getX())*sin(normal) + (y - game.center.getY())*cos(normal);
                }
            }
        }
        else if (command.equals(Misc.Command.BallReady.toString()))
        {
            initset.add(sender);
            if(initset.size()==IPset.size()) {
                err.println(System.currentTimeMillis());
                game.ball.vx = Misc.INITballvx;
                game.ball.vy = Misc.INITballvy;
            }
        }
        else if(command.equals(Misc.Command.ACTION.toString()))
        {
            int index = (IPset.headSet(sender).size() - IPset.headSet(myIP).size());
            if(index<0) index+=IPset.size();
            String action = message.getString("action");
            game.rackets[index].x = message.getInt("x");
            if(action.equals(Misc.Command.L.toString())) game.rackets[index].pressed(KeyMap.left);
            else if(action.equals(Misc.Command.R.toString())) game.rackets[index].pressed(KeyMap.right);
            else if(action.equals(Misc.Command.LT.toString())) game.rackets[index].pressed(KeyMap.tiltLeft);
            else if(action.equals(Misc.Command.RT.toString())) game.rackets[index].pressed(KeyMap.tiltRight);
            else if(action.equals(Misc.Command.ReleaseKeyT.toString())) game.rackets[index].released(KeyMap.tiltRight);
            else if(action.equals(Misc.Command.ReleaseKeyV.toString())) game.rackets[index].released(KeyMap.right);
        }
        else if(command.equals(Misc.Command.SyncHP.toString()))
        {
            int index = (IPset.headSet(sender).size() - IPset.headSet(myIP).size());
            if(index<0) index+=IPset.size();
            game.rackets[index].hp = message.getInt("HP");
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
                game = new pong(size,2);
                game.rackets[0].master=master;
                game.master=master;
                game.ball.master=master;
                for(Racket r : game.rackets) {
                    r.sentient = false;
                }
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

    public void createserver(String server_name, String password, int maxplayers, Misc.Modes mode){
        State = Misc.state.WAITmaster;
        IPset.add(myIP);
        serverDetails = new JSONObject()
                .accumulate("name",server_name)
                .accumulate("mode",mode)
                .accumulate("maxPlayers",maxplayers)
                .accumulate("password",password);
    }
    onJoinListener joinListener;
    interface onJoinListener{
        void onjoin(String name, String element, String ip);
    }

    public void findserver(){
        State = Misc.state.WAITslave;
        broadcast((new JSONObject().accumulate("command", Misc.Command.FIND)).toString(),myIP,Port);
    }

    public void joinserver(String name,String element, String ip){
        State = Misc.state.WAITslave;
        sendMessage((new JSONObject().accumulate("command", Misc.Command.JOIN).accumulate("name",name).accumulate("element",element)).toString(),ip,Port);
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
                        messageSender.createserver("server name","",4, Misc.Modes.DEATHMATCH);
                    }
                    else if(a.equals("find")) {
                        messageSender.findserver();
                    }

                    else if(a.equals("join")) {
                        messageSender.joinserver("name","water",scanner.nextLine());
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
