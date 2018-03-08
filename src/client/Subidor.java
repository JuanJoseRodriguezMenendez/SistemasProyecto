package client;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import util.HashSHA256;

public class Subidor extends TimerTask{

	private Socket socket;
	private PrintStream ps;
	private String directorio;
	
	public Subidor(Socket s,PrintStream p,String d){
		this.socket=s;
		this.ps=p;
		this.directorio=d;
		
	}
	
	public void run() {
		try{
			socket = new Socket("localhost", 6667);
			ps = new PrintStream(socket.getOutputStream());
			ps.print("subir"+"\r\n"); //segundo mensaje = tarea
			ps.flush();
			File carpeta = new File(directorio);
			recorrerDirectorios(carpeta,ps);
			socket.shutdownOutput();			
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			cerrar(ps);
			cerrar(socket);
		}
	} 
	
	

	public void recorrerDirectorios(File carpeta,PrintStream ps) {
		FileInputStream fis = null;
		try{
			ps.print("carpeta " +carpeta.getAbsolutePath()+"\r\n"); //segundos mensajes
			ps.flush();
			for(File f: carpeta.listFiles()) { 
				System.out.println("ruta " + f.getName());
				if(f.isDirectory()){
					ps.print("carpeta " +f.getAbsolutePath()+"\r\n"); //segundos mensajes
					ps.flush();
					recorrerDirectorios(f,ps);
				}if(f.isFile()){
					String extension = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."));
					String ruta = f.getAbsolutePath().substring(0,f.getAbsolutePath().lastIndexOf(".")+1);
					
					fis = new FileInputStream(f);
					ps.print("archivo:" +ruta+extension+" " + f.length() + "\r\n"); //segundos mensajes
					ps.flush();
					byte buff[] = new byte[1024];
					int leidos = fis.read(buff);
					while(leidos != -1){
						ps.write(buff, 0, leidos);
						leidos = fis.read(buff);
					}
					ps.flush();
					ps.println(HashSHA256.getHash(f)+ "\r\n");
					ps.flush();
				}
			}
		}catch (IOException  e) {
			e.printStackTrace();
		}finally{
			cerrar(fis);
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
