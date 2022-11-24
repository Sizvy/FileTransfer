package SocketProgramming;

import com.sun.security.jgss.GSSUtil;

import javax.management.ObjectName;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Worker extends Thread {
    Socket socket;
    long id;
    FileInfo fileInfo;
    boolean uploading = false;
    public Worker(Socket socket)
    {
        this.socket = socket;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FileInfo getFileInfo(){
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo){
        this.fileInfo = fileInfo;
    }
    public boolean getUploading(){
        return uploading;
    }
    public void setUploading(boolean uploading)
    {
        this.uploading = uploading;
    }
    @Override
    public long getId() {
        return id;
    }
    @Override
    public void run()
    {
        // buffers
        try {
            DataOutputStream dout = new DataOutputStream(this.socket.getOutputStream());
            DataInputStream din = new DataInputStream(this.socket.getInputStream());

            while (true)
            {
                String inFromClient = din.readUTF();
                int i;
                if(inFromClient.startsWith("connect")){
                    String[] splitted = inFromClient.split("\\s+");
                    for(i=0;i<Server.clientInfo.size();i++)
                    {
                        if(Server.clientInfo.get(i).getClientID().equalsIgnoreCase(splitted[1]) && Server.clientInfo.get(i).getStatus().equalsIgnoreCase("Online")){
                            try{
                                dout.writeUTF("You are already logged in");
                                dout.flush();
                                this.socket.close();
                            }catch(Exception e){
                            }
                            break;
                        }
                        else if(Server.clientInfo.get(i).getClientID().equalsIgnoreCase(splitted[1]) && Server.clientInfo.get(i).getStatus().equalsIgnoreCase("Offline")){
                            dout.writeUTF("Welcome Back!");
                            dout.flush();
                            Server.clientInfo.get(i).setStatus("Online");
                        }
                    }
                    if(i>=Server.clientInfo.size()){
                        if(CreateDirectory(splitted[1])){
                            dout.writeUTF("A Directory using your ID is created Successfully");
                            dout.flush();
                            this.setId(Long.parseLong(splitted[1]));
                            ArrayList<String> temp = new ArrayList<>();
                            Clients newClient = new Clients(splitted[1],dout,temp,"Online");
                            Server.clientInfo.add(newClient);
                        }
                    }
                }
                else if(inFromClient.equalsIgnoreCase("a")){
                    String s="";
                    for(int j=0;j<Server.clientInfo.size();j++){
                        s+=Server.clientInfo.get(j).getClientID()+"("+Server.clientInfo.get(j).getStatus()+") ";
                    }
                    dout.writeUTF(s);
                    dout.flush();
                }
                else if(inFromClient.equalsIgnoreCase("b")){
                    String[] pathnames,paths;
                    File fs = new File("E:\\UserFiles\\"+this.getId()+"\\public");
                    File file = new File("E:\\UserFiles\\"+this.getId()+"\\private");
                    pathnames = fs.list();
                    paths = file.list();
                    String fileName = "";
                    fileName+="Public: ";
                    for (String pathname : pathnames) {
                        fileName+=pathname+"#";
                    }
                    fileName+="Private: ";
                    for (String pathname : paths) {
                        fileName+=pathname+"#";
                    }
                    dout.writeUTF(fileName);
                    dout.flush();
                    dout.writeUTF("Would you like to download any of these file? if yes then type y#(filename) otherwise type n");
                    dout.flush();
                    inFromClient = din.readUTF();
                    System.out.println(inFromClient);
                    if(inFromClient.equals("won't download")){

                    }
                    else{
                        try{
                            InputStream inputStream = new FileInputStream(new File(inFromClient));
                            int chunk_size = (int) Server.MAX_CHUNK_SIZE;
                            long fz = new File(inFromClient).length();
                            int left = (int) fz;
                            byte[] bytes = new byte[chunk_size];
                            int No_of_chunk = (int) (fz/chunk_size);
                            dout.writeUTF(String.valueOf(No_of_chunk));
                            dout.writeUTF(String.valueOf(chunk_size));
                            dout.flush();
                            while (inputStream.read(bytes,0,chunk_size)>0){
                                dout.write(bytes,0,chunk_size);
                                dout.flush();
                                left-=chunk_size;
                                if(left<chunk_size){
                                    chunk_size = left;
                                }
                            }
                            inputStream.close();
                            dout.writeUTF("Download Completed");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else if(inFromClient.equalsIgnoreCase("logout")){
                    try{
                        dout.writeUTF("Logged out");
                        dout.flush();
                        this.socket.close();
                    }catch(Exception e){
                    }
                    for(int j=0;j<Server.clientInfo.size();j++){
                        if(Server.clientInfo.get(j).getClientID().equalsIgnoreCase(String.valueOf(this.getId()))){
                            ArrayList<String> temp = new ArrayList<>();
                            temp = Server.clientInfo.get(j).getMsg();
                            Server.clientInfo.remove(j);
                            Server.clientInfo.add(new Clients(String.valueOf(this.getId()),dout,temp,"Offline"));
                            break;
                        }
                    }
                }
                else if(inFromClient.startsWith("c")){
                    String[] splitted = inFromClient.split("\\s+");
                    String[] pathnames;
                    File fs = new File("E:\\UserFiles\\"+splitted[1]+"\\public");
                    pathnames = fs.list();
                    String fileName = "";
                    for (String pathname : pathnames) {
                        fileName+=pathname+"#";
                    }
                    dout.writeUTF(fileName);
                    dout.flush();
                    dout.writeUTF("Would you like to download any of these file? if yes then type y#(filename) otherwise type n");
                    dout.flush();
                    inFromClient = din.readUTF();
                    System.out.println(inFromClient);
                    if(inFromClient.equals("won't download")){

                    }
                    else{
                        try{
                            InputStream inputStream = new FileInputStream(new File(inFromClient));
                            int chunk_size = (int) Server.MAX_CHUNK_SIZE;
                            long fz = new File(inFromClient).length();
                            int left = (int) fz;
                            byte[] bytes = new byte[chunk_size];
                            int No_of_chunk = (int) (fz/chunk_size);
                            dout.writeUTF(String.valueOf(No_of_chunk));
                            dout.writeUTF(String.valueOf(chunk_size));
                            dout.flush();
                            while (inputStream.read(bytes,0,chunk_size)>0){
                                dout.write(bytes,0,chunk_size);
                                dout.flush();
                                left-=chunk_size;
                                if(left<chunk_size){
                                    chunk_size = left;
                                }
                            }
                            inputStream.close();
                            dout.writeUTF("Download Completed");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else if(inFromClient.startsWith("d")){
                    String[] splitted = inFromClient.split("\\#");
                    String broadcast = "ID-"+this.getId()+" has requested for a file. Request ID- "+this.getId()+"_"+splitted[1];
                    String s = "Request Sent";
                    dout.writeUTF(s);
                    dout.flush();
                    for(int j=0;j<Server.clientInfo.size();j++){
                        if(Server.clientInfo.get(j).getClientID().equalsIgnoreCase(String.valueOf(this.getId()))){

                        }
                        else{
                            Server.clientInfo.get(j).getMsg().add(broadcast);
                        }
                    }
                }
                else if (inFromClient.equalsIgnoreCase("e")){
                    String s="";
                    for (int j=0;j<Server.clientInfo.size();j++){
                        if(Server.clientInfo.get(j).getClientID().equalsIgnoreCase(String.valueOf(this.getId()))){
                            for(int k=0;k<Server.clientInfo.get(j).getMsg().size();k++){
                                s+=Server.clientInfo.get(j).getMsg().get(k)+"#";
                                Server.clientInfo.get(j).getMsg().set(k,Server.clientInfo.get(j).getMsg().get(k)+"(Seen)");
                            }
                        }
                    }
                    dout.writeUTF(s);
                    dout.flush();
                }
                else if(inFromClient.startsWith("f")){
                    String savedclientInput = inFromClient;
                    String[] splitted = inFromClient.split("\\#");
                    long fileSize = new File(splitted[1]).length();
                    System.out.println(fileSize);
                    String fileName = splitted[1];
                    String folder = splitted[2];
                    String[] fn = fileName.split("\\\\");
                    if(Server.currently_using_space+fileSize>Server.MAX_BUFFER_SIZE){
                        dout.writeUTF("Transmission Failed(File size out of range");
                        dout.flush();
                    }
                    else{
                        Server.currently_using_space+=fileSize;
                        OutputStream otpStream = null;
                        otpStream = new FileOutputStream(new File("E:\\UserFiles\\"+this.getId()+"\\"+folder+"\\"+fn[fn.length-1]));
                        FileInfo fileInfo = new FileInfo(fn[fn.length-1]+"_"+this.getId(),fileName,"E:\\UserFiles\\"+this.getId()+"\\"+folder+"\\"+fn[fn.length-1],fn[fn.length-1], (FileOutputStream) otpStream);
                        String temp = String.valueOf(Server.CHUNK_SIZE);
                        Server.Files.add(fileInfo);
                        this.setFileInfo(fileInfo);
                        dout.writeUTF(temp+"#"+fileInfo.getFileID());
                        dout.flush();
                        int left = (int) fileSize,len=0,sent=0,count=0,total=0;
                        int No_of_chunk = (int) (fileSize/Server.CHUNK_SIZE);
                        String timeoutmsg;
                        try {
                            byte[] bytes = new byte[Server.CHUNK_SIZE];
                            this.setUploading(true);
                            while (count<=No_of_chunk){
                                timeoutmsg = din.readUTF();
                                if(timeoutmsg.equals("true")){
                                    System.out.println("Time out!");
                                    for(int k=0;k<Server.Files.size();k++){
                                        if(Server.Files.get(k).getFileID().equals(this.getFileInfo().getFileID())){
                                            String path = Server.Files.get(k).getToPath();
                                            Server.Files.get(k).getFileOutputStream().close();
                                            new File(path).delete();
                                            System.out.println("Incomplete file deleted");
                                            break;
                                        }
                                    }
                                    break;
                                }

                                len = din.read(bytes,0,Math.min(left,Server.CHUNK_SIZE));
                                otpStream.write(bytes,0,len);
                                total+=len;
                                left-=len;
                                if(count<No_of_chunk){
                                    dout.writeUTF((sent+len)+" bytes sent . Number of Chunk sent: "+count+" out of "+No_of_chunk);
                                }
                                else{
                                    dout.writeUTF("Last Chunk Received. Number of Chunk Received: "+count+" out of "+No_of_chunk);
                                    inFromClient = din.readUTF();
                                    if(inFromClient.equals("Completed")){
                                        if(total==fileSize){
                                            dout.writeUTF("File uploaded Successfully");
                                            System.out.println("File Uploaded Successfully");
                                            otpStream.close();
                                        }
                                        else{
                                            dout.writeUTF("Error while uploading file");
                                            System.out.println("Error while uploading file");
                                            for(int k=0;k<Server.Files.size();k++){
                                                if(Server.Files.get(k).getFileID().equals(this.getFileInfo().getFileID())){
                                                    String path = Server.Files.get(k).getToPath();
                                                    Server.Files.get(k).getFileOutputStream().close();
                                                    new File(path).delete();
                                                    System.out.println("Incomplete file deleted");
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                count++;
                            }
                            this.setUploading(false);
                            Server.currently_using_space-=fileSize;
                            otpStream.close();
                            if(savedclientInput.endsWith("req")){
                                String[] request = savedclientInput.split("\\#");
                                String reqID = request[3];
                                System.out.println("reqID: "+reqID);
                                String[] requester = reqID.split("\\_");
                                for(int k=0;k<Server.clientInfo.size();k++){
                                    if(Server.clientInfo.get(k).getClientID().equals(requester[0])){
                                        System.out.println(requester[0]);
                                        Server.clientInfo.get(k).getMsg().add("Your requested file("+reqID+") is uploaded by "+this.getId());
                                        break;
                                    }
                                }
                            }
                            else{

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Connection Lost with ID- "+this.getId());
            for(int k=0;k<Server.clientInfo.size();k++){
                if(Server.clientInfo.get(k).getClientID().equals(String.valueOf(this.getId()))){
                    Server.clientInfo.get(k).setStatus("Offline");
                    if(this.getUploading()){
                        this.setUploading(false);
                        for(int m=0;m<Server.Files.size();m++){
                            if(Server.Files.get(m).getFileID().equals(this.getFileInfo().getFileID())){
                                String path = Server.Files.get(m).getToPath();
                                try {
                                    Server.Files.get(m).getFileOutputStream().close();
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                                new File(path).delete();
                                System.out.println("Incomplete file deleted");
                                break;
                            }
                        }
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
    public boolean CreateDirectory(String DirectoryName)
    {
        File f = new File("E:\\UserFiles\\"+DirectoryName);
        File sub_f1 = new File("E:\\UserFiles\\"+DirectoryName+"\\private");
        File sub_f2 = new File("E:\\UserFiles\\"+DirectoryName+"\\public");
        if(!f.exists()){
            if (f.mkdir() && sub_f1.mkdir() && sub_f2.mkdir()) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
    public void NotificationDelete(){

    }
}
