package server;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import util.HashSHA256;

public class Server {

	public static void main(String[] args) {
		ServerSocket serverSocket= null;
		Socket cliente=null;
		DataInputStream dis = null;
		OutputStream fos=null;
		try {
			serverSocket = new ServerSocket(6667);
			while(true) {
				try{
					cliente = serverSocket.accept();
					dis= new DataInputStream(cliente.getInputStream());
					String peticion =dis.readLine(); //seguno mensaje  = peticion
					System.out.println("La peticion era "+ peticion);
					if(peticion.equals("subir")){
						System.out.println("peticion subir aceptada");
						String tipoYDocu="";
						String nombre="";
						String hash ="";
						String hashs="";
						File dir=null;
						tipoYDocu = dis.readLine(); //segundos mensajes
						while(tipoYDocu!=null){
							System.out.println("TipoYDocumento " + tipoYDocu );
							if(tipoYDocu.startsWith("carpeta")){
								nombre = tipoYDocu.substring(tipoYDocu.indexOf(" ")+1);
								System.out.println("El nombre del directorio recibido es " + nombre);
								dir = new File(nombre);
				    			dir.mkdir();
							}
							if(tipoYDocu.startsWith("archivo")){
								nombre = tipoYDocu.substring(tipoYDocu.indexOf(":")+1,tipoYDocu.indexOf(" "));
								String tama = tipoYDocu.substring(tipoYDocu.indexOf(" ")+1);
								int tamano = Integer.parseInt(tama);
								System.out.println("El nombre del directorio recibido es " + nombre + " El tamaño del archivo recibido es " + tamano);
								dir = new File(nombre);
								fos = new FileOutputStream(dir);
								byte buff[] = new byte[tamano]; 	
								dis.readFully(buff);
								fos.write(buff);
								hash= dis.readLine();
								hashs= HashSHA256.getHash(dir);
								if(hash.equals(hashs)){
									System.out.println("---- Los hash coinciden, copia correcta----");
								}else{
									System.out.println("---- Los hash no coinciden, copia incorrecta----");
								}
							}
							tipoYDocu = dis.readLine(); //segundos mensajes
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally{
					cerrar(dis);
					cerrar(fos);
					cerrar(cliente);
				}
				
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			cerrar(serverSocket);
		}
	}
	
		
	
	public static void cerrar(Closeable o) {
		if(o!=null) {
			try {
				o.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
