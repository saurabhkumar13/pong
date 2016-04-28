package thefallen.pong;


/**
 * Handles everything regarding networking - sending, receiving, rotating information etc.
 */

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.System.err;
import static java.lang.System.out;

public class ping extends Thread {

    // Basic variable declarations and initialisations

    private int Port;
    private String myIP;
    private SortedSet<String> IPset;
    private SortedSet<String> initset;
    SortedSet<String> players;
    private static pong game;
    private DatagramSocket ds = null;
    private DatagramPacket dp = null;
    static JSONObject serverDetails;
    QuestMode.onGameOverListener listener;
    Misc.state State=Misc.state.INIT;
    int GameMode=1;
    static DatagramSocket theSocket;
    onJoinListener joinListener;

    /*
        name : onJoinListener
        input : see down
        output : void
        function : interface
     */

    interface onJoinListener{
        void onjoin(String name, String element, String ip);
        void onfind(String name, String password, int maxPlayers,String mode,String IP);
    }

    /*
        name : ping
        input : IP - String , port - int
        output : void
        function : Constructor
     */

    public ping(String IP, int port) throws Exception {

        Port = port;
        myIP = IP;
        IPset = new TreeSet<>();
        initset = new TreeSet<>();
        start();
        players = new TreeSet<>();
        theSocket = new DatagramSocket();
    }

    /*
        name : run
        input : void
        output : void
        function : Listens for any requests over the connected network and handles network errors
     */

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
        }
        catch (SocketException se) {

            err.println("chat error (Socket Closed = good): " + se.getMessage());
        }
        catch (IOException se) {

            err.println("chat error: " + se.getMessage());
        }
        catch (Exception je) {

            err.println("JSON error: " + je.getMessage());
        }
    }

    /*
        name : handleComms
        input : sender - String, m - String
        output : void
        function : Decodes every packet that is received appropriately and also sends packets when required.
     */

    private void handleComms(String sender, String m)
    {

        JSONObject message = new JSONObject(m);
        String command = message.getString("command");
        out.println("got msg: \""+m+"\" sender: "+sender+" "+System.currentTimeMillis());

        if(command.equals(Misc.Command.FIND.toString())&&State== Misc.state.WAITmaster) {

            sendMessage((new JSONObject().accumulate("server_details",serverDetails).accumulate("command",Misc.Command.FINDreply)).toString(),sender,Port);
        }
        else if(command.equals(Misc.Command.FINDreply.toString())&&State==Misc.state.WAITslave) {

//            sendMessage(serverDetails.toString(),m,Port);
//            out.println("Found server "+sender+" "+message.getJSONObject("server_details"));
            JSONObject serverDetails = message.getJSONObject("server_details");
            if(joinListener!=null)
                joinListener.onfind(serverDetails.getString("name"),serverDetails.getString("password"),serverDetails.getInt("maxPlayers"),serverDetails.getString("mode"),sender);
        }
        else if(command.equals(Misc.Command.JOIN.toString())&&State== Misc.state.WAITmaster) {

            if(IPset.size()<serverDetails.getInt("maxPlayers")||serverDetails.getInt("maxPlayers")==-1) {

                IPset.add(sender);
                String mode = serverDetails.get("mode")+"";

                if(mode.equals(Misc.Modes.DEATHMATCH.toString()))
                    GameMode = 2;

                players.add(message.accumulate("IP",sender).toString());
                JSONArray slaves = new JSONArray();
                joinListener.onjoin(message.getString("name"),message.getString("element"),sender);
                slaves.put(players);
                slaves = slaves.getJSONArray(0);
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.JOINedslave).accumulate("Slaves",slaves)).toString());
            }
        }
        else if(command.equals(Misc.Command.JOINedslave.toString())&&State== Misc.state.WAITslave) {

//            out.println("Slave added "+sender+" "+message.getString("SlaveIP"));
            JSONArray slaves = message.getJSONArray("Slaves");
            slaves = slaves.getJSONArray(0);

            for(int i=0;i<slaves.length();i++) {
                JSONObject slav = (new JSONObject(slaves.getString(i)));
                IPset.add(slav.getString("IP"));

                if (!players.contains(slaves.getString(i))) {

                    if (joinListener != null)
                        joinListener.onjoin(slav.getString("name"), slav.getString("element"), slav.getString("IP"));
                }

            players.add(slaves.getString(i));
            }
        }
        else if(command.equals(Misc.Command.START.toString())) {
            State = Misc.state.GAMING;
            startGame(this,IPset.size());
            Ball.getVel();
            if(myIP.equals(IPset.first()))
                broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.INITBall).accumulate("vx",Misc.INITballvx).accumulate("vy",Misc.INITballvy)).toString());
        }
        else if(State== Misc.state.GAMING)
        {
            if (command.equals(Misc.Command.INITBall.toString())) {

                Misc.INITballvx = message.getDouble("vx");
                Misc.INITballvy = message.getDouble("vy");

                double vx,vy,normal= - 2 * PI * (IPset.headSet(sender).size() - IPset.headSet(myIP).size()) / IPset.size();
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
            else if (command.equals(Misc.Command.SyncBall.toString())) {

                try {
                    Misc.pop2();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                double vx,vy,normal =  2 * PI * (IPset.headSet(sender).size() - IPset.headSet(myIP).size()) / IPset.size(),x,y;
                err.println("rotating by "+normal*180/PI);
                x = message.getDouble("x");
                y = message.getDouble("y");

                Misc.INITballvx = message.getDouble("vx");
                Misc.INITballvy = message.getDouble("vy");

                normal = -normal;

                vx = (Misc.INITballvx*cos(normal) + Misc.INITballvy*sin(normal));
                vy = (-Misc.INITballvx*sin(normal) + Misc.INITballvy*cos(normal));

                if(game!=null) {

                    Ball ball = game.ball;
                    if (ball!=null) {
                        ball.vx = vx;
                        ball.vy = vy;
                        ball.x = game.center.getX() + (x - game.center.getX())*cos(normal) + (y - game.center.getY())*sin(normal);
                        ball.y = game.center.getY() - (x - game.center.getX())*sin(normal) + (y - game.center.getY())*cos(normal);
                    }
                }
            }
            else if (command.equals(Misc.Command.BallReady.toString())) {

                initset.add(sender);
                if(initset.size()==IPset.size()) {

//                err.println(System.currentTimeMillis());
                    State = Misc.state.GAMING;
                    game.ball.vx = Misc.INITballvx;
                    game.ball.vy = Misc.INITballvy;
                }
            }
            else if(command.equals(Misc.Command.ACTION.toString())) {

                int index = -(IPset.headSet(sender).size() - IPset.headSet(myIP).size());

                if(index<0) index+=IPset.size();

                if(index==0) return;

                String action = message.getString("action");
                game.rackets[index].x = message.getInt("x");

                if(action.equals(Misc.Command.L.toString())) game.rackets[index].pressed(KeyMap.left);
                else if(action.equals(Misc.Command.R.toString())) game.rackets[index].pressed(KeyMap.right);
                else if(action.equals(Misc.Command.LT.toString())) game.rackets[index].pressed(KeyMap.tiltLeft);
                else if(action.equals(Misc.Command.RT.toString())) game.rackets[index].pressed(KeyMap.tiltRight);
                else if(action.equals(Misc.Command.ReleaseKeyT.toString())) game.rackets[index].released(KeyMap.tiltRight);
                else if(action.equals(Misc.Command.ReleaseKeyV.toString())) game.rackets[index].released(KeyMap.right);
            }
            else if(command.equals(Misc.Command.SyncHP.toString())) {

                int index = -(IPset.headSet(sender).size() - IPset.headSet(myIP).size());

                if(index<0) index+=IPset.size();

                if(index==0) return;

                game.rackets[index].hp = message.getInt("HP");
            }
            else if(command.equals(Misc.Command.Died.toString())||command.equals(Misc.Command.Disconnect.toString())) {

                if(IPset.size()>2&&!sender.equals(myIP)) {
                    game.pause();
                    game.f_frame.setVisible(false);
                    IPset.remove(sender);
                    if(myIP.equals(IPset.first()))
                        broadcastToGroup((new JSONObject().accumulate("command",Misc.Command.START)).toString());
                }
                else
                {
                    IPset =new TreeSet<>();
                    game.pause();
                    game.f_frame.setVisible(false);
                    if(listener!=null)
                        listener.onGameOver(!sender.equals(myIP),0);
                }
                initset = new TreeSet<>();
            }
        }
        else if(command.equals(Misc.Command.Disconnect.toString()))
        {
            IPset.remove(sender);
        }

    }

    /*
        name : Stop
        input : void
        output : void
        function : closes the data sockets and disconnects from the game network
     */

    public void Stop() {

        if (ds != null) {

            ds.close();
            ds = null;
        }
    }

    public void Disconnect()
    {
        broadcastToGroup((new JSONObject().accumulate("command", Misc.Command.Disconnect)).toString());
    }
    /*
        name : broadcastToGroup
        input : message - String
        output : void
        function : Broadcasts messages to the group of people currently playing together.
     */

    public void broadcastToGroup(String message)
    {
//        out.println("trying to broadcast: "+message+" "+IPset.size());
        for(String ips : IPset)
        {
            sendMessage(message,ips,Port);
        }
    }

    /*
        name : sendMessage
        input : message - String, recIP - String, Port - int
        output : void
        function :
     */


    public void sendMessage(String message,String recIP,int Port) {

        try {

            out.print("Sending to " + recIP + " socket " + Port + " data: " + message);
            byte[] data = message.getBytes();
            DatagramPacket theOutput = new DatagramPacket(data, data.length, InetAddress.getByName(recIP), Port);
            theSocket.send(theOutput);
            out.println("\tsuccess: msg sent "+System.currentTimeMillis());
        } catch (IOException e) {

//            err.println("failed: for "+recIP+" "+Port+" "+e.getLocalizedMessage());
        }
    }

    public void broadcast(String message,String myIP,int Port){
        String[] prefix = myIP.split("\\.");
        int lim = Integer.valueOf(prefix[1]);
        for(int i=0;i<=lim;i++){
            for(int j=1;j<255;j++) {
            sendMessage(message,prefix[0]+"."+prefix[1]+"."+i+"."+j,Port);
        }
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

    static void startGame(ping master,int size)
    {
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (game != null) game.pause();
                game = new pong(size, 0, true);
                for (String slaves : master.players){
                    JSONObject slav = (new JSONObject(slaves));
                    int index = -(master.IPset.headSet(slav.getString("IP")).size() - master.IPset.headSet(master.myIP).size());
                    if(index<0) index+=master.IPset.size();
                    String element = slav.getString("element");
                    if(element.equals(Misc.Avatar.EARTH.toString()))
                        game.rackets[index].setPowerup(Misc.Avatar.EARTH);
                    else if(element.equals(Misc.Avatar.FIRE.toString()))
                        game.rackets[index].setPowerup(Misc.Avatar.FIRE);
                    else if(element.equals(Misc.Avatar.WIND.toString()))
                        game.rackets[index].setPowerup(Misc.Avatar.WIND);
                    else if(element.equals(Misc.Avatar.WATER.toString()))
                        game.rackets[index].setPowerup(Misc.Avatar.WATER);
                }
                game.addBall(master);
                game.rackets[0].master=master;
                game.master=master;
                for(Racket r : game.rackets) {
                    r.sentient = false;
                    err.println(master.GameMode);
                    if(master.GameMode==2)
                        r.hp = 0;
                }
            }
        });
    }

    public void createserver(String server_name, String password, int maxplayers, Misc.Modes mode,String masterName,String masterElement){
        State = Misc.state.WAITmaster;
        if(mode== Misc.Modes.DEATHMATCH)
            GameMode = 2;
        IPset.add(myIP);
        players.add((new JSONObject().accumulate("name",masterName).accumulate("element",masterElement).accumulate("IP",myIP)).toString());
        serverDetails = new JSONObject()
                .accumulate("name",server_name)
                .accumulate("mode",mode)
                .accumulate("maxPlayers",maxplayers)
                .accumulate("password",password);
    }

    public void findserver(){
        State = Misc.state.WAITslave;
        broadcast((new JSONObject().accumulate("command", Misc.Command.FIND)).toString(),myIP,Port);
    }

    public void joinserver(String name,String element, String ip,String mode){
        State = Misc.state.WAITslave;
        if(mode.equals(Misc.Modes.DEATHMATCH.toString()))
            GameMode = 2;
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
//                        messageSender.createserver("server name","",4, Misc.Modes.DEATHMATCH);
                    }
                    else if(a.equals("find")) {
                        messageSender.findserver();
                    }

                    else if(a.equals("join")) {
                        messageSender.joinserver("name","water",scanner.nextLine(),"NORMAL");
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
