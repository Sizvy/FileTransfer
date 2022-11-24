package SocketProgramming;

import java.io.DataOutputStream;
import java.util.ArrayList;

public class Clients {
    String clientID;
    DataOutputStream out;
    ArrayList<String> msg=new ArrayList<>(100);
    String status;

    public Clients(String clientID, DataOutputStream out, ArrayList<String> msg,String status) {
        this.clientID = clientID;
        this.out = out;
        this.msg = msg;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientID() {
        return clientID;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public ArrayList<String> getMsg() {
        return msg;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public void setMsg(ArrayList<String> msg) {
        this.msg = msg;
    }
}
