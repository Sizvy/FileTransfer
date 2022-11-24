package SocketProgramming;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.spec.RSAOtherPrimeInfo;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 6666);
        System.out.println("Connection established");
        System.out.println("Remote port: " + socket.getPort());
        System.out.println("Local port: " + socket.getLocalPort());
        // buffers
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        DataInputStream din = new DataInputStream(socket.getInputStream());
        System.out.println("Please choose an option from below: \n" +
                " Please write down connect and then your ID log into the Server first\n"+
                "a. Look up the list of all the students who were connected at least once to the server\n"+
                "b. Look up the list of all the files (both private and public) uploaded by you\n"+
                "c. Look up the public files of a specific student and download any of these files\n"+
                "d. Request for a file.\n"+
                "e. View all the unread messages\n"+
                "f. Upload a file\n");
        //
        while(true) {
            String clientInput = (String) input.readLine();
            dout.writeUTF(clientInput);
            dout.flush();
            String msg = "";
            try{
                if(clientInput.startsWith("connect")){
                    msg = din.readUTF();
                    if(msg.equalsIgnoreCase("You are already logged in")){
                        System.exit(0);
                    }
                    else if(msg.endsWith("Successfully")){
                        System.out.println(msg);
                    }
                    else{
                        System.out.println(msg);
                    }
                }
                else if(clientInput.equals("a")){
                    msg = din.readUTF();
                    String[] users = msg.split("\\s+");
                    for(int i=0;i<users.length;i++){
                        System.out.println(users[i]);
                    }
                }
                else if(clientInput.equals("b")){
                    msg = din.readUTF();
                    String[] files = msg.split("\\#");
                    System.out.println("Uploaded Files: ");
                    for(int i=0;i<files.length;i++){
                        System.out.println(files[i]);
                    }
                    msg = din.readUTF();
                    System.out.println(msg);
                    clientInput = input.readLine();
                    if(clientInput.startsWith("y")){
                        String[] sp = clientInput.split("\\#");
                        dout.writeUTF(sp[1]+"\\"+sp[2]);
                        dout.flush();
                        int len=0,count=0;
                        long fileSize = new File(sp[1]+"\\"+sp[2]).length();
                        int left = (int) fileSize;
                        OutputStream outputStream = new FileOutputStream(new File("E:\\DownloadedFiles\\"+sp[2]));
                        try {
                            int No_of_chunk = Integer.parseInt(din.readUTF());
                            int chunk_size = Integer.parseInt(din.readUTF());
                            byte[] bytes = new byte[chunk_size];
                            while (count<=No_of_chunk){
                                len = din.read(bytes,0,Math.min(left,chunk_size));
                                outputStream.write(bytes,0,len);
                                left-=len;
                                count++;
                            }
                            outputStream.close();
                            System.out.println(din.readUTF());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        dout.writeUTF("won't download");
                        dout.flush();
                    }
                }
                else if(clientInput.equals("logout")){
                    msg = din.readUTF();
                    System.out.println(msg);
                    System.exit(0);
                }
                else if(clientInput.startsWith("c")){
                    msg = din.readUTF();
                    String[] files = msg.split("\\#");
                    String[] ID = clientInput.split("\\s+");
                    System.out.println("Uploaded public files of ID-"+ID[1]+":");
                    for(int i=0;i<files.length;i++){
                        System.out.println(files[i]);
                    }
                    msg = din.readUTF();
                    System.out.println(msg);
                    clientInput = input.readLine();
                    if(clientInput.startsWith("y")){
                        String[] sp = clientInput.split("\\#");
                        dout.writeUTF(sp[1]+"\\"+sp[2]);
                        dout.flush();
                        int len=0,count=0;
                        long fileSize = new File(sp[1]+"\\"+sp[2]).length();
                        int left = (int) fileSize;
                        OutputStream outputStream = new FileOutputStream(new File("E:\\DownloadedFiles\\"+sp[2]));
                        try {
                            int No_of_chunk = Integer.parseInt(din.readUTF());
                            int chunk_size = Integer.parseInt(din.readUTF());
                            byte[] bytes = new byte[chunk_size];
                            byte[][] doubleByte = new byte[No_of_chunk][chunk_size];
                            while (count<=No_of_chunk){
                                len = din.read(bytes,0,Math.min(left,chunk_size));
                                outputStream.write(bytes,0,len);
                                left-=len;
                                count++;
                            }
                            outputStream.close();
                            System.out.println(din.readUTF());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        dout.writeUTF("won't download");
                        dout.flush();
                    }
                }
                else if(clientInput.startsWith("d")){
                    msg = din.readUTF();
                    System.out.println(msg);
                }
                else if(clientInput.equals("e")){
                    msg = din.readUTF();
                    String[] splitted = msg.split("\\#");
                    if(splitted.length==0){
                        System.out.println("Inbox is empty");
                    }
                    else{
                        System.out.println("Inbox: ");
                        for(int j=0;j<splitted.length;j++){
                            System.out.println(splitted[j]);
                        }
                    }
                }
                else if(clientInput.startsWith("f")){
                    String[] strings = clientInput.split("\\#");
                    msg = din.readUTF();
                    String[] strings1 = msg.split("\\#");
                    if(msg.startsWith("Transmission")){
                        System.out.println(msg);
                    }
                    else{
                        int size = Integer.parseInt(strings1[0]);
                        System.out.println("CHUNK SIZE and File ID: "+msg);
                        long fileSize = new File(strings[1]).length();
                        System.out.println(fileSize);
                        InputStream iptStream = null;
                        int left= (int) fileSize;
                        try {
                            iptStream = new FileInputStream(new File(strings[1]));
                            socket.setSoTimeout(5000);
                            byte[] bytes = new byte[size];
                            while (iptStream.read(bytes,0,size)>0){
                                dout.writeUTF("false");
                                dout.write(bytes,0,size);
                                dout.flush();
                                left -= size;
                                if(left<size){
                                    size = left;
                                }
                                msg = din.readUTF();
                                if(msg.contains("sent")){
                                    System.out.println(msg);
                                }
                                else if(msg.startsWith("Last")){
                                    System.out.println(msg);
                                    dout.writeUTF("Completed");
                                    msg = din.readUTF();
                                    if(msg.endsWith("Successfully")){
                                        System.out.println(msg);
                                        iptStream.close();
                                    }
                                    else{
                                        System.out.println(msg);
                                    }
                                }
                            }
                            iptStream.close();
                        }catch (SocketTimeoutException e ){
                            System.out.println("Time out!");
                            dout.writeUTF("true");
                            dout.flush();
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
