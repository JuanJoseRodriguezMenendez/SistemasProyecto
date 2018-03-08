package client;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

public class Cliente {
	public static void main(String[] args) {
		Socket socket = null;
		PrintStream ps=null;
		Scanner reader = new Scanner(System.in);
		String accion = "";
		try{
			while(!accion.equals("subir") && !accion.equals("descargar")){
				System.out.println("Introduce la accion subir para comenzar:");
				accion = reader.nextLine();
				if(accion.equals("subir")){
					String directorio = "";
					File carpeta = new File(directorio);
					while(!carpeta.exists()){
						System.out.println("Introduce la ruta del directorio");
						directorio = reader.nextLine();
						carpeta = new File(directorio);
					}
					Subidor s = new Subidor(socket,ps,directorio);
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(s,0,Long.valueOf(100000));
				}if(accion.equals("descargar")){
					socket = new Socket("localhost", 6667);
					ps = new PrintStream(socket.getOutputStream());
					ps.print("descargar"+"\r\n"); //segundo mensaje = tarea
					descargar(socket,reader,ps);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			cerrar(ps);
			cerrar(socket);
			cerrar(reader);
		}
	}
	
		
	public static void descargar(Socket socket,Scanner reader,PrintStream ps){
		OutputStream osf = null;
		DataInputStream dis= null;
		try {
			System.out.println("Introduce el nombre del directorio a recuperar");
			String dir = reader.nextLine();
			ps.print(dir+"\r\n");
			ps.flush();
			dis = new DataInputStream(socket.getInputStream());
			String fich = dis.readLine();
			osf = new FileOutputStream(dir);
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			cerrar(osf);
			cerrar(dis);
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
