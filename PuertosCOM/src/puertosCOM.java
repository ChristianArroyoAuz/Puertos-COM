import javax.comm.UnsupportedCommOperationException;
import java.util.TooManyListenersException;
import javax.comm.SerialPortEventListener;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPortEvent;
import java.util.Enumeration;
import javax.comm.SerialPort;
import java.io.OutputStream;
import javax.comm.CommPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class puertosCOM implements SerialPortEventListener
{
	HashMap<String, CommPortIdentifier> mapaPuertos = new HashMap<String, CommPortIdentifier>();
	CommPortIdentifier identificadorPuertoSeleccionado;
	SerialPort puertoSerial;
	OutputStream salida;
	Enumeration<?> puertos;
	InputStream entrada;
	boolean conectado;
	
	public static void main(String[] args) throws Exception
	{
		puertosCOM puerto = new puertosCOM();
		puerto.buscarPuerto();
		puerto.conectado();
		if (puerto.conectado == true)
		{
			if (puerto.iniciarComunicacion() == true)
			{
				puerto.iniciarEscucha();
				puerto.escribirDatos("Comandos al puerto serial.");
			}
		}
	}
	
	private void parametroPuertoSerial() throws IOException
	{
		int baudRate = 115200;
		try
		{
			puertoSerial.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		}
		catch (UnsupportedCommOperationException ex)
		{
			throw new IOException("Parametros del puerto serial no soportados.");
		}
	}
	
	public void serialEvent(SerialPortEvent evento) {
		if (evento.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			byte[] buffer = new byte[20];
			try {
				while (entrada.available() > 0) {
				}
				System.out.print(new String(buffer));
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
	public void escribirDatos(String enviar)
	{
		try
		{
			salida.write(enviar.getBytes());
		} 
		catch (IOException ex)
		{
			System.out.println("Error al enviar informacion.");
		}
	}
	
	public boolean iniciarComunicacion()
	{
		boolean exito = false;
		try
		{
			entrada = puertoSerial.getInputStream();
			salida = puertoSerial.getOutputStream();
			escribirDatos("HOLA");
			exito = true;
			return exito;
		}
		catch (IOException e)
		{
			System.out.println("Error al abrir el canal.");
			return exito;
		}
	}

	public void iniciarEscucha()
	{
		try
		{
			puertoSerial.addEventListener(this);
			puertoSerial.notifyOnDataAvailable(true);
			System.out.println("Listo.");
		}
		catch (TooManyListenersException e)
		{
			System.out.println("Demasiado en escucha.");
		}
	}
	
	public void buscarPuerto()
	{
		System.out.println("Puertos Disponibles:");
		puertos = CommPortIdentifier.getPortIdentifiers();
		while (puertos.hasMoreElements())
		{
			CommPortIdentifier puerto = (CommPortIdentifier) puertos.nextElement();
			if (puerto.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				System.out.println(puerto.getName());
				mapaPuertos.put(puerto.getName(), puerto);
			}
		}
		System.out.println("--------------");
	}

	public void desconectado()
	{
		try
		{
			escribirDatos("ADIOS");
			puertoSerial.removeEventListener();
			puertoSerial.close();
			entrada.close();
			salida.close();
			conectado = false;
			System.out.println("Desconectado.");
		}
		catch (Exception e)
		{
			System.out.println("Error al desconectar.");
		}
	}
	
	public void conectado()
	{
		String puerto = "COM4";
		identificadorPuertoSeleccionado = (CommPortIdentifier)mapaPuertos.get(puerto);
		CommPort puertoCOM = null;
		try
		{
			puertoCOM = identificadorPuertoSeleccionado.open("Hola", 100);
			puertoSerial = (SerialPort) puertoCOM;
			parametroPuertoSerial();
			conectado = true;
			System.out.println("Conectado exitosamente a puerto " + puerto);
		}
		catch (PortInUseException e)
		{
			System.out.println("Puerto en uso.");
		}
		catch (Exception e)
		{
			System.out.println("Error al abrir puerto.");
		}
	}
}