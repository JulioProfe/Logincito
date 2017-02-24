package com.example.estudiante.logincito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by estudiante on 24/02/17.
 */

public class Comunicacion extends Observable implements Runnable{

    private static Comunicacion ref;
    //public static final String android = "10.0.2.2";
    //public static final String mip = "227.4.3.2";
    public static final int puerto = 5000;

    private DatagramSocket ds;
    private boolean corre;
    private boolean conec;
    private boolean reset;

    private Comunicacion(){
        corre = true;
        conec = true;
        reset = true;
    }

    public static Comunicacion instancia(){
        if (ref == null){
            ref  = new Comunicacion();
            Thread hilo = new Thread(ref);
            hilo.start();
        }
        return ref;
    }

    public boolean intento(){
        try {
            ds = new DatagramSocket();
            setChanged();
            notifyObservers("Shi");
            clearChanged();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        while (corre){
            if (conec){
                if (reset){
                    if (ds != null){
                        ds.close();
                    }
                    reset = false;
                }
                conec = !intento();
            } else {
                if (ds != null){
                    DatagramPacket paquete = recibir();
                    Object objetoRecibido = deserialize(paquete.getData());
                    if (objetoRecibido != null){
                        if(objetoRecibido instanceof Mensaje) {
                            Mensaje msj = (Mensaje) objetoRecibido;
                            System.out.println("NO LLEGO");
                            setChanged();
                            notifyObservers(msj);
                            clearChanged();
                        }

                    }
                }
            }
        }
        ds.close();
    }

    private byte[] serialize(Object data) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            bytes = baos.toByteArray();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private Object deserialize(byte[] bytes) {
        Object data = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            data = ois.readObject();
            ois.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void enviar(final String mensaje, final String destino, final int puerto){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ds != null){
                    try {
                        InetAddress net = InetAddress.getByName(destino);
                        byte[] data = mensaje.getBytes();
                        DatagramPacket packet = new DatagramPacket(data, data.length, net, puerto);
                        System.out.println("Sending data to " + net.getHostAddress() + ":" + puerto);
                        ds.send(packet);
                        System.out.println("Data was sent");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public DatagramPacket recibir(){
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            ds.receive(packet);
            System.out.println("Data received from " + packet.getAddress() + ":" + packet.getPort());
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
