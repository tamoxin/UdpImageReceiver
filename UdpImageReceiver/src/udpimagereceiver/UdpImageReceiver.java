package udpimagereceiver;


import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.Enumeration;
import javax.imageio.ImageIO;

public class UdpImageReceiver {
    
    public static void main(String args[]) {
        Enumeration enumer;
        try {
            enumer = NetworkInterface.getNetworkInterfaces();
            while(enumer.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) enumer.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    System.out.println(i.getHostAddress());
                }
            }
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DatagramSocket sock = null;

        try {
            //1. creating a server socket, parameter is local port number
            sock = new DatagramSocket(7777);

            //buffer to receive incoming data
            byte[] buffer = new byte[1024];
            byte[] imageBuffer;
            byte[] data;
            int byteLength = 65536;
            String byteName = "image";
            
            DatagramPacket incomingInfo = new DatagramPacket(buffer, buffer.length);

            //2. Wait for an incoming data
            System.out.println("Server socket created. Waiting for incoming data...");

            //communication loop
            int i = 0;
            boolean done = false;
            int c = 0;
            while(!done) {
                
                switch(i){
                    case 0:
                        sock.receive(incomingInfo);
                        data = incomingInfo.getData();
                        byteLength = Integer.parseInt(new String(data, 0, incomingInfo.getLength()));
                        echo(incomingInfo, "" + byteLength);
                        i++;
                        break;
                    case 1:
                        sock.receive(incomingInfo);
                        data = incomingInfo.getData();
                        byteName = new String(data, 0, incomingInfo.getLength());
                        echo(incomingInfo, byteName);
                        i++;
                        break;
                        
                    default:
                        
                        imageBuffer = new byte[byteLength];
                        System.out.println(imageBuffer.length);
                        DatagramPacket incomingImage;
                        boolean doneSending = false;
                        int aux = 0;
                        
                        while(!doneSending) {    
                            incomingImage = new DatagramPacket(buffer, buffer.length);
                            sock.receive(incomingImage);
                            buffer = incomingImage.getData();
                            while(c < 1024){
                                imageBuffer[aux] = buffer[c];
                                c ++;
                                aux ++;
                                if(aux == (imageBuffer.length - 1)){
                                    doneSending = true;
                                    break;
                                }
                            }
                            c = 0;
                        }       
                        InputStream in = new ByteArrayInputStream(imageBuffer);
			BufferedImage bImageFromConvert = ImageIO.read(in);
 
			ImageIO.write(bImageFromConvert, "jpg", new File(
					"C:/Users/ITIC/Desktop/byteName" ));
                        //BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBuffer));
                        //File f = new File("F:/Programacion/UdpImageReceiver/img/", byteName);
                        //ImageIO.write(img, "PNG", f);
                        done = true;
                }
                
                //DatagramPacket dp = new DatagramPacket(s.getBytes() , s.getBytes().length , incoming.getAddress() , incoming.getPort());
                //sock.send(dp);
            }
        }
        catch(IOException e) {
            System.err.println("IOException " + e);
        }
    }

    //simple function to echo data to terminal
    public static void echo(DatagramPacket incoming, String s) {
        //echo the details of incoming data - client ip : client port - client message
        System.out.println("OK : " + incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
    }
}

